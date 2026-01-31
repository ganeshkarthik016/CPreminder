package com.example.cpreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.work.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import com.example.cpreminder.ui.theme.CPReminderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDailyAlarm()
        setContent {
            CPReminderTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CPReminderDashboard()
                }
            }
        }
    }

    private fun setupDailyAlarm() {
        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyAlarmWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.MINUTES) // Set this to real 10:30 PM delay for production
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyCPAlarm",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }
}

@Composable
fun CPReminderDashboard() {
    var rating by remember { mutableStateOf("----") }
    var streak by remember { mutableStateOf(0) }
    var handleInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(top = 64.dp, start = 16.dp, end = 16.dp)) {
        Text("CP STREAK TRACKER", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = handleInput,
            onValueChange = { handleInput = it },
            label = { Text("Enter CF Handle") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scope.launch {
                    try {
                        val userRes = RetrofitClient.instance.getUserInfo(handleInput)
                        rating = userRes.result.firstOrNull()?.rating?.toString() ?: "0"

                        val statusRes = RetrofitClient.instance.getUserStatus(handleInput)
                        val lastSub = statusRes.result.firstOrNull()?.creationTimeSeconds ?: 0L
                        if ((System.currentTimeMillis() / 1000) - lastSub < 86400) {
                            streak = 1 // Basic logic: if solved in last 24h, streak is active
                        }
                    } catch (e: Exception) { rating = "Error" }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Update My Stats") }

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Rating: $rating", style = MaterialTheme.typography.titleLarge)
                Text("Streak: $streak Days", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("P31 Submission Paper", style = MaterialTheme.typography.titleMedium)


        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.height(250.dp)) {
            items(31) { index ->
                Box(modifier = Modifier.padding(2.dp).aspectRatio(1f).background(if (index < streak) Color(0xFF4CAF50) else Color.LightGray))
            }
        }
    }
}
