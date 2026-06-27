package com.naamjap.reminder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {

    private val viewModel: ReminderViewModel by viewModels()
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permissions for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // Initialize TextToSpeech
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }

        setContent {
            NaamJapTheme {
                MainScreen(
                    viewModel = viewModel,
                    onSpeak = { speakMantra() }
                )
            }
        }
    }

    private fun speakMantra() {
        tts?.speak("Radha Vallabh Shri Harivansh", TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }
}

// Light warm colors theme
val AmberDark = Color(0xFFB45309)
val AmberMedium = Color(0xFFD97706)
val AmberLight = Color(0xFFFEF3C7)
val Saffron = Color(0xFFF59E0B)
val StoneDark = Color(0xFF1C1917)
val StoneMedium = Color(0xFF44403C)
val StoneLight = Color(0xFFF5F5F4)
val PureWhite = Color(0xFFFFFFFF)

@Composable
fun NaamJapTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme(
        primary = AmberMedium,
        onPrimary = PureWhite,
        primaryContainer = AmberLight,
        onPrimaryContainer = AmberDark,
        background = StoneLight,
        surface = PureWhite,
        onBackground = StoneDark,
        onSurface = StoneDark,
    )

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ReminderViewModel,
    onSpeak: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Naam Jap Reminder",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = StoneDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = AmberMedium,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
                            ) {
                                Text(
                                    "OFFLINE",
                                    color = PureWhite,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            "राधा वल्लभ श्री हरिवंश | Radhavallabh",
                            color = StoneMedium,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PureWhite
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = PureWhite,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Mala") },
                    label = { Text("Mala") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberMedium,
                        selectedTextColor = AmberMedium,
                        unselectedIconColor = StoneMedium,
                        unselectedTextColor = StoneMedium,
                        indicatorColor = AmberLight
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberMedium,
                        selectedTextColor = AmberMedium,
                        unselectedIconColor = StoneMedium,
                        unselectedTextColor = StoneMedium,
                        indicatorColor = AmberLight
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Rounded.History, contentDescription = "History") },
                    label = { Text("History") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberMedium,
                        selectedTextColor = AmberMedium,
                        unselectedIconColor = StoneMedium,
                        unselectedTextColor = StoneMedium,
                        indicatorColor = AmberLight
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Rounded.Info, contentDescription = "Guide") },
                    label = { Text("Guide") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberMedium,
                        selectedTextColor = AmberMedium,
                        unselectedIconColor = StoneMedium,
                        unselectedTextColor = StoneMedium,
                        indicatorColor = AmberLight
                    )
                )
            }
        },
        containerColor = StoneLight
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> MalaScreen(viewModel, onSpeak)
                1 -> SettingsScreen(viewModel)
                2 -> HistoryScreen(viewModel)
                3 -> GuideScreen()
            }
        }
    }
}

