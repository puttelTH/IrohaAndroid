package com.puttel.app.iroha.detail;

import io.grpc.stub.StreamObserver;
import io.reactivex.Emitter;
import io.reactivex.ObservableEmitter;

/**
 * Helper class to convert {@link StreamObserver} to {@link Emitter}
 */

public class StreamObserverToEmitter<T> implements StreamObserver<T> {

    private Emitter<T> emitter;

    public StreamObserverToEmitter(ObservableEmitter<T> o) {
    }

    @Override
    public void onNext(T value) {
        emitter.onNext(value);
    }

    @Override
    public void onError(Throwable t) {
        emitter.onError(t);
    }

    @Override
    public void onCompleted() {
        emitter.onComplete();
    }
}
