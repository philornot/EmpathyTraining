package com.empathytraining.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.empathytraining.data.models.EmpathyScenario
import com.empathytraining.data.models.UserProgress
import com.empathytraining.data.models.UserResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Room Database class for Empathy Training app Manages SQLite database
 * with all entities and provides DAO access
 *
 * Database includes:
 * - EmpathyScenario: Predefined empathy scenarios with resource keys for
 *   localization
 * - UserResponse: User's responses to scenarios with metadata
 * - UserProgress: User's overall progress tracking and statistics
 */
@Database(
    entities = [EmpathyScenario::class, UserResponse::class, UserProgress::class],
    version = 2, // Incremented for schema change to support localization
    exportSchema = false // Set to true in production for database migrations
)
abstract class EmpathyDatabase : RoomDatabase() {

    /**
     * Abstract method to get the DAO instance Room will implement this
     * automatically
     */
    abstract fun empathyDao(): EmpathyDao

    companion object {
        private const val DATABASE_NAME = "empathy_training_database"

        /**
         * Singleton instance of the database Volatile ensures thread-safety for
         * the instance
         */
        @Volatile
        private var INSTANCE: EmpathyDatabase? = null

        /**
         * Get the singleton database instance Creates the database if it doesn't
         * exist, otherwise returns existing instance
         *
         * @param context Application context
         * @return Database instance
         */
        fun getDatabase(context: Context): EmpathyDatabase {
            Timber.d("Getting database instance")

            // Return existing instance if available
            return INSTANCE ?: synchronized(this) {
                // Double-check locking pattern
                val instance = INSTANCE
                if (instance != null) {
                    Timber.d("Returning existing database instance")
                    instance
                } else {
                    Timber.d("Creating new database instance")
                    val newInstance = Room.databaseBuilder(
                        context.applicationContext, EmpathyDatabase::class.java, DATABASE_NAME
                    ).addCallback(DatabaseCallback()) // Add callback for prepopulation
                        .fallbackToDestructiveMigration() // For development - remove in production
                        .build()

                    INSTANCE = newInstance
                    Timber.d("Database instance created successfully")
                    newInstance
                }
            }
        }

        /**
         * Database callback to handle database creation and prepopulation Called
         * when database is first created
         */
        private class DatabaseCallback : Callback() {

            /**
             * Called when database is created for the first time Prepopulates database
             * with initial scenarios and user progress
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Timber.d("Database created - starting prepopulation")

                // Use IO dispatcher for database operations
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val database = INSTANCE
                        if (database != null) {
                            prepopulateDatabase(database.empathyDao())
                            Timber.d("Database prepopulation completed successfully")
                        } else {
                            Timber.e("Database instance is null during prepopulation")
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error during database prepopulation")
                    }
                }
            }
        }

        /**
         * Prepopulate the database with initial scenarios and user progress Called
         * once when database is first created
         *
         * @param dao The DAO instance for database operations
         */
        private suspend fun prepopulateDatabase(dao: EmpathyDao) {
            Timber.d("Starting database prepopulation with scenarios")

            try {
                // Insert scenarios based on resource keys
                val initialScenarios = getInitialScenarios()
                dao.insertScenarios(initialScenarios)
                Timber.d("Inserted ${initialScenarios.size} initial scenarios")

                // Initialize user progress
                dao.initializeUserProgress()
                Timber.d("Initialized user progress")

            } catch (e: Exception) {
                Timber.e(e, "Error during prepopulation")
                throw e
            }
        }

        /**
         * Get the list of initial empathy scenarios using resource keys These
         * scenarios reference string resources for internationalization
         *
         * @return List of predefined empathy scenarios with resource keys
         */
        private fun getInitialScenarios(): List<EmpathyScenario> {
            Timber.d("Creating initial scenarios list from resource keys")

            return EmpathyScenario.SCENARIO_DEFINITIONS.map { definition ->
                EmpathyScenario(
                    scenarioKey = definition.scenarioKey,
                    exampleKey = definition.exampleKey,
                    category = definition.category,
                    difficultyLevel = definition.difficultyLevel,
                    isActive = true,
                    usageCount = 0
                )
            }
        }

        /**
         * Close the database instance and clean up resources Should be called when
         * app is being destroyed
         */
        fun closeDatabase() {
            Timber.d("Closing database instance")
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}