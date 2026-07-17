package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LearningViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: LearningViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = androidx.lifecycle.ViewModelProvider(this)[LearningViewModel::class.java]

        setContent {
            val selectedTheme by viewModel.selectedTheme.collectAsState()
            val themeMode by viewModel.themeMode.collectAsState()

            MyApplicationTheme(
                preset = selectedTheme,
                themeMode = themeMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::viewModel.isInitialized) {
            viewModel.billingManager.handleActivityResult(requestCode, resultCode, data) { success, message ->
                android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }
}
