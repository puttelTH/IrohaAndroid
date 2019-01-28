package com.puttel.app.injection;

import javax.inject.Named;
import javax.inject.Singleton;

import com.puttel.app.InApp;
import com.puttel.app.R;
import dagger.Module;
import dagger.Provides;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Module
public class ApplicationModule {
    public static final String JOB = "JOB";
    public static final String UI = "UI";

    @Provides
    @Singleton
    @Named(JOB)
    public Scheduler provideJobScheduler() {
        return Schedulers.computation();
    }

    @Provides
    @Singleton
    @Named(UI)
    public Scheduler provideUIScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @Singleton
    public ManagedChannel provideManagedChannel() {
        return ManagedChannelBuilder.forAddress(InApp.instance.getApplicationContext().getString(R.string.iroha_url),
                InApp.instance.getApplicationContext().getResources().getInteger(R.integer.iroha_port)).usePlaintext(true).build();
    }
}
