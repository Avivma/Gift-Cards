package com.example.composefirsttry.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.composefirsttry.giftcard.db.GiftCardDatabase
import com.example.composefirsttry.utils.SPKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SPKeys.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideGiftCardDatabase(context: Context): GiftCardDatabase {
        return Room.databaseBuilder(context, GiftCardDatabase::class.java, GiftCardDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

    }
}