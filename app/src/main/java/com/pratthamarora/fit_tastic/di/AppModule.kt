package com.pratthamarora.fit_tastic.di

import android.content.Context
import androidx.room.Room
import com.pratthamarora.fit_tastic.db.RunDatabase
import com.pratthamarora.fit_tastic.utils.Constants.RUN_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        RunDatabase::class.java,
        RUN_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(runDatabase: RunDatabase) = runDatabase.getRunDao()
}