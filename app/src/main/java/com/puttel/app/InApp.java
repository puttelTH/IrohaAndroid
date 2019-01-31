package com.puttel.app;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.puttel.app.data.Account;
import com.puttel.app.injection.ApplicationComponent;
import com.puttel.app.injection.DaggerApplicationComponent;
import com.puttel.app.iroha.IrohaAPI;
import com.puttel.app.iroha.Query;
import iroha.protocol.QryResponses;
import iroha.protocol.Queries;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import lombok.Getter;

import java.security.KeyPair;

public class InApp extends Application {
    public static InApp instance;
    public Account account;
    @Getter
    @VisibleForTesting
    public ApplicationComponent applicationComponent;

    Queries.Query q=null;
    QryResponses.QueryResponse res=null;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applicationComponent = DaggerApplicationComponent.builder().build();
        Logger.addLogAdapter(new AndroidLogAdapter());

    }
}
