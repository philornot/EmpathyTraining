package com.empathytraining.data.di

import android.content.Context
import com.empathytraining.data.database.EmpathyDao
import com.empathytraining.data.database.EmpathyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Hilt module for providing database-related dependencies */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEmpathyDatabase(@ApplicationContext context: Context): EmpathyDatabase {
        return EmpathyDatabase.getDatabase(context)
    }

    @Provides
    fun provideEmpathyDao(database: EmpathyDatabase): EmpathyDao {
        return database.empathyDao()
    }
}