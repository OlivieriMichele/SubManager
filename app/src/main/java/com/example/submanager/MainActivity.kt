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
    private val viewModel: SubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark = viewModel.isDark.value
            val navController = rememberNavController()

            SubManagerTheme(darkTheme = isDark, dynamicColor = false) {
                SubNavigation(navController, viewModel)
            }
        }
    }
}