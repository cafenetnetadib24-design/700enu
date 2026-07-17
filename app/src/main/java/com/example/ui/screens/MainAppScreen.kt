package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CastForEducation
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.ui.geometry.Offset
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Image
import com.example.data.billing.MyketBillingManager
import com.example.data.database.SentenceEntity
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.LearningViewModel
import com.example.ui.viewmodel.LessonSession
import com.example.ui.viewmodel.CategorySession
import com.example.ui.viewmodel.QuestionType
import com.example.ui.viewmodel.QuizQuestion
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.BorderStroke

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: LearningViewModel) {
    val activity = LocalContext.current.findActivity()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isPremiumUnlocked by viewModel.billingManager.isPremiumUnlocked.collectAsState()
    val lessons by viewModel.lessons.collectAsState()

    val lessonTitle = remember(currentScreen, lessons) {
        val screen = currentScreen
        if (screen is AppScreen.CategoryLessons) {
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
            categoriesList.getOrNull(screen.categoryId - 1) ?: "دسته بندی"
        } else if (screen is AppScreen.Learning) {
            lessons.find { it.id == screen.lessonId }?.title ?: "جلسه ${screen.lessonId}"
        } else if (screen is AppScreen.Quiz) {
            if (screen.lessonId == 0) {
                "آزمون جامع هوشمند"
            } else {
                "آزمون " + (lessons.find { it.id == screen.lessonId }?.title ?: "جلسه ${screen.lessonId}")
            }
        } else if (screen == AppScreen.SpeakingQuiz) {
            "آزمون مکالمه هوشمند"
        } else if (screen == AppScreen.Settings) {
            "تنظیمات برنامه"
        } else if (screen == AppScreen.Guide) {
            "راهنمای استفاده از برنامه"
        } else {
            ""
        }
    }

    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()
    val initialThemeCompleted by viewModel.initialThemeCompleted.collectAsState()
    val isSettingsLoaded by viewModel.isSettingsLoaded.collectAsState()

    // Force beautiful Persian RTL direction
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    if (currentScreen == AppScreen.Home) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "۷۰۰ جمله ضروری انگلیسی",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            actions = {
                                IconButton(
                                    onClick = { viewModel.navigateTo(AppScreen.Guide) },
                                    modifier = Modifier.testTag("help_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HelpOutline,
                                        contentDescription = "راهنمای استفاده"
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.navigateTo(AppScreen.Settings) },
                                    modifier = Modifier.testTag("settings_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "تنظیمات"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                scrolledContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("home_top_bar")
                        )
                    } else {
                        TopAppBar(
                            title = {
                                Text(
                                    text = lessonTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        val screen = currentScreen
                                        if (screen is AppScreen.Learning) {
                                            viewModel.navigateTo(AppScreen.CategoryLessons(((screen.lessonId - 1) / 5) + 1))
                                        } else if (screen is AppScreen.Quiz && screen.lessonId != 0) {
                                            viewModel.navigateTo(AppScreen.CategoryLessons(((screen.lessonId - 1) / 5) + 1))
                                        } else {
                                            viewModel.navigateTo(AppScreen.Home)
                                        }
                                    },
                                    modifier = Modifier.testTag("back_to_home_button")
                                ) {
                                    Icon(
                                        imageVector = if (currentScreen is AppScreen.Learning || currentScreen is AppScreen.CategoryLessons || (currentScreen is AppScreen.Quiz && (currentScreen as AppScreen.Quiz).lessonId != 0)) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Home,
                                        contentDescription = "بازگشت",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                scrolledContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("app_top_bar")
                        )
                    }
                }
            ) { paddingValues ->
                AppBackground(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (val screen = currentScreen) {
                        is AppScreen.Home -> HomeScreen(viewModel = viewModel, activity = activity)
                        is AppScreen.CategoryLessons -> CategoryLessonsScreen(viewModel = viewModel, categoryId = screen.categoryId, activity = activity)
                        is AppScreen.Learning -> LearningScreen(viewModel = viewModel, lessonId = screen.lessonId)
                        is AppScreen.Quiz -> QuizScreen(viewModel = viewModel, lessonId = screen.lessonId)
                        is AppScreen.Settings -> SettingsScreen(viewModel = viewModel)
                        is AppScreen.SpeakingQuiz -> SpeakingQuizScreen(viewModel = viewModel)
                        is AppScreen.Guide -> GuideScreen(viewModel = viewModel)
                    }
                }
            }

            if (isSettingsLoaded) {
                if (!onboardingCompleted) {
                    OnboardingTutorialOverlay(viewModel = viewModel)
                } else if (!initialThemeCompleted) {
                    InitialThemeSelectionOverlay(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: LearningViewModel, activity: Activity?) {
    val lessons by viewModel.lessons.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val categoriesLayoutMode by viewModel.categoriesLayoutMode.collectAsState()
    val isPremiumUnlocked by viewModel.billingManager.isPremiumUnlocked.collectAsState()
    val lastLearnedLessonId by viewModel.lastLearnedLessonId.collectAsState()
    val allSentences by viewModel.allSentences.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    
    val quizAttemptsCount by viewModel.quizAttemptsCount.collectAsState()
    val quizSuccessCount by viewModel.quizSuccessCount.collectAsState()
    val quizFailCount by viewModel.quizFailCount.collectAsState()
    val allUserProgress by viewModel.allUserProgress.collectAsState()

    val totalCompletedSentences = remember(allUserProgress) {
        allUserProgress.count { it.isCompleted }
    }
    val progressPercent = remember(totalCompletedSentences) {
        ((totalCompletedSentences / 700.0) * 100).toInt().coerceIn(0, 100)
    }
    
    var searchQuery by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) } // 0: Course, 1: Quiz, 2: Customization

    val filteredSentences = remember(searchQuery, allSentences) {
        if (searchQuery.trim().isEmpty()) {
            emptyList()
        } else {
            val query = searchQuery.trim().lowercase()
            allSentences.filter {
                it.sentence.lowercase().contains(query) ||
                it.translation.lowercase().contains(query)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Hero Banner Item
        item {
            val bannerImageUrl by viewModel.bannerImageUrl.collectAsState()
            val bannerAdUrl by viewModel.bannerAdUrl.collectAsState()
            val context = LocalContext.current

            val hasAdLink = !bannerAdUrl.isNullOrEmpty()
            val clickModifier = if (hasAdLink) {
                Modifier.clickable {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(bannerAdUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "خطا در باز کردن لینک", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Modifier
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray)
                    .then(clickModifier)
            ) {
                if (!bannerImageUrl.isNullOrEmpty()) {
                    val painter = coil.compose.rememberAsyncImagePainter(
                        model = coil.request.ImageRequest.Builder(context)
                            .data(bannerImageUrl)
                            .crossfade(true)
                            .build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = "آموزش ۷۰۰ جمله ضروری انگلیسی",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = "آموزش ۷۰۰ جمله ضروری انگلیسی",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Progress Timeline Item under the main Hero Banner
        item {
            ProgressTimeline(categories = categories)
        }

        // Modern Pill Tab Selector
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        Triple("دوره آموزشی", Icons.Default.School, 0),
                        Triple("آزمون جامع", Icons.Default.Lightbulb, 1)
                    )
                    
                    tabs.forEach { (title, icon, index) ->
                        val isSelected = activeTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary 
                                    else Color.Transparent
                                )
                                .clickable { activeTab = index }
                                .testTag("home_tab_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                           else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                           else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search Bar (Always visible at top of tabs)
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_bar"),
                placeholder = {
                    Text(
                        text = "جستجوی کلمه یا جمله (مثال: hello یا سلام)...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "جستجو",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "پاک کردن",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.08f)
                )
            )
        }

        if (searchQuery.trim().isNotEmpty()) {
            // Search Mode Content
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "نتایج جستجو",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${filteredSentences.size} جمله یافت شد",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (filteredSentences.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🔍",
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "جمله‌ای یافت نشد!",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "لطفاً عبارت دیگری را به انگلیسی یا فارسی جستجو کنید.",
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(filteredSentences) { sentence ->
                    SearchResultCard(
                        sentence = sentence,
                        isPremiumUnlocked = isPremiumUnlocked,
                        onSpeakClick = { viewModel.speakEnglish(sentence.sentence) },
                        onViewInLesson = { viewModel.startLearningAtSentence(sentence.lessonId, sentence.id) },
                        onUpgradeClick = {
                            if (activity != null) {
                                viewModel.billingManager.launchPurchaseFlow(activity) { _, msg ->
                                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }
            }
        } else {
                    // Tab Content Switcher
                    when (activeTab) {
                        0 -> {
                            // TAB 0: COURSE CONTENT
                            // Fast Quick Action Buttons
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // "شروع یادگیری" (Start Learning)
                                    Button(
                                        onClick = { viewModel.startLearning(1) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .testTag("start_learning_button"),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) {
                                        Icon(Icons.Default.School, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("شروع یادگیری", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }

                                    // "ادامه یادگیری" (Continue Learning)
                                    OutlinedButton(
                                        onClick = { viewModel.startLearning(lastLearnedLessonId) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .testTag("continue_learning_button"),
                                        shape = RoundedCornerShape(16.dp),
                                        border = ButtonDefaults.outlinedButtonBorder.copy()
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("ادامه یادگیری", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }

                            // Sessions list header
                            item {
                                Text(
                                    text = "دسته‌بندی‌های موضوعی",
                                    style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Categories items list
                    if (categoriesLayoutMode == "grid") {
                        val chunkedCategories = categories.chunked(2)
                        items(chunkedCategories) { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowItems.forEach { category ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        CategoryItemGridCard(
                                            category = category,
                                            onCategoryClick = {
                                                viewModel.navigateTo(AppScreen.CategoryLessons(category.id))
                                            }
                                        )
                                    }
                                }
                                if (rowItems.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    } else {
                        items(categories) { category ->
                            CategoryItemRow(
                                category = category,
                                onCategoryClick = {
                                    viewModel.navigateTo(AppScreen.CategoryLessons(category.id))
                                }
                            )
                        }
                    }
                }
                1 -> {
                    // TAB 1: COMPREHENSIVE SMART QUIZ CONTENT
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("smart_quiz_card"),
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
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "آزمون جامع هوشمند",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "ارزیابی آموخته‌ها از تمامی جملاتی که تاکنون یاد گرفته‌اید",
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Study Stats and Progress
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Total progress rate
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "میزان پیشرفت کل:",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "$progressPercent٪ (${totalCompletedSentences} از ۷۰۰ جمله)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        
                                        LinearProgressIndicator(
                                            progress = { progressPercent / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )

                                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

                                        // Quiz attempts stats
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "تعداد کل آزمون‌های داده شده:",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "$quizAttemptsCount بار",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "تعداد آزمون‌های موفق (نمره ۶ به بالا):",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "$quizSuccessCount بار",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF10B981)
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "تعداد آزمون‌های ناموفق:",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "$quizFailCount بار",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }

                                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "تعداد جملات قابل آزمون فعلی:",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "${lastLearnedLessonId * 7} جمله ضروری",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Start Button
                                Button(
                                    onClick = { viewModel.startSmartQuiz() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .testTag("start_smart_quiz_button"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("شروع آزمون هوشمند (۱۰ سوال)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // anti-repetition note
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🔒",
                                            fontSize = 16.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "الگوریتم هوشمند فعال است: سوالات آزمون‌ها به گونه‌ای جابجا و تکرارزدایی می‌شوند که در ۱۰ آزمون اول خود با هیچ سوال تکراری مواجه نخواهید شد.",
                                            fontSize = 10.sp,
                                            lineHeight = 15.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("speaking_quiz_start_card"),
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
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981).copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "آزمون مکالمه (شفاهی)",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "با بیان صوتی جملات یاد گرفته شده، تلفظ و درستی ترجمه خود را بسنجید.",
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Start Speaking Button
                                Button(
                                    onClick = { viewModel.startSpeakingQuiz() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .testTag("start_speaking_quiz_button"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF10B981),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("شروع آزمون مکالمه (۱۰ سوال صوتی)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getCategoryIcon(catId: Int): ImageVector {
    return when (catId) {
        1 -> Icons.Default.Phone
        2 -> Icons.Default.Person
        3 -> Icons.Default.Favorite
        4 -> Icons.Default.Refresh
        5 -> Icons.Default.Star
        6 -> Icons.Default.Home
        7 -> Icons.Default.List
        8 -> Icons.Default.ShoppingCart
        9 -> Icons.Default.Build
        10 -> Icons.Default.School
        11 -> Icons.Default.LocationOn
        12 -> Icons.Default.Place
        13 -> Icons.Default.PlayArrow
        14 -> Icons.Default.Done
        15 -> Icons.Default.Warning
        16 -> Icons.Default.Face
        17 -> Icons.Default.Settings
        18 -> Icons.Default.Share
        19 -> Icons.Default.MenuBook
        20 -> Icons.Default.Person
        21 -> Icons.Default.Email
        else -> Icons.Default.Book
    }
}

@Composable
fun SentenceIllustrativeBanner(
    sentenceText: String,
    categoryName: String,
    categoryId: Int,
    modifier: Modifier = Modifier
) {
    val gradientColors = remember(categoryId) {
        when (categoryId % 6) {
            0 -> listOf(Color(0xFF6366F1), Color(0xFF4F46E5))
            1 -> listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
            2 -> listOf(Color(0xFF10B981), Color(0xFF047857))
            3 -> listOf(Color(0xFFEC4899), Color(0xFFBE185D))
            4 -> listOf(Color(0xFFF59E0B), Color(0xFFD97706))
            else -> listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.minDimension * 0.6f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.7f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.06f),
                radius = size.minDimension * 0.8f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.2f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(categoryId),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = categoryName,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CategoryItemRow(
    category: CategorySession,
    onCategoryClick: () -> Unit
) {
    val isCompleted = category.isCompleted
    val containerColor = if (isCompleted) Color(0xFFE6F4EA) else MaterialTheme.colorScheme.surface
    val onSurfaceColor = if (isCompleted) Color(0xFF137333) else MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = if (isCompleted) Color(0xFF137333).copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCategoryClick() }
            .testTag("category_item_${category.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circle Badge representing category icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) Color(0xFF10B981).copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(category.id),
                        contentDescription = null,
                        tint = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.title,
                        fontWeight = FontWeight.Bold,
                        color = onSurfaceColor,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isCompleted) "۱۰۰٪ تکمیل شده و آزمون‌ها موفق" else "پیشرفت: ${category.completedSentencesCount} از ${category.totalSentencesCount} جمله",
                        color = onSurfaceVariantColor,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = onSurfaceVariantColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar for the category
            val progressPercent = if (category.totalSentencesCount > 0) {
                (category.completedSentencesCount.toFloat() / category.totalSentencesCount).coerceIn(0f, 1f)
            } else 0f

            LinearProgressIndicator(
                progress = { progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                trackColor = if (isCompleted) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun CategoryItemGridCard(
    category: CategorySession,
    onCategoryClick: () -> Unit
) {
    val isCompleted = category.isCompleted
    val containerColor = if (isCompleted) Color(0xFFE6F4EA) else MaterialTheme.colorScheme.surface
    val onSurfaceColor = if (isCompleted) Color(0xFF137333) else MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = if (isCompleted) Color(0xFF137333).copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onCategoryClick() }
            .testTag("category_item_grid_${category.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circle Badge representing category icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) Color(0xFF10B981).copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(category.id),
                        contentDescription = null,
                        tint = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                // Progress count tag
                Text(
                    text = "${category.completedSentencesCount}/${category.totalSentencesCount}",
                    color = onSurfaceVariantColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = category.title,
                fontWeight = FontWeight.Bold,
                color = onSurfaceColor,
                fontSize = 13.sp,
                maxLines = 2,
                lineHeight = 18.sp
            )

            // Progress bar for the category
            val progressPercent = if (category.totalSentencesCount > 0) {
                (category.completedSentencesCount.toFloat() / category.totalSentencesCount).coerceIn(0f, 1f)
            } else 0f

            LinearProgressIndicator(
                progress = { progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                trackColor = if (isCompleted) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun CategoryLessonsScreen(
    viewModel: LearningViewModel,
    categoryId: Int,
    activity: Activity?
) {
    val categories by viewModel.categories.collectAsState()
    val isPremiumUnlocked by viewModel.billingManager.isPremiumUnlocked.collectAsState()
    
    val category = remember(categories, categoryId) {
        categories.find { it.id == categoryId }
    }

    if (category == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("دسته بندی یافت نشد.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "آموزش گام به گام انگلیسی در این موضوع شامل ۵ درس مجزا",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Row for overall category progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "پیشرفت کل موضوع:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "${category.completedSentencesCount} از ${category.totalSentencesCount} جمله تکمیل شده",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val progressPercent = if (category.totalSentencesCount > 0) {
                        (category.completedSentencesCount.toFloat() / category.totalSentencesCount).coerceIn(0f, 1f)
                    } else 0f
                    
                    LinearProgressIndicator(
                        progress = { progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(category.lessons) { lesson ->
            LessonItemRow(
                lesson = lesson,
                onStartLearning = { viewModel.startLearning(lesson.id) },
                onStartQuiz = { viewModel.startQuiz(lesson.id) },
                onStartSpeakingQuiz = { viewModel.startSpeakingQuizForLesson(lesson.id) },
                onUpgradeClick = {
                    if (activity != null) {
                        viewModel.billingManager.launchPurchaseFlow(activity) { _, msg ->
                            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LessonItemRow(
    lesson: LessonSession,
    onStartLearning: () -> Unit,
    onStartQuiz: () -> Unit,
    onStartSpeakingQuiz: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!lesson.isLocked) {
                    onStartLearning()
                } else {
                    if (lesson.isSequenceLocked) {
                        Toast.makeText(context, "لطفاً ابتدا آموزش قبلی را با موفقیت تکمیل کنید!", Toast.LENGTH_SHORT).show()
                    } else {
                        onUpgradeClick()
                    }
                }
            }
            .testTag("lesson_item_${lesson.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circle Badge representing state
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (lesson.isLocked) MaterialTheme.colorScheme.surfaceVariant
                            else if (lesson.isCompleted) Color(0xFF10B981).copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (lesson.isLocked) Icons.Default.Lock
                        else if (lesson.isCompleted) Icons.Default.Check
                        else Icons.Default.Book,
                        contentDescription = null,
                        tint = if (lesson.isLocked) MaterialTheme.colorScheme.onSurfaceVariant
                        else if (lesson.isCompleted) Color(0xFF10B981)
                        else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lesson.title,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Text(
                        text = lesson.subtitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Action Indicator Label
                if (lesson.isLocked) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "قفل 🔒",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                } else if (lesson.isCompleted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "کامل شده",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "آموزش فعال",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Simple Learning Progress indicator inside each card
            if (!lesson.isLocked) {
                val progressFraction = if (lesson.totalSentencesCount > 0) {
                    lesson.completedCount.toFloat() / lesson.totalSentencesCount.toFloat()
                } else 0f
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        color = if (lesson.isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${lesson.completedCount} از ${lesson.totalSentencesCount} جمله",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Standard Quiz and Speaking Quiz buttons
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Start/Study Button
                    Button(
                        onClick = onStartLearning,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (lesson.completedCount >= 7) "مطالعه مجدد" else "شروع آموزش", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Standard Quiz Button
                    Button(
                        onClick = onStartQuiz,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lesson.isQuizPerfect) Color(0xFFD1FAE5) else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (lesson.isQuizPerfect) Color(0xFF065F46) else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = if (lesson.isQuizPerfect) BorderStroke(1.dp, Color(0xFF34D399)) else null
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (lesson.isQuizPerfect) "آزمون تستی (۱۰۰٪) ✅" else "آزمون تستی 📝",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Speaking Quiz Button
                    Button(
                        onClick = onStartSpeakingQuiz,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lesson.isSpeakingPerfect) Color(0xFFD1FAE5) else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (lesson.isSpeakingPerfect) Color(0xFF065F46) else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = if (lesson.isSpeakingPerfect) BorderStroke(1.dp, Color(0xFF34D399)) else null
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (lesson.isSpeakingPerfect) "مکالمه (۱۰۰٪) ✅" else "آزمون مکالمه 🎤",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LearningScreen(viewModel: LearningViewModel, lessonId: Int) {
    val sentences by viewModel.learningSentences.collectAsState()
    val currentIndex by viewModel.currentSentenceIndex.collectAsState()
    val isPremiumUnlocked by viewModel.billingManager.isPremiumUnlocked.collectAsState()
    val activity = LocalContext.current.findActivity()

    if (sentences.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("در حال بارگذاری جملات...")
        }
        return
    }

    val currentSentence = sentences[currentIndex]

    val categoryId = remember(lessonId) { ((lessonId - 1) / 5) + 1 }
    val categoryName = remember(categoryId) {
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
        categoriesList.getOrNull(categoryId - 1) ?: "دسته‌بندی $categoryId"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("learning_screen_root"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top session progress bar
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "پیشرفت جلسه: جمله ${currentIndex + 1} از ${sentences.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "جلسه $lessonId",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / sentences.size.toFloat() },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
        }

        // Master Sentence Card with visual cues
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 24.dp)
                .testTag("sentence_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Category Visual dynamic illustrative banner
                SentenceIllustrativeBanner(
                    sentenceText = currentSentence.sentence,
                    categoryName = categoryName,
                    categoryId = categoryId
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // English Sentence
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = currentSentence.sentence,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            lineHeight = 32.sp,
                            style = androidx.compose.ui.text.TextStyle(textDirection = androidx.compose.ui.text.style.TextDirection.Ltr),
                            modifier = Modifier.testTag("english_sentence_text")
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(
                        modifier = Modifier.width(60.dp),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Persian translation
                    Text(
                        text = currentSentence.translation,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                        modifier = Modifier.testTag("persian_translation_text")
                    )
                }

                // Interactive Audio cue helper
                Text(
                    text = "برای شنیدن تلفظ صحیح روی دکمه بلندگو کلیک کنید",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Interaction Actions
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // دکمه بلندگو (تلفظ صوتی) به جای بلد نبودم
                Button(
                    onClick = { viewModel.speakEnglish(currentSentence.sentence) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("play_audio_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "پخش صوتی",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("شنیدن تلفظ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // بلد بودم -> یاد گرفتم (Saves success and advances)
                Button(
                    onClick = { viewModel.completeCurrentSentence() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("correct_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("یاد گرفتم", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Helpers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateLearningPrevious() },
                    enabled = currentIndex > 0,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("prev_sentence_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("جمله قبل", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.navigateLearningNext() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("next_sentence_button")
                ) {
                    Text(if (currentIndex == sentences.size - 1) "آزمون جلسه" else "جمله بعد", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun QuizScreen(viewModel: LearningViewModel, lessonId: Int) {
    val questions by viewModel.quizQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val correctCount by viewModel.quizCorrectAnswersCount.collectAsState()
    val wrongCount by viewModel.quizWrongAnswersCount.collectAsState()
    val isFinished by viewModel.isQuizFinished.collectAsState()
    val selectedQuizOption by viewModel.selectedQuizOption.collectAsState()
    val isPremiumUnlocked by viewModel.billingManager.isPremiumUnlocked.collectAsState()
    val activity = LocalContext.current.findActivity()

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("در حال ساخت آزمون اختصاصی...")
        }
        return
    }

    if (isFinished) {
        val isSuccessful = correctCount == questions.size
        if (isSuccessful && lessonId > 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .testTag("quiz_success_speaking_screen"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Success Medal/Banner Icon
                item {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE6F4EA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF137333),
                            modifier = Modifier.size(72.dp)
                        )
                    }
                }

                // Beautiful Success Message Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "تبریک! آزمون تستی با موفقیت انجام شد 🎉",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF137333),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "زبان آموز گرامی شما با موفقیت توانستید آزمون های تستی این دوره را با موفقیت انجام دهید  حال باید\nآزمون مکالمه را انجام دهید  پس از موفقیت در آزمون مکالمه دوره بعدی\n\nآموزش برای شما فعال می شود",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                // Results Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("درست", fontSize = 12.sp, color = Color(0xFF10B981))
                                Text(text = correctCount.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("کل سوالات", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = questions.size.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Interaction Buttons
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Prominent Start Speaking Quiz button
                        Button(
                            onClick = { viewModel.startSpeakingQuizForLesson(lessonId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("start_speaking_quiz_after_success"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("شروع آزمون مکالمه", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }

                        // Full-width Back button (takes user to the category lessons)
                        Button(
                            onClick = {
                                val catId = ((lessonId - 1) / 5) + 1
                                viewModel.navigateTo(AppScreen.CategoryLessons(catId))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("back_to_category_after_success"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("بازگشت به دسته آموزشی", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.startQuiz(lessonId) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تکرار آزمون", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.navigateTo(AppScreen.Home) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("صفحه اصلی", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            return
        }

        // Quiz Complete Screen
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .testTag("quiz_results_root"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            item {
                Text(
                    text = "آزمون جلسه با موفقیت تمام شد!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "سیستم تکرار منقطع پیشرفت علمی شما را ذخیره کرد.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            // Results Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("درست", fontSize = 12.sp, color = Color(0xFF10B981))
                            Text(
                                text = correctCount.toString(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("کل سوالات", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = questions.size.toString(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("نادرست", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                            Text(
                                text = wrongCount.toString(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }



            // Screen Buttons
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Full-width Back button
                    Button(
                        onClick = {
                            val catId = if (lessonId > 0) ((lessonId - 1) / 5) + 1 else 1
                            viewModel.navigateTo(AppScreen.CategoryLessons(catId))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("quiz_fail_back_to_category"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("بازگشت به دسته آموزشی", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.startQuiz(lessonId) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تکرار آزمون", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.Home) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("صفحه اصلی", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        return
    }

    val currentQuestion = questions[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("quiz_screen_root"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Quiz Header Progress
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سوال ${currentIndex + 1} از ${questions.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "پاسخ درست: $correctCount",
                    fontSize = 12.sp,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )
        }

        // Question display card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 24.dp)
                .testTag("question_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Audio assist icon if the question has English text
                if (currentQuestion.type == QuestionType.MULTIPLE_CHOICE_EN_TO_FA || currentQuestion.type == QuestionType.FILL_IN_BLANK) {
                    IconButton(
                        onClick = { viewModel.speakEnglish(currentQuestion.questionText) },
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "شنیدن صوتی",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = currentQuestion.translationHint,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                val isQuestionEnglish = currentQuestion.type == QuestionType.MULTIPLE_CHOICE_EN_TO_FA || currentQuestion.type == QuestionType.FILL_IN_BLANK
                if (isQuestionEnglish) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = currentQuestion.questionText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp,
                            style = androidx.compose.ui.text.TextStyle(textDirection = androidx.compose.ui.text.style.TextDirection.Ltr),
                            modifier = Modifier.testTag("question_text")
                        )
                    }
                } else {
                    Text(
                        text = currentQuestion.questionText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp,
                        modifier = Modifier.testTag("question_text")
                    )
                }
            }
        }

        // Multiple choices option block
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            currentQuestion.options.forEachIndexed { optIdx, option ->
                val isSelected = selectedQuizOption == option
                val isCorrect = option.lowercase().trim() == currentQuestion.correctAnswer.lowercase().trim()
                val isSomeOptionSelected = selectedQuizOption != null

                val containerColor = when {
                    isSomeOptionSelected && isCorrect -> Color(0xFF10B981) // Green for correct
                    isSomeOptionSelected && isSelected -> MaterialTheme.colorScheme.error // Red for incorrect selected
                    isSomeOptionSelected -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) // Faded for others
                    else -> MaterialTheme.colorScheme.surfaceVariant // Default normal
                }

                val contentColor = when {
                    isSomeOptionSelected && (isCorrect || isSelected) -> Color.White
                    isSomeOptionSelected -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Button(
                    onClick = { viewModel.submitQuizAnswer(option) },
                    enabled = !isSomeOptionSelected, // Disable interactions once chosen
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("option_$optIdx"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        contentColor = contentColor,
                        disabledContainerColor = containerColor,
                        disabledContentColor = contentColor
                    )
                ) {
                    val isOptionEnglish = currentQuestion.type == QuestionType.MULTIPLE_CHOICE_FA_TO_EN || currentQuestion.type == QuestionType.FILL_IN_BLANK
                    if (isOptionEnglish) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Text(
                                text = option,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                style = androidx.compose.ui.text.TextStyle(textDirection = androidx.compose.ui.text.style.TextDirection.Ltr)
                            )
                        }
                    } else {
                        Text(
                            text = option,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    sentence: SentenceEntity,
    isPremiumUnlocked: Boolean,
    onSpeakClick: () -> Unit,
    onViewInLesson: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    val isLocked = sentence.lessonId != 1 && !isPremiumUnlocked
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("search_result_item_${sentence.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lesson badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isLocked) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isLocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "قفل شده",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = "جلسه ${sentence.lessonId}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLocked) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // TTS Pronunciation Button
                IconButton(
                    onClick = onSpeakClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "تلفظ صوتی",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // English sentence
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = sentence.sentence,
                    style = MaterialTheme.typography.titleMedium.copy(textDirection = androidx.compose.ui.text.style.TextDirection.Ltr),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Persian translation
            Text(
                text = sentence.translation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CTA Button to view in training
            Button(
                onClick = {
                    if (isLocked) {
                        onUpgradeClick()
                    } else {
                        onViewInLesson()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLocked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                ),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLocked) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ارتقا برای مشاهده در آموزش", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("مشاهده در آموزش این جلسه", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: LearningViewModel) {
    val reminderEnabled by viewModel.reminderEnabled.collectAsState()
    val reminderHour by viewModel.reminderHour.collectAsState()
    val reminderMinute by viewModel.reminderMinute.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val categoriesLayoutMode by viewModel.categoriesLayoutMode.collectAsState()
    val conditionalFreeLearning by viewModel.conditionalFreeLearning.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen_lazy_column"),
        contentPadding = PaddingValues(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Study Reminder Setting Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reminder_settings_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "یادآور روزانه مطالعه",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "ارسال اعلان روزانه برای مطالعه جملات جدید",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                        
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { viewModel.updateReminderEnabled(it) },
                            modifier = Modifier.testTag("reminder_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    if (reminderEnabled) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )

                        Text(
                            text = "تنظیم زمان اعلان روزانه:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = {
                                        val nextHour = (reminderHour + 1) % 24
                                        viewModel.updateReminderTime(nextHour, reminderMinute)
                                    },
                                    modifier = Modifier.testTag("increase_hour_button")
                                ) {
                                    Icon(Icons.Default.ArrowUpward, "افزایش ساعت")
                                }
                                Text(
                                    text = String.format(java.util.Locale.US, "%02d", reminderHour),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(
                                    onClick = {
                                        val prevHour = if (reminderHour - 1 < 0) 23 else reminderHour - 1
                                        viewModel.updateReminderTime(prevHour, reminderMinute)
                                    },
                                    modifier = Modifier.testTag("decrease_hour_button")
                                ) {
                                    Icon(Icons.Default.ArrowDownward, "کاهش ساعت")
                                }
                                Text("ساعت", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            
                            Text(
                                text = ":",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                            )
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = {
                                        val nextMinute = (reminderMinute + 5) % 60
                                        viewModel.updateReminderTime(reminderHour, nextMinute)
                                    },
                                    modifier = Modifier.testTag("increase_minute_button")
                                ) {
                                    Icon(Icons.Default.ArrowUpward, "افزایش دقیقه")
                                }
                                Text(
                                    text = String.format(java.util.Locale.US, "%02d", reminderMinute),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(
                                    onClick = {
                                        val prevMinute = if (reminderMinute - 5 < 0) 55 else reminderMinute - 5
                                        viewModel.updateReminderTime(reminderHour, prevMinute)
                                    },
                                    modifier = Modifier.testTag("decrease_minute_button")
                                ) {
                                    Icon(Icons.Default.ArrowDownward, "کاهش دقیقه")
                                }
                                Text("دقیقه", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Explanation Note
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔔",
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "یادآوری مطالعه به صورت نوتیفیکیشن صوتی همراه با لرزش ملایم گوشی (ویبره) ارسال خواهد شد. ساعت پیش‌فرض روی ۱۷ عصر تنظیم شده است.",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Appearance Customization Card (moved from the tab for completeness)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("appearance_settings_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "شخصی‌سازی ظاهر برنامه",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "رنگ‌بندی و تم دلخواه خود را انتخاب کنید",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "انتخاب رنگ پوسته (رنگ اصلی)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Row of color circles
                    val presets = listOf(
                        Pair("indigo", Color(0xFF4F46E5)),
                        Pair("emerald", Color(0xFF059669)),
                        Pair("ocean", Color(0xFF0284C7)),
                        Pair("rose", Color(0xFFDB2777)),
                        Pair("pink", Color(0xFFEC4899)),
                        Pair("sunset", Color(0xFFD97706)),
                        Pair("amethyst", Color(0xFF7C3AED))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        presets.forEach { (presetKey, presetColor) ->
                            val isCurrent = selectedTheme == presetKey
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(presetColor)
                                    .clickable { viewModel.updateThemePreset(presetKey) }
                                    .testTag("color_preset_$presetKey"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "انتخاب شده",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "حالت نمایش پوسته",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Styled segmented options for theme modes
                    val modes = listOf(
                        Pair("system", "سیستم"),
                        Pair("light", "روشن"),
                        Pair("dark", "تاریک")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        modes.forEach { (modeKey, modeTitle) ->
                            val isCurrent = themeMode == modeKey
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.updateThemeMode(modeKey) }
                                    .testTag("theme_mode_$modeKey"),
                                color = if (isCurrent) MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = if (isCurrent) null else CardDefaults.outlinedCardBorder().copy()
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = modeTitle,
                                        fontSize = 13.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCurrent) MaterialTheme.colorScheme.onPrimary 
                                               else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "چیدمان دسته‌بندی‌های آموزشی",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val layoutModes = listOf(
                        Pair("list", "لیست ساده"),
                        Pair("grid", "شبکه‌ای (۲ ستونه)")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        layoutModes.forEach { (layoutKey, layoutTitle) ->
                            val isCurrent = categoriesLayoutMode == layoutKey
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.updateCategoriesLayoutMode(layoutKey) }
                                    .testTag("categories_layout_mode_$layoutKey"),
                                color = if (isCurrent) MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = if (isCurrent) null else CardDefaults.outlinedCardBorder().copy()
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = layoutTitle,
                                        fontSize = 13.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCurrent) MaterialTheme.colorScheme.onPrimary 
                                               else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Background Customization Card
        item {
            val backgroundType by viewModel.backgroundType.collectAsState()
            val customBackgroundUri by viewModel.customBackgroundUri.collectAsState()
            val context = LocalContext.current
            
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                if (uri != null) {
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        // ignore
                    }
                    viewModel.updateCustomBackgroundUri(uri.toString())
                    viewModel.updateBackgroundType("custom")
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("background_settings_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "پس‌زمینه صفحات (طرح و تصویر)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "یک پترن زیبا یا تصویر دلخواه برای پس‌زمینه انتخاب کنید",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "انتخاب پترن یا تصویر ساده:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Row of pattern items
                    val patternOptions = listOf(
                        Pair("none", "ساده"),
                        Pair("pattern_1", "پترن ۱"),
                        Pair("pattern_2", "پترن ۲"),
                        Pair("pattern_3", "پترن ۳"),
                        Pair("pattern_4", "پترن ۴"),
                        Pair("pattern_5", "پترن ۵")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val themePrimary = MaterialTheme.colorScheme.primary
                        val themeOnSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

                        patternOptions.forEach { (typeKey, title) ->
                            val isCurrent = backgroundType == typeKey
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isCurrent) themePrimary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    )
                                    .border(
                                        width = if (isCurrent) 2.dp else 1.dp,
                                        color = if (isCurrent) themePrimary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.updateBackgroundType(typeKey) }
                                    .testTag("background_pattern_$typeKey"),
                                contentAlignment = Alignment.Center
                            ) {
                                // Draw a miniature of the pattern in the box
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val clr = if (isCurrent) themePrimary else themeOnSurfaceVariant
                                    val alpha = if (isCurrent) 0.25f else 0.15f
                                    val widthVal = this.size.width
                                    val heightVal = this.size.height
                                    when (typeKey) {
                                        "pattern_1" -> {
                                            // Dots Grid Mini
                                            val sp = 8.dp.toPx()
                                            val r = 1.dp.toPx()
                                            for (cx in 0..(widthVal / sp).toInt()) {
                                                for (cy in 0..(heightVal / sp).toInt()) {
                                                    drawCircle(clr.copy(alpha = alpha), r, Offset(cx * sp, cy * sp))
                                                }
                                            }
                                        }
                                        "pattern_2" -> {
                                            // Diagonal Mini
                                            val sp = 8.dp.toPx()
                                            var off = 0f
                                            while (off < widthVal + heightVal) {
                                                drawLine(clr.copy(alpha = alpha), Offset(off, 0f), Offset(0f, off), strokeWidth = 1f)
                                                off += sp
                                            }
                                        }
                                        "pattern_3" -> {
                                            // Crosses Mini
                                            val sp = 12.dp.toPx()
                                            for (cx in 0..(widthVal / sp).toInt()) {
                                                for (cy in 0..(heightVal / sp).toInt()) {
                                                    val x = cx * sp + (if (cy % 2 == 0) 0f else sp/2)
                                                    val y = cy * sp
                                                    drawLine(clr.copy(alpha = alpha), Offset(x - 2f, y), Offset(x + 2f, y), strokeWidth = 1f)
                                                    drawLine(clr.copy(alpha = alpha), Offset(x, y - 2f), Offset(x, y + 2f), strokeWidth = 1f)
                                                }
                                            }
                                        }
                                        "pattern_4" -> {
                                            // Waves Mini
                                            val sp = 10.dp.toPx()
                                            for (cy in 0..(heightVal / sp).toInt()) {
                                                val baseY = cy * sp
                                                val p = androidx.compose.ui.graphics.Path()
                                                p.moveTo(0f, baseY)
                                                for (i in 0..10) {
                                                    val x = i * (widthVal / 10f)
                                                    val y = baseY + kotlin.math.sin((x / widthVal) * 2 * kotlin.math.PI).toFloat() * 2f
                                                    p.lineTo(x, y)
                                                }
                                                drawPath(p, clr.copy(alpha = alpha), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f))
                                            }
                                        }
                                        "pattern_5" -> {
                                            // Rings Mini
                                            val r = 6.dp.toPx()
                                            val sp = 10.dp.toPx()
                                            for (cx in 0..(widthVal / sp).toInt()) {
                                                for (cy in 0..(heightVal / sp).toInt()) {
                                                    drawCircle(clr.copy(alpha = alpha), r, Offset(cx * sp, cy * sp), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.5f))
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                if (typeKey == "none") {
                                    Text(text = "ساده", fontSize = 10.sp, color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    if (isCurrent) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val isCustom = backgroundType == "custom"
                    Button(
                        onClick = {
                            launcher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("pick_custom_background_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCustom) MaterialTheme.colorScheme.primary 
                                            else MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = if (isCustom) MaterialTheme.colorScheme.onPrimary 
                                           else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCustom && customBackgroundUri.isNotEmpty()) "تصویر گالری انتخاب شد 🎨" else "انتخاب تصویر دلخواه از گالری",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isCustom && customBackgroundUri.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "یک تصویر شخصی در گالری تنظیم شده است.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "❌ حذف تصویر",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.updateCustomBackgroundUri("")
                                        viewModel.updateBackgroundType("none")
                                    }
                                    .testTag("remove_custom_background_button")
                            )
                        }
                    }
                }
            }
        }

        // Conditional-free Learning Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("conditional_free_settings_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "آموزش بدون حالت شرط",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "فعال‌سازی تمامی دوره‌ها بدون نیاز به انجام آزمون‌ها",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                        
                        Switch(
                            checked = conditionalFreeLearning,
                            onCheckedChange = { viewModel.updateConditionalFreeLearning(it) },
                            modifier = Modifier.testTag("conditional_free_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        // Replay Onboarding Tutorial Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("replay_tutorial_settings_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "راهنمای تعاملی شروع کار",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "نمایش مجدد معرفی بخش‌ها و آموزش استفاده از برنامه",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.updateOnboardingCompleted(false) },
                            modifier = Modifier.testTag("replay_tutorial_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("نمایش", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressTimeline(categories: List<CategorySession>) {
    val totalCompleted = categories.sumOf { it.completedSentencesCount }
    val totalCount = categories.sumOf { it.totalSentencesCount }.coerceAtLeast(1)
    val progressPercentage = (totalCompleted * 100) / totalCount

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("progress_timeline"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "درصد پیشرفت: $progressPercentage٪",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal scrollable timeline of milestones
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                itemsIndexed(categories) { index, category ->
                    val isCompleted = category.isCompleted
                    val isActive = !isCompleted && (index == 0 || categories[index - 1].isCompleted)
                    val isLocked = !isCompleted && !isActive

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Node column
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(85.dp)
                        ) {
                            // Badge Icon
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCompleted -> Color(0xFF10B981) // Completed
                                            isActive -> MaterialTheme.colorScheme.primary // Active
                                            else -> MaterialTheme.colorScheme.surfaceVariant // Locked
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else if (isActive) {
                                    Icon(
                                        imageVector = getCategoryIcon(category.id),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Category Name
                            Text(
                                text = category.title,
                                fontSize = 10.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isCompleted -> Color(0xFF047857)
                                    isActive -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )

                            // Status text
                            Text(
                                text = when {
                                    isCompleted -> "۱۰۰٪ کامل"
                                    isActive -> "آموزش فعال"
                                    else -> "قفل"
                                },
                                fontSize = 8.sp,
                                color = when {
                                    isCompleted -> Color(0xFF10B981)
                                    isActive -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                },
                                textAlign = TextAlign.Center
                            )
                        }

                        // Connecting line to next node (except for last node)
                        if (index < categories.size - 1) {
                            val nextCategory = categories[index + 1]
                            val isLineCompleted = isCompleted && nextCategory.completedSentencesCount > 0
                            val lineColor = when {
                                isLineCompleted -> Color(0xFF10B981)
                                isCompleted -> Color(0xFF10B981).copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(3.dp)
                                    .background(lineColor)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppBackground(
    viewModel: LearningViewModel,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val backgroundType by viewModel.backgroundType.collectAsState()
    val customBackgroundUri by viewModel.customBackgroundUri.collectAsState()
    val themePrimary = MaterialTheme.colorScheme.primary
    val themeBg = MaterialTheme.colorScheme.background

    Box(modifier = modifier.fillMaxSize()) {
        // Draw the background layer
        if (backgroundType == "custom" && customBackgroundUri.isNotEmpty()) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val painter = coil.compose.rememberAsyncImagePainter(
                model = coil.request.ImageRequest.Builder(context)
                    .data(customBackgroundUri)
                    .crossfade(true)
                    .build()
            )
            androidx.compose.foundation.Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            // Add a semi-transparent screen color tint over the user's custom image to keep text readable
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(themeBg.copy(alpha = 0.75f))
            )
        } else if (backgroundType != "none") {
            // Draw a solid subtle container tint
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(themeBg)
                    .drawBehind {
                        val stroke1 = 1.dp.toPx()
                        val stroke12 = 1.2.dp.toPx()
                        val spacing24 = 24.dp.toPx()
                        val spacing32 = 32.dp.toPx()
                        val spacing40 = 40.dp.toPx()
                        val spacing48 = 48.dp.toPx()
                        val radius24 = 24.dp.toPx()
                        val radius15 = 1.5.dp.toPx()
                        val crossSize = 4.dp.toPx()
                        val widthVal = this.size.width
                        val heightVal = this.size.height

                        when (backgroundType) {
                            "pattern_1" -> {
                                val dotColor = themePrimary.copy(alpha = 0.08f)
                                val cols = (widthVal / spacing24).toInt() + 1
                                val rows = (heightVal / spacing24).toInt() + 1
                                for (c in 0..cols) {
                                    for (r in 0..rows) {
                                        drawCircle(
                                            color = dotColor,
                                            radius = radius15,
                                            center = Offset(c * spacing24, r * spacing24)
                                        )
                                    }
                                }
                            }
                            "pattern_2" -> {
                                val lineColor = themePrimary.copy(alpha = 0.04f)
                                val maxDim = widthVal + heightVal
                                var offset = 0f
                                while (offset < maxDim) {
                                    drawLine(
                                        color = lineColor,
                                        start = Offset(offset, 0f),
                                        end = Offset(0f, offset),
                                        strokeWidth = stroke1
                                    )
                                    offset += spacing32
                                }
                            }
                            "pattern_3" -> {
                                val crossColor = themePrimary.copy(alpha = 0.06f)
                                val cols = (widthVal / spacing40).toInt() + 1
                                val rows = (heightVal / spacing40).toInt() + 1
                                for (c in 0..cols) {
                                    for (r in 0..rows) {
                                        val cx = c * spacing40 + (if (r % 2 == 0) 0f else spacing40 / 2)
                                        val cy = r * spacing40
                                        drawLine(
                                            color = crossColor,
                                            start = Offset(cx - crossSize, cy),
                                            end = Offset(cx + crossSize, cy),
                                            strokeWidth = stroke1
                                        )
                                        drawLine(
                                            color = crossColor,
                                            start = Offset(cx, cy - crossSize),
                                            end = Offset(cx, cy + crossSize),
                                            strokeWidth = stroke1
                                        )
                                    }
                                }
                            }
                            "pattern_4" -> {
                                val waveColor = themePrimary.copy(alpha = 0.05f)
                                val rows = (heightVal / spacing48).toInt() + 1
                                val pointsCount = 40
                                val stepX = widthVal / pointsCount
                                for (r in 0..rows) {
                                    val baseY = r * spacing48
                                    val path = androidx.compose.ui.graphics.Path()
                                    path.moveTo(0f, baseY)
                                    for (i in 0..pointsCount) {
                                        val x = i * stepX
                                        val y = baseY + kotlin.math.sin((x / widthVal) * 4 * kotlin.math.PI).toFloat() * 10f
                                        path.lineTo(x, y)
                                    }
                                    drawPath(
                                        path = path,
                                        color = waveColor,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke12)
                                    )
                                }
                            }
                            "pattern_5" -> {
                                val ringColor = themePrimary.copy(alpha = 0.04f)
                                val spacingX = 40.dp.toPx()
                                val spacingY = 40.dp.toPx()
                                val cols = (widthVal / spacingX).toInt() + 1
                                val rows = (heightVal / spacingY).toInt() + 1
                                val strokeStyle = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke1)
                                for (c in 0..cols) {
                                    for (r in 0..rows) {
                                        drawCircle(
                                            color = ringColor,
                                            radius = radius24,
                                            center = Offset(c * spacingX, r * spacingY),
                                            style = strokeStyle
                                        )
                                    }
                                }
                            }
                        }
                    }
            )
        }

        // Screen content on top of background
        content()
    }
}

@Composable
fun GuideScreen(viewModel: LearningViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("guide_screen_lazy_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero / Introduction Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "آموزش کامل کار با نرم‌افزار",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "به راهنمای جامع یادگیری ۷۰۰ جمله ضروری انگلیسی خوش آمدید! این برنامه با هدف تسلط کامل شما روی مکالمات و جملات فوق‌کاربردی انگلیسی طراحی شده است. در ادامه بخش‌های اصلی نرم‌افزار آموزش داده می‌شود.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Section 1: Learning Flow
        item {
            GuideSectionCard(
                icon = Icons.Default.Book,
                title = "۱. بخش آموزش جلسات (Learning)",
                description = "در این بخش، جملات کاربردی را به تفکیک دسته‌بندی و جلسات یاد می‌گیرید:\n" +
                        "• گوش دادن به تلفظ با زدن روی دکمه بلندگو 🔊\n" +
                        "• مشاهده معنی فارسی و ساختار انگلیسی جمله\n" +
                        "• علامت‌گذاری جملات برای تمرین بیشتر\n" +
                        "• مرور مستمر جملات هر جلسه تا یادگیری کامل تلفظ و معنی"
            )
        }

        // Section 2: Quizzes
        item {
            GuideSectionCard(
                icon = Icons.Default.CastForEducation,
                title = "۲. آزمون‌های تستی هوشمند (Quizzes)",
                description = "برای باز کردن جلسات جدید و سنجش یادگیری خود، در آزمون‌ها شرکت کنید:\n" +
                        "• آزمون‌های چهارگزینه‌ای فارسی به انگلیسی و انگلیسی به فارسی\n" +
                        "• سوالات جای خالی (Fill-in-the-blank) برای تمرین املا و لغات کلیدی\n" +
                        "• نیاز به کسب نمره قبولی برای باز شدن درس‌های بعدی به صورت ترتیبی\n" +
                        "• امکان فعال‌سازی آموزش بدون شرط در تنظیمات برای باز بودن تمامی درس‌ها"
            )
        }

        // Section 3: Speaking Quiz
        item {
            GuideSectionCard(
                icon = Icons.Default.Mic,
                title = "۳. آزمون مکالمه هوشمند (Speaking Quiz)",
                description = "قوی‌ترین بخش برای تقویت مهارت صحبت کردن و تلفظ شما:\n" +
                        "• با زدن روی نماد میکروفون، جمله انگلیسی نمایش‌داده شده را با صدای بلند بخوانید 🎙\n" +
                        "• موتور هوشمند تشخیص گفتار، تلفظ شما را آنالیز کرده و به آن امتیاز می‌دهد\n" +
                        "• برای قبولی و عالی شدن باید جمله را کاملاً صحیح تلفظ کنید\n" +
                        "• تقویت خارق‌العاده لهجه و اعتماد به نفس شما در صحبت کردن"
            )
        }

        // Section 4: Settings & Customization
        item {
            GuideSectionCard(
                icon = Icons.Default.Settings,
                title = "۴. تنظیمات و شخصی‌سازی (Settings)",
                description = "برنامه را مطابق با سلیقه و عادت‌های مطالعه خود سفارشی کنید:\n" +
                        "• انتخاب تم رنگی دلخواه (آبی، صورتی، پرتقالی، سبز، ایندیگو و ...)\n" +
                        "• تغییر پوسته بین روشن، تاریک یا هماهنگ با سیستم\n" +
                        "• تنظیم یادآور مطالعه روزانه در ساعت خاص برای استمرار آموزش ⏰\n" +
                        "• شخصی‌سازی پس‌زمینه صفحات: انتخاب از بین پترن‌های زیبای پیش‌فرض یا تنظیم عکس شخصی از گالری تلفن همراه!"
            )
        }

        // Section 5: Tips & Progress
        item {
            GuideSectionCard(
                icon = Icons.Default.Lightbulb,
                title = "۵. نکات کلیدی برای موفقیت",
                description = "• هر روز حداقل ۱۵ دقیقه با برنامه تمرین کنید (استمرار راز موفقیت است).\n" +
                        "• حتماً جملات را با صدای بلند تکرار کنید تا عضلات گفتاری شما به انگلیسی عادت کنند.\n" +
                        "• بخش درصد پیشرفت در صفحه اصلی، میزان تکمیل بودن کل دوره شما را به صورت دقیق نشان می‌دهد.\n" +
                        "• در صورت نیاز به بازنشانی یا شروع مجدد، می‌توانید از بخش تنظیمات پیشرفت خود را صفر کنید."
            )
        }

        // Footer version item
        item {
            Text(
                text = "۷۰۰ جمله ضروری انگلیسی • نسخه ۱.۲.۰",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
fun GuideSectionCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun OnboardingTutorialOverlay(viewModel: LearningViewModel) {
    var currentStep by remember { mutableStateOf(0) }
    val stepsCount = 5

    Surface(
        modifier = Modifier.fillMaxSize().testTag("onboarding_overlay_surface"),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header (Skip Button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "راهنمای تعاملی شروع کار",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(
                        onClick = { viewModel.updateOnboardingCompleted(true) },
                        modifier = Modifier.testTag("skip_onboarding_button")
                    ) {
                        Text(
                            text = "رد کردن آموزش",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    repeat(stepsCount) { step ->
                        if (currentStep == step) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Image / Icon
                                Box(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (step) {
                                            0 -> Icons.Default.School
                                            1 -> Icons.Default.MenuBook
                                            2 -> Icons.Default.Mic
                                            3 -> Icons.Default.WorkspacePremium
                                            else -> Icons.Default.Settings
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Title
                                Text(
                                    text = when (step) {
                                        0 -> "به ۷۰۰ جمله ضروری انگلیسی خوش آمدید! 🇬🇧✨"
                                        1 -> "۲۱ دسته‌بندی موضوعی و سازمان‌دهی شده 🗂️"
                                        2 -> "تلفظ صوتی آفلاین و آزمون‌های هوشمند 🗣️"
                                        3 -> "سنجش و آزمون هوشمند مکالمه صوتی 🎙️"
                                        else -> "تم‌های زیبا، حالت تاریک و یادآور هوشمند 🎨🔔"
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Description
                                Text(
                                    text = when (step) {
                                        0 -> "این نرم‌افزار به شما کمک می‌کند تا روی کاربردی‌ترین مکالمات روزمره انگلیسی به صورت ۱۰۰٪ آفلاین و رایگان مسلط شوید. یادگیری را از همین امروز شروع کنید!"
                                        1 -> "جملات برنامه در ۲۱ دسته‌بندی ضروری (احوالپرسی، خرید، مسافرت، رستوران، جهت‌یابی و...) تدوین شده‌اند که هر دسته دارای ۵ جلسه آموزشی با متد آموزشی نوین است."
                                        2 -> "در بخش یادگیری، تلفظ صوتی باکیفیت هر جمله به صورت آفلاین، به همراه معنی روان فارسی قابل دسترسی است. پس از پایان هر جلسه، می‌توانید با آزمون چندگزینه‌ای دانشتان را تست کنید."
                                        3 -> "یک ویژگی شگفت‌انگیز: با رفتن به بخش «آزمون مکالمه هوشمند»، می‌توانید جمله انگلیسی را با صدای خود تلفظ کنید! برنامه با استفاده از میکروفون، لحن و لهجه شما را ارزیابی می‌کند."
                                        else -> "برنامه را شخصی‌سازی کنید! از ۶ پالت رنگی جذاب استفاده کنید، حالت تاریک (Dark Mode) را فعال نمایید یا یادآور روزانه مطالعه را روشن کنید تا با نوتیفیکیشن صوتی به شما یادآوری شود."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 24.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Steps Indicator (dots)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(stepsCount) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (index == currentStep) 10.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentStep) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.testTag("onboarding_back_button").weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("قبلی", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    // Next/Finish Button
                    Button(
                        onClick = {
                            if (currentStep < stepsCount - 1) {
                                currentStep++
                            } else {
                                viewModel.updateOnboardingCompleted(true)
                            }
                        },
                        modifier = Modifier.testTag("onboarding_next_button").weight(1.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (currentStep == stepsCount - 1) "بزن بریم! 🚀" else "بعدی",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InitialThemeSelectionOverlay(viewModel: LearningViewModel) {
    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Automatically pre-select the beautiful girl-friendly "pink" theme on opening
    LaunchedEffect(Unit) {
        if (selectedTheme == "indigo") {
            viewModel.updateThemePreset("pink")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize().testTag("theme_selection_overlay_surface"),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "شخصی‌سازی و انتخاب تم برنامه 🎨",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "پوسته نرم‌افزار را به سلیقه خود انتخاب کنید. تم صورتی ترکیب با سفید به عنوان گزینه پیش‌فرض و ویژه دخترخانم‌ها فعال شده است! با ضربه روی گزینه‌ها، تغییرات را به صورت زنده پشت کادر مشاهده کنید.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Scrollable list of beautiful themes
                val themeOptions = listOf(
                    Triple("pink", "صورتی رؤیایی (سفید-صورتی) 🌸", Color(0xFFEC4899)),
                    Triple("indigo", "نیلی سلطنتی (کلاسیک) 💙", Color(0xFF4F46E5)),
                    Triple("emerald", "سبز زمردی (آرامش طبیعت) 💚", Color(0xFF059669)),
                    Triple("ocean", "آبی اقیانوسی (عمق دریا) 🐳", Color(0xFF0284C7)),
                    Triple("rose", "گلی رز (مدرن و پرانرژی) ❤️", Color(0xFFDB2777)),
                    Triple("sunset", "طلایی غروب (گرم و درخشان) 💛", Color(0xFFD97706)),
                    Triple("amethyst", "بنفش کریستالی (شیک و خاص) 💜", Color(0xFF7C3AED))
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    themeOptions.forEach { (presetKey, displayName, color) ->
                        val isSelected = selectedTheme == presetKey
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) color.copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) color else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    viewModel.updateThemePreset(presetKey)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Color Indicator
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(color),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "انتخاب شده",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.updateInitialThemeCompleted(true)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_initial_theme_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "ثبت و ورود به برنامه 🚀",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