@Composable
fun MalaScreen(
    viewModel: ReminderViewModel,
    onSpeak: () -> Unit
) {
    val completedBeads by viewModel.malaCompletedBeads.collectAsState()
    val totalRounds by viewModel.malaTotalRounds.collectAsState()
    val context = LocalContext.current

    // Set up vibration feedback
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    var animateTrigger by remember { mutableStateOf(false) }
    val scaleAnimate by animateFloatAsState(
        targetValue = if (animateTrigger) 0.93f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        finishedListener = { animateTrigger = false }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top stats card
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$completedBeads / 108",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = AmberDark
                    )
                    Text(
                        text = "Current Beads",
                        fontSize = 11.sp,
                        color = StoneMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Divider(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp),
                    color = StoneLight
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalRounds",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = AmberDark
                    )
                    Text(
                        text = "Mala Rounds",
                        fontSize = 11.sp,
                        color = StoneMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Center Mala Canvas and pad
        Box(
            modifier = Modifier
                .size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            // Draw Circular Mala beads
            Canvas(modifier = Modifier.size(260.dp)) {
                val center = this.center
                val radius = size.minDimension / 2.0f - 12.dp.toPx()
                for (i in 0 until 108) {
                    val angle = (i * 360.0 / 108.0) * (Math.PI / 180.0)
                    val x = (center.x + radius * cos(angle)).toFloat()
                    val y = (center.y + radius * sin(angle)).toFloat()

                    // Check if bead is chanted
                    val isCompleted = i < completedBeads
                    val beadColor = if (isCompleted) Color(0xFFD97706) else Color(0xFFE5E7EB)
                    val sizeRadius = if (i % 9 == 0) 5.dp.toPx() else 3.5.dp.toPx()

                    drawCircle(
                        color = beadColor,
                        radius = sizeRadius,
                        center = Offset(x, y)
                    )
                }
            }

            // Clickable Center Pad
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer(
                        scaleX = scaleAnimate,
                        scaleY = scaleAnimate
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Saffron, AmberMedium)
                        )
                    )
                    .clickable {
                        animateTrigger = true
                        // Vibrate
                        try {
                            vibrator?.vibrate(50)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        // Speak mantra
                        onSpeak()
                        // Increment
                        viewModel.chantBead()
                    }
                    .border(2.dp, PureWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "🌸",
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "राधा वल्लभ",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "श्री हरिवंश",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "TAP TO CHANT",
                        color = AmberLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.triggerTestReminder() },
                colors = ButtonDefaults.buttonColors(containerColor = AmberMedium),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Rounded.Notifications, contentDescription = "Alert")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test Reminder Firing", fontWeight = FontWeight.Bold)
            }

            TextButton(
                onClick = { viewModel.resetMala() },
                colors = ButtonDefaults.textButtonColors(contentColor = StoneMedium)
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Reset")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset Mala Chanting Count", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: ReminderViewModel) {
    val context = LocalContext.current
    val interval by viewModel.interval.collectAsState()
    val showNotification by viewModel.showNotification.collectAsState()
    val playSound by viewModel.playSound.collectAsState()
    val soundType by viewModel.soundType.collectAsState()
    val quietHoursEnabled by viewModel.quietHoursEnabled.collectAsState()
    val quietHoursStart by viewModel.quietHoursStart.collectAsState()
    val quietHoursEnd by viewModel.quietHoursEnd.collectAsState()

    var showIntervalDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Settings,
                        contentDescription = "Settings",
                        tint = AmberMedium,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Reminder & Sound Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = StoneDark
                    )
                }
            }
        }

        // Interval Setting
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                onClick = { showIntervalDialog = true },
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Reminder Interval",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = StoneDark
                    )
                    Text(
                        "Configure the repeating cycle for reminders.",
                        fontSize = 11.sp,
                        color = StoneMedium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Interval Period:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = StoneDark
                        )
                        Surface(
                            color = AmberLight,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                "$interval Minutes",
                                color = AmberDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Toggles Setting
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Reminder Actions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = StoneDark
                    )

                    // Toggle 1: Notification popup
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Show Notifications",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneDark
                            )
                            Text(
                                "Receive status popups on device",
                                fontSize = 11.sp,
                                color = StoneMedium
                            )
                        }
                        Switch(
                            checked = showNotification,
                            onCheckedChange = { viewModel.updateShowNotification(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = AmberMedium
                            )
                        )
                    }

                    Divider(color = StoneLight)

                    // Toggle 2: Play Sound
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Play Sound Alerts",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneDark
                            )
                            Text(
                                "Hear meditative audio alerts",
                                fontSize = 11.sp,
                                color = StoneMedium
                            )
                        }
                        Switch(
                            checked = playSound,
                            onCheckedChange = { viewModel.updatePlaySound(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = AmberMedium
                            )
                        )
                    }
                }
            }
        }

        // Sound Selection (Only if sound active)
        if (playSound) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Sound / Chant Options",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = StoneDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val options = listOf(
                            Triple("gong", "🌸 Temple Gong", "Resonant brass gong alert"),
                            Triple("bell", "🔔 Meditative Bell", "Soft ringing high-pitch chime"),
                            Triple("tts", "🗣️ Voice TTS", "Reads 'Radha Vallabh Shri Harivansh'")
                        )

                        options.forEach { (type, label, desc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (soundType == type) AmberLight else Color.Transparent)
                                    .clickable { viewModel.updateSoundType(type) }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = soundType == type,
                                    onClick = { viewModel.updateSoundType(type) },
                                    colors = RadioButtonDefaults.colors(selectedColor = AmberMedium)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (soundType == type) AmberDark else StoneDark
                                    )
                                    Text(
                                        desc,
                                        fontSize = 11.sp,
                                        color = StoneMedium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

        // Quiet Hours Setting
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Quiet Hours Pause",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = StoneDark
                            )
                            Text(
                                "Pause reminders during sleeping hours",
                                fontSize = 11.sp,
                                color = StoneMedium
                            )
                        }
                        Switch(
                            checked = quietHoursEnabled,
                            onCheckedChange = { viewModel.updateQuietHoursEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = AmberMedium
                            )
                        )
                    }

                    AnimatedVisibility(visible = quietHoursEnabled) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = StoneLight)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = quietHoursStart,
                                    onValueChange = { viewModel.updateQuietHoursStart(it) },
                                    label = { Text("Starts At") },
                                    shape = RoundedCornerShape(12.dp),
                                    placeholder = { Text("22:00") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AmberMedium,
                                        focusedLabelColor = AmberMedium
                                    )
                                )
                                OutlinedTextField(
                                    value = quietHoursEnd,
                                    onValueChange = { viewModel.updateQuietHoursEnd(it) },
                                    label = { Text("Ends At") },
                                    shape = RoundedCornerShape(12.dp),
                                    placeholder = { Text("06:00") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AmberMedium,
                                        focusedLabelColor = AmberMedium
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Support & Feedback Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Support & Feedback",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = StoneDark
                    )

                    // Send Feedback
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/meetdudhatt"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📷", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Send Feedback",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Report bugs, request new features, share suggestions, or contact the developer.",
                                fontSize = 11.sp,
                                color = StoneMedium
                            )
                        }
                    }

                    Divider(color = StoneLight)

                    // Business Inquiry
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:meetdudhatt@gmail.com")
                                        putExtra(Intent.EXTRA_SUBJECT, "Business Inquiry - Naam Jap Reminder")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📧", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Business Inquiry",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "For business partnerships, collaborations, sponsorships, or professional inquiries.",
                                fontSize = 11.sp,
                                color = StoneMedium
                            )
                        }
                    }

                    Divider(color = StoneLight)

                    // Report a Bug
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/meetdudhatt"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🐞", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Report a Bug",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Open Instagram to report bugs directly: https://instagram.com/meetdudhatt",
                                fontSize = 11.sp,
                                color = StoneMedium
                            )
                        }
                    }

                    Divider(color = StoneLight)

                    // Request a Feature
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/meetdudhatt"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Request a Feature",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Open Instagram to request new features: https://instagram.com/meetdudhatt",
                                fontSize = 11.sp,
                                color = StoneMedium
                            )
                        }
                    }

                    // Rate This App (Hidden until the application is published on Google Play Store)
                    /*
                    Divider(color = StoneLight)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⭐", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Rate This App",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneDark
                            )
                            Text(
                                "Rate us on Google Play Store.",
                                fontSize = 11.sp,
                                color = StoneMedium
                            )
                        }
                    }
                    */

                    Divider(color = StoneLight)

                    // Share App
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Naam Jap Reminder App")
                                        putExtra(Intent.EXTRA_TEXT, "Radha Vallabh Shri Harivansh! Maintain steady daily chanting with the Naam Jap Reminder offline app. Download it here: https://play.google.com/store/apps/details?id=com.naamjap.reminder")
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share Naam Jap Reminder"))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📤", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Share App",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StoneDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Share this app with friends and family using the Android Share Sheet.",
                                fontSize = 11.sp,
                                color = StoneMedium
                            )
                        }
                    }
                }
            }
        }

        // About Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Made with ❤️ by Meet Dudhat",
                    color = StoneMedium,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Version 1.0.0",
                    color = StoneMedium,
                    fontSize = 11.sp
                )
                Text(
                    text = "© 2026 Meet Dudhat",
                    color = StoneMedium,
                    fontSize = 11.sp
                )
            }
        }
    }

    // Interval Dialog
    if (showIntervalDialog) {
        AlertDialog(
            onDismissRequest = { showIntervalDialog = false },
            title = { Text("Select Interval Minutes", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(15, 30, 45, 60).forEach { mins ->
                        Button(
                            onClick = {
                                viewModel.updateInterval(mins)
                                showIntervalDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (interval == mins) AmberMedium else StoneLight,
                                contentColor = if (interval == mins) PureWhite else StoneDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$mins Minutes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIntervalDialog = false }) {
                    Text("Close", color = AmberMedium)
                }
            }
        )
    }
}

@Composable
fun HistoryScreen(viewModel: ReminderViewModel) {
    val logs by viewModel.chantLogs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.History,
                    contentDescription = "History",
                    tint = AmberMedium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Database Trigger History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = StoneDark
                )
            }

            if (logs.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.clearLogs() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Clear All", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⏳", fontSize = 48.sp)
                    Text(
                        "No logs generated yet.",
                        fontWeight = FontWeight.Bold,
                        color = StoneDark,
                        fontSize = 14.sp
                    )
                    Text(
                        "Logs appear automatically when the reminder timers fire or when you chant your virtual Mala beads.",
                        textAlign = TextAlign.Center,
                        color = StoneMedium,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(logs) { log ->
                    val isMala = log.type == "MALA"
                    val isSilent = log.detail.contains("Silent")
                    val bg = if (isMala) AmberLight else if (isSilent) StoneLight else PureWhite
                    val border = if (isMala) AmberMedium.copy(alpha = 0.3f) else Color.Transparent

                    Card(
                        colors = CardDefaults.cardColors(containerColor = bg),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, border, RoundedCornerShape(16.dp)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isMala) Icons.Rounded.Star else if (isSilent) Icons.Rounded.NotificationsOff else Icons.Rounded.NotificationsActive,
                                contentDescription = log.type,
                                tint = if (isMala) AmberMedium else if (isSilent) StoneMedium else AmberMedium,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = log.detail,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StoneDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
                                Text(
                                    text = sdf.format(Date(log.timestamp)),
                                    fontSize = 10.sp,
                                    color = StoneMedium,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GuideScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Naam Jap Meditative Guide",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = StoneDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Naam Jap (Repetition of the Divine Name) is an ancient, potent spiritual practice of chanting. In our tradition, chanting 'राधा वल्लभ श्री हरिवंश' connects the practitioner directly to transcendental divine love, peace, and spiritual clarity.",
                        fontSize = 12.sp,
                        color = StoneMedium,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Fully Offline-First Engine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = StoneDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Our application does not send any telemetry, analytics, or personal data to external servers. " +
                                "By combining local Jetpack DataStore preferences, private Room databases, WorkManager background workers, " +
                                "and the native Android TTS / Sound synthesizer APIs, the entire system runs locally and privately on your device.",
                        fontSize = 12.sp,
                        color = StoneMedium,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AmberLight),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Quick Chanting Tip",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AmberDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Whenever a reminder notification fires, try taking 10-20 seconds to pause your current task, " +
                                "gently close your eyes, repeat the holy name 'राधा वल्लभ श्री हरिवंश' with devotion, and tap the virtual Mala bead pad to log it. " +
                                "This creates steady spiritual discipline throughout your daily routine.",
                        fontSize = 11.5.sp,
                        color = StoneDark,
                        lineHeight = 17.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}
