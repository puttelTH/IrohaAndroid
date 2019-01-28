package com.puttel.app;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.puttel.app.data.Account;
import com.puttel.app.injection.ApplicationComponent;
import com.puttel.app.injection.DaggerApplicationComponent;
import lombok.Getter;

public class InApp extends Application {
    public static InApp instance;
    public Account account;
    @Getter
    @VisibleForTesting
    public ApplicationComponent applicationComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applicationComponent = DaggerApplicationComponent.builder().build();
        Logger.addLogAdapter(new AndroidLogAdapter());
    }
}
