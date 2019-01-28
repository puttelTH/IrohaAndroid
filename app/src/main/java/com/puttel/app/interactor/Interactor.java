package com.puttel.app.interactor;


import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;

public class Interactor {
    final CompositeDisposable subscriptions = new CompositeDisposable();
    final Scheduler jobScheduler;
    final Scheduler uiScheduler;

    Interactor(Scheduler jobScheduler, Scheduler uiScheduler) {
        this.jobScheduler = jobScheduler;
        this.uiScheduler = uiScheduler;
    }

    public void unsubscribe() {
        subscriptions.clear();
    }
}
