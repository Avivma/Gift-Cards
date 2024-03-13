package com.example.composefirsttry.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.composefirsttry.giftcard.logic.GiftCardDatabase
import com.example.composefirsttry.giftcard.logic.cardsmetadata.db.MetadataCardsDbHandler
import com.example.composefirsttry.utils.SPKeys
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class EncryptedSp

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

    @EncryptedSp
    @Singleton
    @Provides ()
    fun provideEncryptedSharedPreferences(context: Context): SharedPreferences {
        //more info: https://medium.com/@Naibeck/android-security-encryptedsharedpreferences-ea239e717e5f
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            SPKeys.ENCRYPTED_SHARED_PREFERENCES_NAME, // fileName
            masterKeyAlias, // masterKeyAlias
            context, // context
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // prefKeyEncryptionScheme
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // prefvalueEncryptionScheme
        )
//        ) as EncryptedSharedPreferences
    }

    @Singleton
    @Provides
    fun provideGiftCardDatabase(context: Context): GiftCardDatabase {
        return Room.databaseBuilder(context, GiftCardDatabase::class.java, GiftCardDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

    }

    @Singleton
    @Provides
    fun provideMetadataCardsHandler(sp: SharedPreferences): MetadataCardsDbHandler {
        val handler = MetadataCardsDbHandler(sp, Gson())
        handler.buildFromSp()
        return handler
    }
}