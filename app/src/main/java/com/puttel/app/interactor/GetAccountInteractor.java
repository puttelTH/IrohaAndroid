package com.puttel.app.interactor;

import com.puttel.app.injection.ApplicationModule;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import iroha.protocol.QryResponses;

import javax.inject.Inject;
import javax.inject.Named;

public class GetAccountInteractor extends SingleInteractor<QryResponses.Account, String> {

    @Inject
    GetAccountInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                         @Named(ApplicationModule.UI) Scheduler uiScheduler){
        super(jobScheduler, uiScheduler);
    }

    @Override
    protected Single<QryResponses.Account> build(String accountId) {
        return Single.create(emitter -> {

        });

    }
}
