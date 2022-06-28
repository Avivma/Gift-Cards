package com.example.composefirsttry.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.composefirsttry.utils.SPKeys;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public Context provideContext(){
        return context;
    }

    @Singleton
    @Provides
    public SharedPreferences provideSharedPreferences(Context context){
        return context.getSharedPreferences(SPKeys.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
