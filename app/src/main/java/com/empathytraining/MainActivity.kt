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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
 * Main Activity for the Empathy Training app Sets up the Compose UI,
 * database, and navigation
 *
 * This activity serves as the entry point and handles:
 * - Theme setup with Material 3
 * - Database initialization and repository setup
 * - Navigation between different screens
 * - Edge-to-edge display for modern Android UI
 *
 * The app follows MVVM-like architecture with Repository pattern: Activity
 * -> Repository -> DAO -> Room Database
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var empathyRepository: EmpathyRepository

    /** Called when activity is created Sets up the entire app UI and data layer */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("MainActivity onCreate started")

        // Enable edge-to-edge display for modern Android UI
        enableEdgeToEdge()

        // Initialize user progress if first time user
        initializeUserProgress()

        // Set up Compose UI with theme and navigation
        setContent {
            EmpathyTheme {
                EmpathyApp()
            }
        }

        Timber.d("MainActivity onCreate completed")
    }

    /**
     * Initialize user progress if this is the first time the user opens the
     * app
     */
    private fun initializeUserProgress() {
        lifecycleScope.launch {
            try {
                val userProgress = empathyRepository.getUserProgressSingle()
                if (userProgress == null) {
                    Timber.d("First time user detected - initializing progress")
                    empathyRepository.initializeUserProgress()
                    Timber.d("User progress initialized")
                } else {
                    Timber.d("Existing user - progress already initialized")
                    Timber.d("Current streak: ${userProgress.currentStreak}, Total responses: ${userProgress.totalResponses}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error initializing user progress")
            }
        }
    }

    /**
     * Main app composable that sets up the UI structure Uses Material 3
     * Scaffold for consistent layout
     */
    @Composable
    private fun EmpathyApp() {
        Timber.d("Setting up main app UI")

        // Check if repository is initialized
        var isRepositoryReady by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            // Wait for repository initialization if needed
            if (::empathyRepository.isInitialized) {
                isRepositoryReady = true
                Timber.d("Repository is ready for UI")
            } else {
                Timber.w("Repository not yet initialized - waiting...")
                // Hilt should have injected the repository by now
                // If not, there might be a configuration issue
            }
        }

        // Main app surface with background color from theme
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            if (isRepositoryReady) {
                // Main app scaffold with navigation
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->
                    // Set up navigation with repository
                    EmpathyNavigation(
                        repository = empathyRepository,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
            } else {
                // Show loading state while repository initializes
                LoadingScreen()
            }
        }
    }

    /**
     * Simple loading screen while app initializes Shows a basic loading
     * indicator with app branding
     */
    @Composable
    private fun LoadingScreen() {
        Timber.d("Showing loading screen")

        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            // TODO: Add a proper loading indicator here
            // For now, just show empty screen with background color
            // This will be very brief during app startup
        }
    }

    /**
     * Called when activity is destroyed Clean up resources to prevent memory
     * leaks
     */
    override fun onDestroy() {
        Timber.d("MainActivity onDestroy")
        super.onDestroy()
    }

    /**
     * Called when activity is paused (user leaves app) Good place for saving
     * any unsaved data
     */
    override fun onPause() {
        Timber.d("MainActivity onPause")
        super.onPause()
    }

    /**
     * Called when activity is resumed (user returns to app) Good place for
     * refreshing data if needed
     */
    override fun onResume() {
        Timber.d("MainActivity onResume")
        super.onResume()
    }
}