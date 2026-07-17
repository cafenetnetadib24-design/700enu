package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.LearningViewModel
import java.util.Locale

@Composable
fun SpeakingQuizScreen(viewModel: LearningViewModel) {
    val sentences by viewModel.speakingQuizSentences.collectAsState()
    val currentIndex by viewModel.currentSpeakingIndex.collectAsState()
    val recognizedText by viewModel.speakingRecognizedText.collectAsState()
    val isListeningFlow by viewModel.isSpeakingListening.collectAsState()
    val correctCount by viewModel.speakingQuizCorrectCount.collectAsState()
    val isChecked by viewModel.isSpeakingResultChecked.collectAsState()
    val isCorrect by viewModel.isSpeakingResultCorrect.collectAsState()
    val isFinished by viewModel.isSpeakingQuizFinished.collectAsState()

    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    // Speech Recognizer States
    var isRecognizerActive by remember { mutableStateOf(false) }
    var speechErrorMsg by remember { mutableStateOf<String?>(null) }
    var partialResultText by remember { mutableStateOf("") }
    
    // Keyboard fallback state
    var showKeyboardInput by remember { mutableStateOf(false) }
    var manualTextAnswer by remember { mutableStateOf("") }

    val speechRecognizer = remember { 
        try {
            SpeechRecognizer.createSpeechRecognizer(context)
        } catch (e: Exception) {
            null
        }
    }
    
    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    DisposableEffect(speechRecognizer) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isRecognizerActive = true
                speechErrorMsg = null
                partialResultText = ""
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isRecognizerActive = false
            }

            override fun onError(error: Int) {
                isRecognizerActive = false
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "خطای ضبط صدا"
                    SpeechRecognizer.ERROR_CLIENT -> "خطای برنامه کلاینت"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "عدم دسترسی به میکروفون"
                    SpeechRecognizer.ERROR_NETWORK -> "خطای شبکه (نیاز به اینترنت)"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "پایان زمان شبکه"
                    SpeechRecognizer.ERROR_NO_MATCH -> "جمله‌ای تشخیص داده نشد. لطفا واضح‌تر صحبت کنید."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "سرویس مشغول است. لحظاتی دیگر تلاش کنید."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "پایان زمان صحبت. لطفا سریع‌تر بگویید."
                    else -> "سرویس گفتار در دسترس نیست. می‌توانید از کیبورد استفاده کنید."
                }
                speechErrorMsg = message
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotEmpty()) {
                    viewModel.submitSpeakingAnswer(text)
                } else {
                    speechErrorMsg = "جمله‌ای تشخیص داده نشد."
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                partialResultText = matches?.firstOrNull() ?: ""
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer?.setRecognitionListener(listener)

        onDispose {
            try {
                speechRecognizer?.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Request permissions on screen launch
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    if (isFinished) {
        // SUMMARY / COMPLETION SCREEN
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("speaking_completion_card"),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                if (correctCount >= 6) Color(0xFF10B981).copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (correctCount >= 6) Icons.Default.CheckCircle else Icons.Default.School,
                            contentDescription = null,
                            tint = if (correctCount >= 6) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "پایان آزمون مکالمه هوشمند",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "عملکرد شما در بیان درست تلفظ و ساختار جملات انگلیسی:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Score Display
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$correctCount",
                            fontSize = 64.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (correctCount >= 6) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " از ۱۰",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 14.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Feedback Message
                    val feedbackText = when {
                        correctCount == 10 -> "فوق‌العاده و بی‌نقص! شما یک سخنور بومی هستید! 🌟"
                        correctCount >= 8 -> "بسیار عالی! تسلط و تلفظ شما به جملات بی‌نظیر است. 👏"
                        correctCount >= 6 -> "خوب است! با کمی تمرین بیشتر تلفظ‌های روان‌تری خواهید داشت. 👍"
                        else -> "برای تقویت مکالمه، مجدداً جملات جلسات را مرور و تکرار صوتی کنید. 💪"
                    }

                    Text(
                        text = feedbackText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    val activeId by viewModel.activeLessonId.collectAsState()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Repeat Button
                        OutlinedButton(
                            onClick = {
                                if (activeId > 0) {
                                    viewModel.startSpeakingQuizForLesson(activeId)
                                } else {
                                    viewModel.startSpeakingQuiz()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تکرار آزمون", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        // Back Button
                        Button(
                            onClick = {
                                val catId = if (activeId > 0) ((activeId - 1) / 5) + 1 else 1
                                viewModel.navigateTo(AppScreen.CategoryLessons(catId))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("بازگشت", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary Home Button
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.Home) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("speaking_finish_home_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("صفحه اصلی", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    } else if (sentences.isNotEmpty() && currentIndex < sentences.size) {
        val currentSentence = sentences[currentIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP STATUS ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "آزمون مکالمه (شفاهی)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "سوال ${currentIndex + 1} از ۱۰",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { (currentIndex + 1) / 10f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // PERSIAN QUESTION CARD
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("speaking_question_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "این جمله را به انگلیسی بگویید:",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Text(
                                text = currentSentence.translation,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }

                // RECORD / INPUT INTERACTIVE ZONE
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("speaking_interactive_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (!isChecked) {
                                // Microphone / Voice button
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(140.dp)
                                ) {
                                    if (isRecognizerActive) {
                                        // Simple pulse animation ring
                                        Surface(
                                            modifier = Modifier.fillMaxSize(),
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        ) {}
                                    }

                                    Button(
                                        onClick = {
                                            if (!hasPermission) {
                                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            } else {
                                                if (isRecognizerActive) {
                                                    try {
                                                        speechRecognizer?.stopListening()
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                    isRecognizerActive = false
                                                } else {
                                                    speechErrorMsg = null
                                                    partialResultText = ""
                                                    try {
                                                        speechRecognizer?.startListening(recognizerIntent)
                                                    } catch (e: Exception) {
                                                        speechErrorMsg = "سیستم گفتار در این دستگاه به درستی نصب نیست. لطفاً دستی تایپ کنید."
                                                        isRecognizerActive = false
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .size(96.dp)
                                            .testTag("microphone_button"),
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isRecognizerActive) MaterialTheme.colorScheme.error 
                                                           else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (isRecognizerActive) Icons.Default.Stop else Icons.Default.Mic,
                                            contentDescription = "ضبط صدا",
                                            modifier = Modifier.size(36.dp),
                                            tint = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = if (isRecognizerActive) "در حال شنیدن... بگویید و سپس دکمه قرمز را بزنید" 
                                           else "دکمه بالا را بزنید و انگلیسی جمله را بخوانید",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isRecognizerActive) MaterialTheme.colorScheme.error 
                                           else MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Realtime partial feedback text
                                if (partialResultText.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = partialResultText,
                                            fontSize = 15.sp,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                            color = MaterialTheme.colorScheme.primary,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }

                                // Error prompt
                                speechErrorMsg?.let { error ->
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = error,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }

                                // Fallback option
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(
                                    onClick = { 
                                        manualTextAnswer = ""
                                        showKeyboardInput = !showKeyboardInput 
                                    },
                                    modifier = Modifier.testTag("toggle_keyboard_fallback_button"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = if (showKeyboardInput) Icons.Default.Close else Icons.Default.Keyboard,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (showKeyboardInput) "بستن کیبورد" else "کیبورد (پاسخ کتبی)",
                                        fontSize = 11.sp
                                    )
                                }

                                // Manual input fields
                                AnimatedVisibility(visible = showKeyboardInput) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        OutlinedTextField(
                                            value = manualTextAnswer,
                                            onValueChange = { manualTextAnswer = it },
                                            placeholder = { Text("ترجمه انگلیسی را تایپ کنید...") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("manual_input_field"),
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = {
                                                if (manualTextAnswer.isNotBlank()) {
                                                    viewModel.submitSpeakingAnswer(manualTextAnswer)
                                                    showKeyboardInput = false
                                                }
                                            },
                                            enabled = manualTextAnswer.isNotBlank(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("submit_manual_answer_button"),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("بررسی و تایید پاسخ", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                            } else {
                                // RESULT STATE (CHECKED)
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Feedback visual card (Checkmark or Cross)
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isCorrect) Color(0xFF10B981).copy(alpha = 0.15f)
                                                else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                                            contentDescription = null,
                                            tint = if (isCorrect) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = if (isCorrect) "پاسخ شما کاملاً درست است!" else "نیاز به تلاش دوباره",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCorrect) Color(0xFF10B981) else MaterialTheme.colorScheme.error
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "جمله شما:",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        Text(
                                            text = recognizedText.ifEmpty { "سکوت یا عدم تشخیص" },
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                            color = if (isCorrect) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                                            textAlign = TextAlign.Center,
                                            style = androidx.compose.ui.text.TextStyle(textDirection = androidx.compose.ui.text.style.TextDirection.Ltr),
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                    }

                                    if (!isCorrect) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = "ترجمه صحیح انگلیسی:",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                            Text(
                                                text = currentSentence.sentence,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                textAlign = TextAlign.Center,
                                                style = androidx.compose.ui.text.TextStyle(textDirection = androidx.compose.ui.text.style.TextDirection.Ltr),
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Speaker replay button
                                        IconButton(
                                            onClick = { viewModel.speakEnglish(currentSentence.sentence) },
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                                .testTag("replay_sound_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "شنیدن مجدد",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Text("تلفظ صحیح جمله", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Next Button
                                    Button(
                                        onClick = { viewModel.advanceSpeakingQuiz() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("speaking_next_button"),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isCorrect) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(
                                            text = if (currentIndex < 9) "جمله بعدی ➔" else "مشاهده نتیجه نهایی 🏁",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // HELPER CARD
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💡",
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "برای کسب بیشترین نتیجه، تلاش کنید ابتدا کلمات را در ذهن خود ترجمه کرده و سپس روان و با صدای رسا بیان کنید. علائم نگارشی نظیر علامت سوال و نقطه نادیده گرفته می‌شوند.",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
