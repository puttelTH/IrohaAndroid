package com.puttel.app.interactor;

import com.puttel.app.PreferencesUtil;
import com.puttel.app.injection.ApplicationModule;
import io.reactivex.Completable;
import io.reactivex.Scheduler;

import javax.inject.Inject;
import javax.inject.Named;

public class CreateAccountInteractor extends CompletableInteractor<String>{
    private final PreferencesUtil preferenceUtils;
    @Inject
    CreateAccountInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                            @Named(ApplicationModule.UI) Scheduler uiScheduler,
                            PreferencesUtil preferencesUtil){
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferencesUtil;
    }

    @Override
    protected Completable build(String username) {
        return Completable.create(emitter -> {

        });
    }
}
