package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibrationEffect
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.billing.MyketBillingManager
import com.example.data.database.AppDatabase
import com.example.data.database.SentenceEntity
import com.example.data.database.UserProgressEntity
import com.example.data.repository.SentenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

// Simple representation of the screens
sealed class AppScreen {
    object Home : AppScreen()
    data class CategoryLessons(val categoryId: Int) : AppScreen()
    data class Learning(val lessonId: Int) : AppScreen()
    data class Quiz(val lessonId: Int) : AppScreen()
    object Settings : AppScreen()
    object SpeakingQuiz : AppScreen()
    object Guide : AppScreen()
}

// Lesson session data model
data class LessonSession(
    val id: Int,
    val title: String,
    val subtitle: String,
    val isFree: Boolean,
    val isLocked: Boolean,
    val totalSentencesCount: Int = 7,
    val completedCount: Int = 0,
    val isCompleted: Boolean = false,
    val isSequenceLocked: Boolean = false,
    val isQuizPerfect: Boolean = false,
    val isSpeakingPerfect: Boolean = false
)

// Category session data model
data class CategorySession(
    val id: Int,
    val title: String,
    val subtitle: String,
    val completedSentencesCount: Int,
    val totalSentencesCount: Int = 35,
    val isCompleted: Boolean,
    val lessons: List<LessonSession>
)

enum class QuestionType {
    MULTIPLE_CHOICE_FA_TO_EN,
    MULTIPLE_CHOICE_EN_TO_FA,
    FILL_IN_BLANK
}

data class QuizQuestion(
    val id: Int,
    val type: QuestionType,
    val questionText: String,       // e.g. English sentence or Persian translation
    val translationHint: String,   // Helper translation in Persian if question is in English
    val correctAnswer: String,
    val options: List<String>,
    val sentence: SentenceEntity,
    val blankWord: String = ""      // Only for FILL_IN_BLANK
)

class LearningViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository: SentenceRepository
    val billingManager: MyketBillingManager

    // Screen State Navigation
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen

    // Master list of sentences from DB
    val allSentences: StateFlow<List<SentenceEntity>>

    // User progress from DB
    val allUserProgress: StateFlow<List<UserProgressEntity>>

    // Dynamic lessons compilation state
    val lessons: StateFlow<List<LessonSession>>

    // Dynamic categories compilation state
    val categories: StateFlow<List<CategorySession>>

    // Active learning session state
    private val _activeLessonId = MutableStateFlow<Int>(1)
    val activeLessonId: StateFlow<Int> = _activeLessonId

    private val _learningSentences = MutableStateFlow<List<SentenceEntity>>(emptyList())
    val learningSentences: StateFlow<List<SentenceEntity>> = _learningSentences

    private val _currentSentenceIndex = MutableStateFlow<Int>(0)
    val currentSentenceIndex: StateFlow<Int> = _currentSentenceIndex

    // Active quiz session state
    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions

    private val _currentQuestionIndex = MutableStateFlow<Int>(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex

    private val _quizCorrectAnswersCount = MutableStateFlow<Int>(0)
    val quizCorrectAnswersCount: StateFlow<Int> = _quizCorrectAnswersCount

    private val _quizWrongAnswersCount = MutableStateFlow<Int>(0)
    val quizWrongAnswersCount: StateFlow<Int> = _quizWrongAnswersCount

    private val _isQuizFinished = MutableStateFlow<Boolean>(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished

    private val _selectedQuizOption = MutableStateFlow<String?>(null)
    val selectedQuizOption: StateFlow<String?> = _selectedQuizOption

    // Shared Flow to signal Haptic/Vibration Events to UI
    private val _vibrationEvents = MutableSharedFlow<Unit>()
    val vibrationEvents: SharedFlow<Unit> = _vibrationEvents.asSharedFlow()

    // Offline Text to Speech Engine
    private var tts: TextToSpeech? = null
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady

    // Last learned lesson bookmark index for "ادامه یادگیری" (Resume learning)
    private val _lastLearnedLessonId = MutableStateFlow<Int>(1)
    val lastLearnedLessonId: StateFlow<Int> = _lastLearnedLessonId

    // Selected Color Theme Preset (e.g. indigo, emerald, ocean, rose, sunset, amethyst)
    private val _selectedTheme = MutableStateFlow<String>("indigo")
    val selectedTheme: StateFlow<String> = _selectedTheme

    // Theme Mode Selection (system, light, dark)
    private val _themeMode = MutableStateFlow<String>("system")
    val themeMode: StateFlow<String> = _themeMode

    // Categories Layout Mode Selection (list, grid)
    private val _categoriesLayoutMode = MutableStateFlow<String>("list")
    val categoriesLayoutMode: StateFlow<String> = _categoriesLayoutMode

    // Quiz statistics
    private val _quizAttemptsCount = MutableStateFlow<Int>(0)
    val quizAttemptsCount: StateFlow<Int> = _quizAttemptsCount

    private val _quizSuccessCount = MutableStateFlow<Int>(0)
    val quizSuccessCount: StateFlow<Int> = _quizSuccessCount

    private val _quizFailCount = MutableStateFlow<Int>(0)
    val quizFailCount: StateFlow<Int> = _quizFailCount

    // Daily study reminder settings
    private val _reminderEnabled = MutableStateFlow<Boolean>(true)
    val reminderEnabled: StateFlow<Boolean> = _reminderEnabled

    private val _reminderHour = MutableStateFlow<Int>(17)
    val reminderHour: StateFlow<Int> = _reminderHour

    private val _reminderMinute = MutableStateFlow<Int>(0)
    val reminderMinute: StateFlow<Int> = _reminderMinute

    // Conditional-free learning setting ("آموزش بدون حالت شرط")
    private val _conditionalFreeLearning = MutableStateFlow<Boolean>(false)
    val conditionalFreeLearning: StateFlow<Boolean> = _conditionalFreeLearning

    // Background Customization setting (none, pattern_1, pattern_2, pattern_3, pattern_4, pattern_5, custom)
    private val _backgroundType = MutableStateFlow<String>("none")
    val backgroundType: StateFlow<String> = _backgroundType

    private val _customBackgroundUri = MutableStateFlow<String>("")
    val customBackgroundUri: StateFlow<String> = _customBackgroundUri

    // Onboarding tutorial completed state
    private val _onboardingCompleted = MutableStateFlow<Boolean>(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted

    // Settings loaded state to prevent UI flickers on startup
    private val _isSettingsLoaded = MutableStateFlow<Boolean>(false)
    val isSettingsLoaded: StateFlow<Boolean> = _isSettingsLoaded

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SentenceRepository(database.sentenceDao())
        billingManager = MyketBillingManager(application, repository, viewModelScope)

        // Load saved theme, quiz, and reminder configuration on launch
        viewModelScope.launch {
            _selectedTheme.value = repository.getSetting("theme_preset") ?: "indigo"
            _themeMode.value = repository.getSetting("theme_mode") ?: "system"
            _categoriesLayoutMode.value = repository.getSetting("categories_layout_mode") ?: "list"
            _backgroundType.value = repository.getSetting("background_type") ?: "none"
            _customBackgroundUri.value = repository.getSetting("custom_background_uri") ?: ""
            _onboardingCompleted.value = repository.getSetting("onboarding_completed")?.toBooleanStrictOrNull() ?: false

            _quizAttemptsCount.value = repository.getSetting("quiz_attempts_count")?.toIntOrNull() ?: 0
            _quizSuccessCount.value = repository.getSetting("quiz_success_count")?.toIntOrNull() ?: 0
            _quizFailCount.value = repository.getSetting("quiz_fail_count")?.toIntOrNull() ?: 0

            _reminderEnabled.value = repository.getSetting("reminder_enabled")?.toBooleanStrictOrNull() ?: true
            _reminderHour.value = repository.getSetting("reminder_hour")?.toIntOrNull() ?: 17
            _reminderMinute.value = repository.getSetting("reminder_minute")?.toIntOrNull() ?: 0
            _conditionalFreeLearning.value = repository.getSetting("conditional_free_learning")?.toBooleanStrictOrNull() ?: false

            updateScheduledReminder()
            _isSettingsLoaded.value = true
        }

        allSentences = repository.allSentences.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allUserProgress = repository.allUserProgress.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Combine sentences, progress, and settings dynamically to build lessons session states
        lessons = combine(
            allSentences, 
            allUserProgress, 
            repository.allSettings,
            billingManager.isPremiumUnlocked
        ) { sentencesList: List<SentenceEntity>, progressList: List<UserProgressEntity>, settingsList: List<com.example.data.database.AppSettingEntity>, isPremium: Boolean ->
            val progressMap = progressList.associateBy { it.sentenceId }
            val settingsMap = settingsList.associateBy { it.key }
            val isConditionalFree = settingsMap["conditional_free_learning"]?.value == "true"
            val categoriesList = listOf(
                "احوالپرسی و ارتباط اولیه",
                "معرفی خود و دیگران",
                "خانواده و روابط شخصی",
                "زمان و تاریخ",
                "آب و هوا و فصول",
                "خانه و محیط زندگی",
                "غذا، نوشیدنی و رستوران",
                "خرید و امور مالی",
                "کار، شغل و دفتر",
                "تحصیلات، دانشگاه و مدرسه",
                "سفر، هتل و ترابری",
                "جهت‌یابی و آدرس دادن",
                "تفریح، سرگرمی و ورزش",
                "سلامتی، پزشکی و بهداشت",
                "موقعیت‌های اضطراری",
                "احساسات، عواطف و نظرات",
                "تکنولوژی، اینترنت و موبایل",
                "هنر، موسیقی و رسانه",
                "طبیعت، گیاهان و جانوران",
                "توصیف ظاهر و شخصیت",
                "مکالمات رسمی و اداری"
            )
            
            val list = mutableListOf<LessonSession>()
            var previousLessonCompleted = true // Lesson 1 is always unlocked since there's no previous
            
            // OPTIMIZATION: Group sentences by lessonId beforehand to avoid 105 iterations (O(N) instead of O(N * L))
            val sentencesByLesson = sentencesList.groupBy { it.lessonId }
            
            for (lessonId in 1..105) {
                val catId = ((lessonId - 1) / 5) + 1
                val catName = categoriesList.getOrNull(catId - 1) ?: "دسته بندی $catId"
                val subLessonIndex = ((lessonId - 1) % 5) + 1
                val title = "آموزش $subLessonIndex از ۵ ($catName)"
                val isFree = lessonId <= 5 // First category is completely free, others locked under premium
                
                val lessonSentences = sentencesByLesson[lessonId] ?: emptyList()
                val completedCount = lessonSentences.count { progressMap[it.id]?.isCompleted == true }
                
                val isQuizPerfect = settingsMap["lesson_${lessonId}_quiz_perfect"]?.value == "true"
                val isSpeakingPerfect = settingsMap["lesson_${lessonId}_speaking_perfect"]?.value == "true"
                
                val isCompleted = completedCount >= 7 && lessonSentences.isNotEmpty() && isQuizPerfect && isSpeakingPerfect
                
                // Unlocked if previous lesson is completed (except first lesson)
                val isSequenceLocked = if (isConditionalFree) false else !previousLessonCompleted
                val isLocked = if (isConditionalFree) false else ((!isFree && !isPremium) || isSequenceLocked)
                
                list.add(
                    LessonSession(
                        id = lessonId,
                        title = title,
                        subtitle = "جملات ${(lessonId - 1) * 7 + 1} تا ${lessonId * 7}",
                        isFree = isFree,
                        isLocked = isLocked,
                        totalSentencesCount = 7,
                        completedCount = completedCount,
                        isCompleted = isCompleted || isConditionalFree,
                        isSequenceLocked = isSequenceLocked,
                        isQuizPerfect = isQuizPerfect || isConditionalFree,
                        isSpeakingPerfect = isSpeakingPerfect || isConditionalFree
                    )
                )
                
                previousLessonCompleted = if (isConditionalFree) true else isCompleted
            }
            list
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Combine lessons to construct categories dynamically
        categories = lessons.map { lessonsList: List<LessonSession> ->
            val categoriesList = listOf(
                "احوالپرسی و ارتباط اولیه",
                "معرفی خود و دیگران",
                "خانواده و روابط شخصی",
                "زمان و تاریخ",
                "آب و هوا و فصول",
                "خانه و محیط زندگی",
                "غذا، نوشیدنی و رستوران",
                "خرید و امور مالی",
                "کار، شغل و دفتر",
                "تحصیلات، دانشگاه و مدرسه",
                "سفر، هتل و ترابری",
                "جهت‌یابی و آدرس دادن",
                "تفریح، سرگرمی و ورزش",
                "سلامتی، پزشکی و بهداشت",
                "موقعیت‌های اضطراری",
                "احساسات، عواطف و نظرات",
                "تکنولوژی، اینترنت و موبایل",
                "هنر، موسیقی و رسانه",
                "طبیعت، گیاهان و جانوران",
                "توصیف ظاهر و شخصیت",
                "مکالمات رسمی و اداری"
            )
            
            // OPTIMIZATION: Group lessons by categoryId beforehand to avoid repetitive filtering inside the map
            val lessonsByCategory = lessonsList.groupBy { ((it.id - 1) / 5) + 1 }
            
            categoriesList.mapIndexed { index, catName ->
                val catId = index + 1
                val catLessons = lessonsByCategory[catId] ?: emptyList()
                val completedSentences = catLessons.sumOf { it.completedCount }
                val totalSentences = catLessons.sumOf { it.totalSentencesCount }
                
                CategorySession(
                    id = catId,
                    title = catName,
                    subtitle = "شامل ۵ آموزش کاربردی",
                    completedSentencesCount = completedSentences,
                    totalSentencesCount = if (totalSentences > 0) totalSentences else 35,
                    isCompleted = catLessons.isNotEmpty() && catLessons.all { it.isCompleted },
                    lessons = catLessons
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Observe progress to update the last learned session bookmark dynamically
        viewModelScope.launch {
            allUserProgress.collect { progressList ->
                if (progressList.isNotEmpty()) {
                    val maxCompletedSentenceId = progressList.filter { it.isCompleted }.maxOfOrNull { it.sentenceId } ?: 0
                    val estimatedLessonId = ((maxCompletedSentenceId) / 7) + 1
                    _lastLearnedLessonId.value = estimatedLessonId.coerceIn(1, 105)
                }
            }
        }

        // Initialize TTS
        tts = TextToSpeech(application, this)

        // Initialize and sync sentences from data.cv inside assets folder on thread pool
        viewModelScope.launch(Dispatchers.IO) {
            val syncTriggered = repository.checkAndSyncData(application)
            Log.d("LearningViewModel", "Database synced on launch: $syncTriggered")
            
            // Connect Myket IAP Service
            billingManager.startConnection {
                Log.d("LearningViewModel", "Myket Billing Service Connected")
            }
        }
    }

    private fun createLessonSession(
        lessonId: Int,
        title: String,
        sentences: List<SentenceEntity>,
        progressMap: Map<Int, UserProgressEntity>,
        isPremium: Boolean,
        isFreeByDefault: Boolean
    ): LessonSession {
        val lessonSentences = sentences.filter { it.lessonId == lessonId }
        val completedCount = lessonSentences.count { progressMap[it.id]?.isCompleted == true }
        
        return LessonSession(
            id = lessonId,
            title = title,
            subtitle = "جملات ${(lessonId - 1) * 7 + 1} تا ${lessonId * 7}",
            isFree = isFreeByDefault,
            isLocked = !isFreeByDefault && !isPremium,
            totalSentencesCount = 7,
            completedCount = completedCount,
            isCompleted = completedCount >= 7 && lessonSentences.isNotEmpty(),
            isSequenceLocked = false
        )
    }

    // TTS initialisation listener
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not available or not supported.")
            } else {
                _isTtsReady.value = true
                Log.d("TTS", "English TTS engine successfully initialized offline.")
            }
        } else {
            Log.e("TTS", "Failed to initialize TextToSpeech.")
        }
    }

    private var lastSpokenText: String? = null
    private var lastSpeechRate: Float = 1.0f

    /**
     * Pronounces the English sentence aloud offline using Native TTS Engine.
     * Alternates between normal speed on first click and slower speed on second click.
     * Only alphanumeric characters and spaces are spoken.
     */
    fun speakEnglish(text: String) {
        if (_isTtsReady.value) {
            val cleanedText = text.replace(Regex("[^a-zA-Z0-9\\s]"), "").trim()
            if (cleanedText.isEmpty()) return

            val rate = if (cleanedText == lastSpokenText) {
                if (lastSpeechRate == 1.0f) 0.35f else 1.0f
            } else {
                1.0f
            }
            lastSpokenText = cleanedText
            lastSpeechRate = rate
            
            tts?.setSpeechRate(rate)
            tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.w("TTS", "TTS engine is not ready yet.")
        }
    }

    /**
     * Set the current active screen
     */
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    /**
     * Starts learning mode for a specific lesson
     */
    fun startLearning(lessonId: Int) {
        viewModelScope.launch {
            _activeLessonId.value = lessonId
            val sentences = repository.getSentencesByLesson(lessonId).first()
            _learningSentences.value = sentences
            
            // Start at the first uncompleted sentence in this lesson, or 0 if all done
            val progressList = allUserProgress.value.associateBy { it.sentenceId }
            val firstUncompletedIndex = sentences.indexOfFirst { progressMapItem ->
                progressList[progressMapItem.id]?.isCompleted != true
            }
            
            _currentSentenceIndex.value = if (firstUncompletedIndex != -1) firstUncompletedIndex else 0
            navigateTo(AppScreen.Learning(lessonId))
        }
    }

    /**
     * Starts learning mode and opens a specific sentence directly by its ID
     */
    fun startLearningAtSentence(lessonId: Int, sentenceId: Int) {
        viewModelScope.launch {
            _activeLessonId.value = lessonId
            val sentences = repository.getSentencesByLesson(lessonId).first()
            _learningSentences.value = sentences
            
            val index = sentences.indexOfFirst { it.id == sentenceId }
            _currentSentenceIndex.value = if (index != -1) index else 0
            navigateTo(AppScreen.Learning(lessonId))
        }
    }

    /**
     * Mark current sentence as completed and go to next or trigger completion state
     */
    fun completeCurrentSentence() {
        val currentSentencesList = _learningSentences.value
        val currentIndex = _currentSentenceIndex.value
        
        if (currentIndex < currentSentencesList.size) {
            val sentence = currentSentencesList[currentIndex]
            viewModelScope.launch(Dispatchers.IO) {
                // Fetch existing progress
                val existingProgress = repository.getProgressForSentence(sentence.id)
                val updatedProgress = existingProgress?.copy(
                    isCompleted = true,
                    correctCount = existingProgress.correctCount + 1,
                    lastAttemptTime = System.currentTimeMillis()
                ) ?: UserProgressEntity(
                    sentenceId = sentence.id,
                    isCompleted = true,
                    correctCount = 1
                )
                repository.updateProgress(updatedProgress)
                
                // Navigate forward
                withContext(Dispatchers.Main) {
                    if (currentIndex < currentSentencesList.size - 1) {
                        _currentSentenceIndex.value = currentIndex + 1
                    } else {
                        // Lesson finished, navigate directly to dynamic quiz!
                        startQuiz(sentence.lessonId)
                    }
                }
            }
        }
    }

    /**
     * Increments correct answer count for the current sentence without skipping yet (useful for custom review)
     */
    fun markSentenceWrong() {
        val currentSentencesList = _learningSentences.value
        val currentIndex = _currentSentenceIndex.value
        
        if (currentIndex < currentSentencesList.size) {
            val sentence = currentSentencesList[currentIndex]
            viewModelScope.launch(Dispatchers.IO) {
                val existingProgress = repository.getProgressForSentence(sentence.id)
                val updatedProgress = existingProgress?.copy(
                    incorrectCount = existingProgress.incorrectCount + 1,
                    lastAttemptTime = System.currentTimeMillis()
                ) ?: UserProgressEntity(
                    sentenceId = sentence.id,
                    incorrectCount = 1
                )
                repository.updateProgress(updatedProgress)
                triggerVibration()
            }
        }
    }

    fun navigateLearningPrevious() {
        if (_currentSentenceIndex.value > 0) {
            _currentSentenceIndex.value = _currentSentenceIndex.value - 1
        }
    }

    fun navigateLearningNext() {
        if (_currentSentenceIndex.value < _learningSentences.value.size - 1) {
            _currentSentenceIndex.value = _currentSentenceIndex.value + 1
        } else {
            // End of lesson, go to Quiz!
            startQuiz(_activeLessonId.value)
        }
    }

    /**
     * Generates a 10-question spaced-repetition quiz for the selected lesson.
     */
    fun startQuiz(lessonId: Int) {
        viewModelScope.launch {
            _activeLessonId.value = lessonId
            val allList = allSentences.value
            val currentLessonSentences = allList.filter { it.lessonId == lessonId }
            if (currentLessonSentences.isEmpty()) return@launch

            // Spaced Repetition logic:
            // 7 questions from the current lesson.
            // 3 questions from previous lessons (or random of current if no previous lessons exist).
            val previousLessonsSentences = allList.filter { it.lessonId < lessonId }
            
            val currentSelected = currentLessonSentences.shuffled().take(7)
            val previousSelected = if (previousLessonsSentences.isNotEmpty()) {
                previousLessonsSentences.shuffled().take(3)
            } else {
                currentLessonSentences.shuffled().filter { !currentSelected.contains(it) }.take(3)
            }
            
            val combinedSentences = (currentSelected + previousSelected).shuffled()
            val questions = combinedSentences.map { sentence ->
                generateQuestionForSentence(sentence, allList)
            }

            _quizQuestions.value = questions
            _currentQuestionIndex.value = 0
            _quizCorrectAnswersCount.value = 0
            _quizWrongAnswersCount.value = 0
            _selectedQuizOption.value = null
            _isQuizFinished.value = false
            navigateTo(AppScreen.Quiz(lessonId))
        }
    }

    private fun generateQuestionForSentence(sentence: SentenceEntity, pool: List<SentenceEntity> = emptyList()): QuizQuestion {
        // Flat list pool
        val flatPool = pool.ifEmpty { allSentences.value }
        
        // Randomly pick a question type
        val type = when ((1..3).random()) {
            1 -> QuestionType.MULTIPLE_CHOICE_FA_TO_EN
            2 -> QuestionType.MULTIPLE_CHOICE_EN_TO_FA
            else -> QuestionType.FILL_IN_BLANK
        }

        return when (type) {
            QuestionType.MULTIPLE_CHOICE_FA_TO_EN -> {
                val correctAnswer = sentence.sentence
                val distractors = flatPool
                    .filter { it.id != sentence.id }
                    .shuffled()
                    .take(3)
                    .map { it.sentence }
                val options = (distractors + correctAnswer).shuffled()
                
                QuizQuestion(
                    id = sentence.id,
                    type = type,
                    questionText = sentence.translation, // Show Persian
                    translationHint = "گزینه انگلیسی درست را انتخاب کنید:",
                    correctAnswer = correctAnswer,
                    options = options,
                    sentence = sentence
                )
            }
            QuestionType.MULTIPLE_CHOICE_EN_TO_FA -> {
                val correctAnswer = sentence.translation
                val distractors = flatPool
                    .filter { it.id != sentence.id }
                    .shuffled()
                    .take(3)
                    .map { it.translation }
                val options = (distractors + correctAnswer).shuffled()
                
                QuizQuestion(
                    id = sentence.id,
                    type = type,
                    questionText = sentence.sentence, // Show English
                    translationHint = "معنی فارسی صحیح را انتخاب کنید:",
                    correctAnswer = correctAnswer,
                    options = options,
                    sentence = sentence
                )
            }
            QuestionType.FILL_IN_BLANK -> {
                // Find a word inside English sentence to replace with blank.
                // Choose a meaningful word, usually one that is 4+ characters, or fallback to the last word.
                val words = sentence.sentence.trim().split(" ")
                val cleanWords = words.map { it.replace(Regex("[?,.!]"), "") }
                
                // Try to find a good word to blank out
                var targetWordIndex = cleanWords.indexOfFirst { it.length > 3 }
                if (targetWordIndex == -1) targetWordIndex = words.size - 1
                
                val blankWord = cleanWords[targetWordIndex]
                val displayedSentence = words.mapIndexed { idx, word ->
                    if (idx == targetWordIndex) "___" else word
                }.joinToString(" ")

                val correctAnswer = blankWord
                // Collect other words from the same sentence or other random sentences as choices
                val distractors = listOf("is", "are", "have", "you", "good", "from", "do", "what", "like")
                    .filter { it.lowercase() != correctAnswer.lowercase() }
                    .shuffled()
                    .take(3)
                
                val options = (distractors + correctAnswer).shuffled()

                QuizQuestion(
                    id = sentence.id,
                    type = type,
                    questionText = displayedSentence, // Show English with ___
                    translationHint = sentence.translation, // Show Persian as helper
                    correctAnswer = correctAnswer,
                    options = options,
                    sentence = sentence,
                    blankWord = blankWord
                )
            }
        }
    }

    /**
     * Submit an answer for the current quiz question
     */
    fun submitQuizAnswer(selectedOption: String) {
        val questions = _quizQuestions.value
        val currentIndex = _currentQuestionIndex.value
        if (currentIndex >= questions.size) return
        if (_selectedQuizOption.value != null) return // Prevent duplicate selection/fast-clicking during delay

        _selectedQuizOption.value = selectedOption
        val question = questions[currentIndex]
        val isCorrect = selectedOption.lowercase().trim() == question.correctAnswer.lowercase().trim()

        viewModelScope.launch(Dispatchers.IO) {
            // Save quiz attempt stats in local DB
            val existingProgress = repository.getProgressForSentence(question.sentence.id)
            val updatedProgress = existingProgress?.copy(
                correctCount = existingProgress.correctCount + (if (isCorrect) 1 else 0),
                incorrectCount = existingProgress.incorrectCount + (if (isCorrect) 0 else 1),
                lastAttemptTime = System.currentTimeMillis()
            ) ?: UserProgressEntity(
                sentenceId = question.sentence.id,
                correctCount = if (isCorrect) 1 else 0,
                incorrectCount = if (isCorrect) 0 else 1
            )
            repository.updateProgress(updatedProgress)

            withContext(Dispatchers.Main) {
                if (isCorrect) {
                    _quizCorrectAnswersCount.value = _quizCorrectAnswersCount.value + 1
                } else {
                    _quizWrongAnswersCount.value = _quizWrongAnswersCount.value + 1
                    triggerVibration()
                }
                
                // Keep the color highlighted for 1.6 seconds, then reset selection and advance
                kotlinx.coroutines.delay(1600)
                _selectedQuizOption.value = null
                advanceQuiz()
            }
        }
    }

    private fun advanceQuiz() {
        val nextIdx = _currentQuestionIndex.value + 1
        if (nextIdx < _quizQuestions.value.size) {
            _currentQuestionIndex.value = nextIdx
        } else {
            _isQuizFinished.value = true
            recordQuizCompletion()
        }
    }

    private fun recordQuizCompletion() {
        viewModelScope.launch {
            val total = _quizAttemptsCount.value + 1
            _quizAttemptsCount.value = total
            repository.saveSetting("quiz_attempts_count", total.toString())

            val correct = _quizCorrectAnswersCount.value
            val isSuccess = correct >= 6 // 60% or more (6 out of 10) is successful
            if (isSuccess) {
                val success = _quizSuccessCount.value + 1
                _quizSuccessCount.value = success
                repository.saveSetting("quiz_success_count", success.toString())
            } else {
                val fail = _quizFailCount.value + 1
                _quizFailCount.value = fail
                repository.saveSetting("quiz_fail_count", fail.toString())
            }

            // Save perfect score for the active lesson if got 100% (all questions correct)
            val activeId = _activeLessonId.value
            if (activeId != 0 && correct == _quizQuestions.value.size) {
                repository.saveSetting("lesson_${activeId}_quiz_perfect", "true")
            }
        }
    }

    fun updateScheduledReminder() {
        val enabled = _reminderEnabled.value
        val hour = _reminderHour.value
        val minute = _reminderMinute.value
        val context = getApplication<Application>()
        if (enabled) {
            com.example.ui.reminder.ReminderScheduler.scheduleDailyReminder(context, hour, minute)
        } else {
            com.example.ui.reminder.ReminderScheduler.cancelReminder(context)
        }
    }

    fun updateReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _reminderEnabled.value = enabled
            repository.saveSetting("reminder_enabled", enabled.toString())
            updateScheduledReminder()
        }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            _reminderHour.value = hour
            _reminderMinute.value = minute
            repository.saveSetting("reminder_hour", hour.toString())
            repository.saveSetting("reminder_minute", minute.toString())
            updateScheduledReminder()
        }
    }

    fun updateThemePreset(preset: String) {
        viewModelScope.launch {
            _selectedTheme.value = preset
            repository.saveSetting("theme_preset", preset)
        }
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            _themeMode.value = mode
            repository.saveSetting("theme_mode", mode)
        }
    }

    fun updateCategoriesLayoutMode(mode: String) {
        viewModelScope.launch {
            _categoriesLayoutMode.value = mode
            repository.saveSetting("categories_layout_mode", mode)
        }
    }

    fun updateConditionalFreeLearning(enabled: Boolean) {
        viewModelScope.launch {
            _conditionalFreeLearning.value = enabled
            repository.saveSetting("conditional_free_learning", enabled.toString())
        }
    }

    fun updateBackgroundType(type: String) {
        viewModelScope.launch {
            _backgroundType.value = type
            repository.saveSetting("background_type", type)
        }
    }

    fun updateCustomBackgroundUri(uri: String) {
        viewModelScope.launch {
            _customBackgroundUri.value = uri
            repository.saveSetting("custom_background_uri", uri)
        }
    }

    fun updateOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch {
            _onboardingCompleted.value = completed
            repository.saveSetting("onboarding_completed", completed.toString())
        }
    }

    /**
     * Starts a comprehensive smart quiz up to the user's learned lessons,
     * ensuring that questions do not repeat across consecutive attempts (at least 10 attempts).
     */
    fun startSmartQuiz() {
        viewModelScope.launch {
            // Determine pool of studied sentences based on lastLearnedLessonId
            val maxLessonId = _lastLearnedLessonId.value.coerceIn(1, 3)
            val allList = allSentences.value
            val studiedSentences = allList.filter { it.lessonId <= maxLessonId }
            if (studiedSentences.isEmpty()) return@launch

            // Load recently used sentence IDs from settings to avoid repetitions
            val recentStr = repository.getSetting("recent_quiz_sentence_ids") ?: ""
            val recentIds = recentStr.split(",")
                .filter { it.isNotEmpty() }
                .mapNotNull { it.toIntOrNull() }
                .toMutableList()

            // Filter studied sentences into available (unused) and used
            val available = studiedSentences.filter { !recentIds.contains(it.id) }
            val used = studiedSentences.filter { recentIds.contains(it.id) }

            val selectedSentences = mutableListOf<SentenceEntity>()
            if (available.size >= 10) {
                // If we have enough unused sentences, take 10 random ones
                selectedSentences.addAll(available.shuffled().take(10))
            } else {
                // If we have fewer than 10 unused sentences, take all of them
                selectedSentences.addAll(available)
                // And fill the rest with used sentences, prioritizing the ones asked longest ago
                val needed = 10 - selectedSentences.size
                val sortedUsed = used.sortedBy { sentence ->
                    val index = recentIds.indexOf(sentence.id)
                    if (index != -1) index else Int.MAX_VALUE
                }
                selectedSentences.addAll(sortedUsed.take(needed))
            }

            // Shuffle selected sentences to make the quiz random
            val finalCombined = selectedSentences.shuffled()

            // Map to quiz questions
            val questions = finalCombined.map { sentence ->
                generateQuestionForSentence(sentence, allList)
            }

            // Update recently used sentence IDs history
            val newRecentIds = recentIds.filter { id -> !finalCombined.any { it.id == id } }.toMutableList()
            newRecentIds.addAll(finalCombined.map { it.id })
            
            // Limit history size (e.g., last 80 IDs, or just keep it bounded)
            if (newRecentIds.size > 80) {
                while (newRecentIds.size > 80) {
                    newRecentIds.removeAt(0)
                }
            }
            
            val newRecentStr = newRecentIds.joinToString(",")
            repository.saveSetting("recent_quiz_sentence_ids", newRecentStr)

            // Start Quiz state
            _activeLessonId.value = 0 // represents general comprehensive quiz!
            _quizQuestions.value = questions
            _currentQuestionIndex.value = 0
            _quizCorrectAnswersCount.value = 0
            _quizWrongAnswersCount.value = 0
            _selectedQuizOption.value = null
            _isQuizFinished.value = false
            navigateTo(AppScreen.Quiz(0))
        }
    }

    /**
     * Triggers hardware tactile vibration on failure.
     */
    fun triggerVibration() {
        viewModelScope.launch {
            _vibrationEvents.emit(Unit)
        }
        
        // Directly trigger system vibrator from Android context as fallback
        try {
            val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(200)
                }
            }
        } catch (e: Exception) {
            Log.e("Vibration", "Could not trigger hardware vibration: ", e)
        }
    }

    // ==========================================
    // SPEAKING QUIZ SESSION STATES & LOGIC
    // ==========================================
    private val _speakingQuizSentences = MutableStateFlow<List<SentenceEntity>>(emptyList())
    val speakingQuizSentences: StateFlow<List<SentenceEntity>> = _speakingQuizSentences

    private val _currentSpeakingIndex = MutableStateFlow<Int>(0)
    val currentSpeakingIndex: StateFlow<Int> = _currentSpeakingIndex

    private val _speakingRecognizedText = MutableStateFlow<String>("")
    val speakingRecognizedText: StateFlow<String> = _speakingRecognizedText

    private val _isSpeakingListening = MutableStateFlow<Boolean>(false)
    val isSpeakingListening: StateFlow<Boolean> = _isSpeakingListening

    private val _speakingQuizCorrectCount = MutableStateFlow<Int>(0)
    val speakingQuizCorrectCount: StateFlow<Int> = _speakingQuizCorrectCount

    private val _isSpeakingResultChecked = MutableStateFlow<Boolean>(false)
    val isSpeakingResultChecked: StateFlow<Boolean> = _isSpeakingResultChecked

    private val _isSpeakingResultCorrect = MutableStateFlow<Boolean>(false)
    val isSpeakingResultCorrect: StateFlow<Boolean> = _isSpeakingResultCorrect

    private val _isSpeakingQuizFinished = MutableStateFlow<Boolean>(false)
    val isSpeakingQuizFinished: StateFlow<Boolean> = _isSpeakingQuizFinished

    fun startSpeakingQuiz() {
        viewModelScope.launch {
            _activeLessonId.value = 0 // general comprehensive quiz
            val maxLessonId = _lastLearnedLessonId.value.coerceIn(1, 3)
            val allList = allSentences.value
            val studiedSentences = allList.filter { it.lessonId <= maxLessonId }
            if (studiedSentences.isEmpty()) return@launch

            // Choose 10 random sentences from studied pool
            val selected = studiedSentences.shuffled().take(10)

            _speakingQuizSentences.value = selected
            _currentSpeakingIndex.value = 0
            _speakingRecognizedText.value = ""
            _isSpeakingListening.value = false
            _speakingQuizCorrectCount.value = 0
            _isSpeakingResultChecked.value = false
            _isSpeakingResultCorrect.value = false
            _isSpeakingQuizFinished.value = false

            navigateTo(AppScreen.SpeakingQuiz)
        }
    }

    fun startSpeakingQuizForLesson(lessonId: Int) {
        viewModelScope.launch {
            _activeLessonId.value = lessonId // lesson-specific speaking quiz
            val allList = allSentences.value
            val lessonSentences = allList.filter { it.lessonId == lessonId }
            if (lessonSentences.isEmpty()) return@launch

            _speakingQuizSentences.value = lessonSentences
            _currentSpeakingIndex.value = 0
            _speakingRecognizedText.value = ""
            _isSpeakingListening.value = false
            _speakingQuizCorrectCount.value = 0
            _isSpeakingResultChecked.value = false
            _isSpeakingResultCorrect.value = false
            _isSpeakingQuizFinished.value = false

            navigateTo(AppScreen.SpeakingQuiz)
        }
    }

    fun submitSpeakingAnswer(recognized: String) {
        val index = _currentSpeakingIndex.value
        val sentences = _speakingQuizSentences.value
        if (index >= sentences.size) return

        val currentSentence = sentences[index]
        
        // Let's standardise sentences by removing punctuation and converting to lowercase
        val cleanRecognized = recognized.lowercase().replace(Regex("[?,.!]"), "").trim()
        val cleanCorrect = currentSentence.sentence.lowercase().replace(Regex("[?,.!]"), "").trim()

        _speakingRecognizedText.value = recognized
        _isSpeakingResultChecked.value = true

        val isCorrect = cleanRecognized == cleanCorrect
        _isSpeakingResultCorrect.value = isCorrect

        viewModelScope.launch(Dispatchers.IO) {
            // Update local DB progress for this sentence
            val existingProgress = repository.getProgressForSentence(currentSentence.id)
            val updatedProgress = existingProgress?.copy(
                correctCount = existingProgress.correctCount + (if (isCorrect) 1 else 0),
                incorrectCount = existingProgress.incorrectCount + (if (isCorrect) 0 else 1),
                lastAttemptTime = System.currentTimeMillis()
            ) ?: UserProgressEntity(
                sentenceId = currentSentence.id,
                correctCount = if (isCorrect) 1 else 0,
                incorrectCount = if (isCorrect) 0 else 1
            )
            repository.updateProgress(updatedProgress)

            withContext(Dispatchers.Main) {
                if (isCorrect) {
                    _speakingQuizCorrectCount.value = _speakingQuizCorrectCount.value + 1
                } else {
                    // Trigger vibration and read the correct answer aloud
                    triggerVibration()
                    speakEnglish(currentSentence.sentence)
                }
            }
        }
    }

    fun advanceSpeakingQuiz() {
        val nextIndex = _currentSpeakingIndex.value + 1
        if (nextIndex < _speakingQuizSentences.value.size) {
            _currentSpeakingIndex.value = nextIndex
            _speakingRecognizedText.value = ""
            _isSpeakingResultChecked.value = false
            _isSpeakingResultCorrect.value = false
            _isSpeakingListening.value = false
        } else {
            _isSpeakingQuizFinished.value = true
            // Record quiz completion stats
            viewModelScope.launch {
                val total = _quizAttemptsCount.value + 1
                _quizAttemptsCount.value = total
                repository.saveSetting("quiz_attempts_count", total.toString())

                val correct = _speakingQuizCorrectCount.value
                val isSuccess = correct >= 6
                if (isSuccess) {
                    val success = _quizSuccessCount.value + 1
                    _quizSuccessCount.value = success
                    repository.saveSetting("quiz_success_count", success.toString())
                } else {
                    val fail = _quizFailCount.value + 1
                    _quizFailCount.value = fail
                    repository.saveSetting("quiz_fail_count", fail.toString())
                }

                // Save perfect score for the active lesson if got 100% (all speaking sentences correct)
                val activeId = _activeLessonId.value
                if (activeId != 0 && correct == _speakingQuizSentences.value.size) {
                    repository.saveSetting("lesson_${activeId}_speaking_perfect", "true")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
