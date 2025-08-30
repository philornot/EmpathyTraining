package com.empathytraining

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.empathytraining.data.repository.EmpathyRepository
import com.empathytraining.ui.navigation.EmpathyNavigation
import com.empathytraining.ui.theme.EmpathyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Main Activity for the Empathy Training app.
 *
 * Sets up the Compose UI with Material 3 theme and navigation. Handles
 * user progress initialization and provides the main entry point.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var empathyRepository: EmpathyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            EmpathyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background
                    ) { paddingValues ->
                        EmpathyNavigation(
                            repository = empathyRepository,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            try {
                val userProgress = empathyRepository.getUserProgressSingle()
                if (userProgress == null) {
                    empathyRepository.initializeUserProgress()
                    Timber.d("User progress initialized for new user")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize user progress")
            }
        }
    }
}