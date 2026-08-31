package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.repository.OfflinePrayerRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MosqueAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var offlinePrayerRepository: OfflinePrayerRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserPreferencesRepository.initialize(offlinePrayerRepository)
        enableEdgeToEdge()
        setContent {
            MosqueAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

