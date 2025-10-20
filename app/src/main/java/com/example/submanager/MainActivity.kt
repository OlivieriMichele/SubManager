package com.example.submanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.submanager.ui.theme.SubManagerTheme
import com.example.submanager.viewModel.SubViewModel

class MainActivity : ComponentActivity() {

    // 💡 Usa la property delegation (la più stabile)
    private val viewModel: SubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Rimuovi la chiamata a viewModel() qui
            val isDark = viewModel.isDark.value
            val navController = rememberNavController()

            SubManagerTheme(darkTheme = isDark) {
                SubNavigation(navController, viewModel)
            }
        }
    }
}