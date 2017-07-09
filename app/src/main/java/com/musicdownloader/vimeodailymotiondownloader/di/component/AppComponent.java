package com.musicdownloader.vimeodailymotiondownloader.di.component;

import android.content.Context;
import android.content.SharedPreferences;

import com.musicdownloader.vimeodailymotiondownloader.di.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by Hanh Nguyen on 7/7/2017.
 */
@Singleton
@Component(
        modules = {AppModule.class}
)
public interface AppComponent {
    Context context();
    OkHttpClient okHttpClient();
    Retrofit retrofit();
    SharedPreferences sharedPreferences();
}
