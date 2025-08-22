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
 * - EmpathyScenario: Predefined empathy scenarios with example responses
 * - UserResponse: User's responses to scenarios with metadata
 * - UserProgress: User's overall progress tracking and statistics
 */
@Database(
    entities = [EmpathyScenario::class, UserResponse::class, UserProgress::class],
    version = 1,
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
                // Insert initial scenarios
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
         * Get the list of initial empathy scenarios These scenarios cover various
         * life situations requiring empathetic responses
         *
         * @return List of predefined empathy scenarios
         */
        private fun getInitialScenarios(): List<EmpathyScenario> {
            Timber.d("Creating initial scenarios list")

            return listOf(
                // WORK CATEGORY - Professional and workplace scenarios
                EmpathyScenario(
                    scenarioText = "I'm so exhausted from this job. I work 10-hour days and barely have time for anything else.",
                    exampleResponse = "That sounds incredibly draining. Working such long hours must be taking a real toll on you. Have you been able to talk to anyone about finding a better work-life balance?",
                    category = "work",
                    difficultyLevel = 2
                ),

                EmpathyScenario(
                    scenarioText = "My boss never appreciates anything I do. I put in so much effort but it feels invisible.",
                    exampleResponse = "It must be so frustrating to work hard and not feel recognized for your efforts. Feeling invisible at work can be really discouraging. Your contributions do matter, even if they're not being acknowledged right now.",
                    category = "work",
                    difficultyLevel = 2
                ),

                EmpathyScenario(
                    scenarioText = "I got passed over for the promotion again. I don't understand what I'm doing wrong.",
                    exampleResponse = "That must be such a disappointment, especially when you've been working toward it. Not getting the promotion you deserve can feel really deflating. Have you been able to get any feedback about what might help you in future opportunities?",
                    category = "work",
                    difficultyLevel = 3
                ),

                // RELATIONSHIPS CATEGORY - Romantic and interpersonal relationships
                EmpathyScenario(
                    scenarioText = "My partner and I keep fighting about the same things over and over. Nothing seems to get resolved.",
                    exampleResponse = "Repeating the same arguments can be so exhausting and frustrating for both of you. It sounds like you're both stuck in a pattern that isn't working. Sometimes talking with a counselor can help break those cycles and find new ways to communicate.",
                    category = "relationships",
                    difficultyLevel = 4
                ),

                EmpathyScenario(
                    scenarioText = "I feel like my partner doesn't really listen to me anymore. We're growing apart.",
                    exampleResponse = "Feeling unheard by someone you love can be really lonely and painful. It's hard when you sense that distance growing. Have you been able to share with your partner how you're feeling about the communication between you two?",
                    category = "relationships",
                    difficultyLevel = 3
                ),

                EmpathyScenario(
                    scenarioText = "I'm going through a breakup and I feel like my world is falling apart.",
                    exampleResponse = "Breakups can feel absolutely devastating, especially when that person was such a big part of your life. It makes sense that everything feels overwhelming right now. These feelings are so valid, and healing takes time. You don't have to go through this alone.",
                    category = "relationships",
                    difficultyLevel = 4
                ),

                // FAMILY CATEGORY - Family dynamics and relationships
                EmpathyScenario(
                    scenarioText = "My parents still treat me like a child even though I'm 25. They question every decision I make.",
                    exampleResponse = "That must feel so frustrating when you're trying to establish your independence as an adult. It can be hard when parents struggle to adjust to the relationship changing as you grow up. Your feelings about wanting more respect for your autonomy are completely understandable.",
                    category = "family",
                    difficultyLevel = 3
                ),

                EmpathyScenario(
                    scenarioText = "My teenage daughter won't talk to me anymore. I feel like I'm losing her.",
                    exampleResponse = "That must be so heartbreaking as a parent. The teenage years can create such distance, and it's natural to feel worried about your connection with her. Your love for her comes through clearly, and that foundation matters even when communication is difficult.",
                    category = "family",
                    difficultyLevel = 4
                ),

                EmpathyScenario(
                    scenarioText = "I'm the only one who takes care of my aging parents. My siblings never help.",
                    exampleResponse = "Carrying that responsibility largely on your own must be incredibly overwhelming and exhausting. It's understandable to feel resentful when the care isn't shared equally. You're doing something really important and difficult, and it's okay to feel frustrated about the lack of support.",
                    category = "family",
                    difficultyLevel = 4
                ),

                // PERSONAL CATEGORY - Self-worth, identity, personal struggles
                EmpathyScenario(
                    scenarioText = "I feel like I'm not good enough at anything. Everyone else seems to have it figured out.",
                    exampleResponse = "Those feelings of not measuring up can be so heavy to carry. Social media and comparing ourselves to others can make it seem like everyone else has life figured out, but that's rarely the reality. You have unique strengths and value, even when it's hard to see them.",
                    category = "personal",
                    difficultyLevel = 3
                ),

                EmpathyScenario(
                    scenarioText = "I keep making the same mistakes over and over. I feel stuck in a pattern I can't break.",
                    exampleResponse = "Breaking patterns can feel impossible, especially when you're aware of them but still find yourself repeating them. It's actually a sign of growth that you recognize the pattern. Change is hard and takes time - be patient with yourself as you work through this.",
                    category = "personal",
                    difficultyLevel = 3
                ),

                EmpathyScenario(
                    scenarioText = "I don't know what I want to do with my life. I feel lost and directionless.",
                    exampleResponse = "Feeling uncertain about your path can be really unsettling, especially when it seems like you should have answers. It's okay not to have everything figured out - many people feel this way even if they don't show it. Taking time to explore what matters to you is valuable, not wasteful.",
                    category = "personal",
                    difficultyLevel = 2
                ),

                // HEALTH CATEGORY - Physical and mental health challenges
                EmpathyScenario(
                    scenarioText = "I've been dealing with chronic pain for months and doctors can't figure out what's wrong.",
                    exampleResponse = "Living with chronic pain while not having answers must be incredibly frustrating and scary. Not knowing what's causing it adds another layer of stress to already difficult situation. Your pain is real and valid, regardless of whether doctors have found the cause yet.",
                    category = "health",
                    difficultyLevel = 4
                ),

                EmpathyScenario(
                    scenarioText = "I've been feeling really anxious lately and it's affecting everything I do.",
                    exampleResponse = "Anxiety can be so overwhelming when it starts impacting all areas of your life. It takes courage to acknowledge what you're going through. There are ways to manage anxiety, and reaching out for support - whether from friends, family, or professionals - can make a real difference.",
                    category = "health",
                    difficultyLevel = 3
                ),

                EmpathyScenario(
                    scenarioText = "I'm struggling with depression and I can barely get out of bed most days.",
                    exampleResponse = "Depression can make even the simplest tasks feel overwhelming and exhausting. Just getting through each day takes incredible strength, even when it doesn't feel like it. You deserve support and care - please consider reaching out to a mental health professional if you haven't already.",
                    category = "health",
                    difficultyLevel = 5
                ),

                // FRIENDSHIP CATEGORY - Friend relationships and social connections
                EmpathyScenario(
                    scenarioText = "I found out my best friend has been talking about me behind my back.",
                    exampleResponse = "Discovering that betrayal from someone you trusted so deeply must be incredibly painful and shocking. It's natural to feel hurt, angry, and confused. Take time to process these feelings before deciding how to handle the situation with your friend.",
                    category = "friendship",
                    difficultyLevel = 3
                ),

                EmpathyScenario(
                    scenarioText = "I feel like I don't have any real friends. I'm always the one reaching out first.",
                    exampleResponse = "That can feel so lonely and one-sided when you're always the one initiating. It's exhausting to feel like you're putting in all the effort in friendships. Your desire for more balanced, mutual friendships is completely reasonable and you deserve connections where the effort goes both ways.",
                    category = "friendship",
                    difficultyLevel = 3
                ),

                EmpathyScenario(
                    scenarioText = "My friend group excluded me from plans and I found out through social media.",
                    exampleResponse = "Finding out you were left out through social media must have been such a painful shock. Being excluded by people you consider friends is heartbreaking and confusing. Your feelings of hurt and disappointment are completely valid.",
                    category = "friendship",
                    difficultyLevel = 3
                ),

                // EDUCATION CATEGORY - School, learning, academic challenges
                EmpathyScenario(
                    scenarioText = "I'm failing my classes and I don't know how to catch up. I feel overwhelmed.",
                    exampleResponse = "Feeling behind academically can create such overwhelming stress and anxiety. It's understandable to feel lost when the work feels insurmountable. Remember that asking for help - from teachers, tutors, or counselors - is a sign of strength, not weakness.",
                    category = "education",
                    difficultyLevel = 2
                ),

                EmpathyScenario(
                    scenarioText = "I got rejected from my dream college. I don't know what to do now.",
                    exampleResponse = "College rejection, especially from your dream school, can feel like such a crushing disappointment. It's okay to grieve this loss and feel upset about it. This doesn't define your worth or your future - there are many paths to success and fulfillment.",
                    category = "education",
                    difficultyLevel = 3
                ),

                // GENERAL CATEGORY - Mixed or general life situations
                EmpathyScenario(
                    scenarioText = "Everything in my life seems to be going wrong at once.",
                    exampleResponse = "When multiple difficult things happen at the same time, it can feel absolutely overwhelming and unfair. It makes sense that you feel like you can't catch a break. Taking things one day or even one moment at a time can help when everything feels like too much.",
                    category = "general",
                    difficultyLevel = 3
                ),

                EmpathyScenario(
                    scenarioText = "I feel like nobody really understands me or what I'm going through.",
                    exampleResponse = "Feeling misunderstood can be so isolating and lonely. It's hard when you feel like you're going through something that others can't relate to or grasp. Your experiences and feelings are valid, even if others haven't walked in your shoes.",
                    category = "general",
                    difficultyLevel = 2
                ),

                EmpathyScenario(
                    scenarioText = "I'm scared about the future and all the uncertainty ahead.",
                    exampleResponse = "Uncertainty about the future can create such deep anxiety and fear. Not knowing what's coming next is one of the hardest things humans deal with. Your fears are understandable - facing an uncertain future takes courage, and you're braver than you might realize.",
                    category = "general",
                    difficultyLevel = 2
                ),

                EmpathyScenario(
                    scenarioText = "I feel like I'm always disappointing the people I care about.",
                    exampleResponse = "Carrying the weight of feeling like you're letting others down can be exhausting and heartbreaking. That pressure to meet everyone's expectations is so heavy. Remember that you're human, and the people who truly care about you understand that nobody is perfect.",
                    category = "general",
                    difficultyLevel = 3
                ),

                EmpathyScenario(
                    scenarioText = "I lost my job and I don't know how I'm going to pay my bills.",
                    exampleResponse = "Losing your job is not just about losing income - it affects your security, identity, and peace of mind. The financial stress must be overwhelming right now. This is a really tough situation, and it's okay to feel scared and uncertain about what comes next.",
                    category = "work",
                    difficultyLevel = 4
                ),

                EmpathyScenario(
                    scenarioText = "My pet died and everyone keeps saying 'it was just an animal' but I'm heartbroken.",
                    exampleResponse = "The loss of a beloved pet is a real and profound grief. Pets become family members and their loss leaves a genuine hole in your heart. Don't let anyone minimize your pain - your grief is valid and your pet's life mattered deeply.",
                    category = "personal",
                    difficultyLevel = 3
                )
            )
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