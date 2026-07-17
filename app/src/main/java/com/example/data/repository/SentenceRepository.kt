package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.database.AppDatabase
import com.example.data.database.AppSettingEntity
import com.example.data.database.SentenceDao
import com.example.data.database.SentenceEntity
import com.example.data.database.UserProgressEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.InputStream

class SentenceRepository(private val sentenceDao: SentenceDao) {

    companion object {
        private const val TAG = "SentenceRepository"
        private const val KEY_DATA_HASH = "data_hash"
        private const val KEY_PREMIUM_UNLOCKED = "premium_unlocked"
    }

    val allSentences: Flow<List<SentenceEntity>> = sentenceDao.getAllSentences()
    val allUserProgress: Flow<List<UserProgressEntity>> = sentenceDao.getAllUserProgress()
    val allSettings: Flow<List<AppSettingEntity>> = sentenceDao.getAllSettings()

    fun getSentencesByLesson(lessonId: Int): Flow<List<SentenceEntity>> {
        return sentenceDao.getSentencesByLesson(lessonId)
    }

    suspend fun getProgressForSentence(sentenceId: Int): UserProgressEntity? {
        return sentenceDao.getProgressForSentence(sentenceId)
    }

    suspend fun updateProgress(progress: UserProgressEntity) {
        sentenceDao.insertProgress(progress)
    }

    suspend fun isPremiumUnlocked(): Boolean {
        return sentenceDao.getSetting(KEY_PREMIUM_UNLOCKED)?.value == "true"
    }

    suspend fun setPremiumUnlocked(unlocked: Boolean) {
        sentenceDao.insertSetting(AppSettingEntity(KEY_PREMIUM_UNLOCKED, unlocked.toString()))
    }

    suspend fun getSetting(key: String): String? {
        return sentenceDao.getSetting(key)?.value
    }

    suspend fun saveSetting(key: String, value: String) {
        sentenceDao.insertSetting(AppSettingEntity(key, value))
    }

    /**
     * Checks if the data.cv asset file changed since the last import.
     * If so, clears and imports the latest sentences into the SQLite database.
     */
    suspend fun checkAndSyncData(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            val jsonString = readAssetFile(context, "data.cv") ?: return@withContext false
            val fileHash = jsonString.hashCode().toString()
            
            val storedHashSetting = sentenceDao.getSetting(KEY_DATA_HASH)
            val storedHash = storedHashSetting?.value

            if (storedHash != fileHash) {
                Log.d(TAG, "Data changed or not imported yet. Stored hash: $storedHash, new hash: $fileHash. Starting sync...")
                
                // Parse the JSON array
                val jsonArray = JSONArray(jsonString)
                val sentencesList = ArrayList<SentenceEntity>()
                
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val id = item.getInt("id")
                    val sentence = item.getString("sentence")
                    val translation = item.getString("translation")
                    val image = item.optString("image", "default")
                    val audio = item.optString("audio", "default.mp3")
                    
                    // Assign lessonId: 7 sentences per lesson
                    val lessonId = ((id - 1) / 7) + 1
                    
                    sentencesList.add(
                        SentenceEntity(
                            id = id,
                            sentence = sentence,
                            translation = translation,
                            image = image,
                            audio = audio,
                            lessonId = lessonId
                        )
                    )
                }

                // Insert into database
                if (sentencesList.isNotEmpty()) {
                    sentenceDao.insertSentences(sentencesList)
                    sentenceDao.insertSetting(AppSettingEntity(KEY_DATA_HASH, fileHash))
                    Log.d(TAG, "Successfully synced ${sentencesList.size} sentences into local database.")
                    return@withContext true
                }
            } else {
                Log.d(TAG, "Database is already up to date. Hash: $storedHash")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing sentences: ", e)
        }
        return@withContext false
    }

    private fun readAssetFile(context: Context, fileName: String): String? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read asset file $fileName", e)
            null
        }
    }
}
