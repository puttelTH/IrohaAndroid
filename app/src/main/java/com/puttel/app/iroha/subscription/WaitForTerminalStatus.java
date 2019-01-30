package com.puttel.app.iroha.subscription;

import android.os.Build;
import com.puttel.app.iroha.IrohaAPI;
import com.puttel.app.iroha.routers.TxStatusRouter;
import com.puttel.app.iroha.timeout.OneSecondTimeout;
import com.puttel.app.iroha.timeout.TimeoutStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import iroha.protocol.Endpoint;
import lombok.val;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class WaitForTerminalStatus implements SubscriptionStrategy {

    private AtomicBoolean terminated = new AtomicBoolean(false);

    private TimeoutStrategy timeoutStrategy = new OneSecondTimeout();

    private Consumer<Throwable> onError = e -> {
        /* default: ignore onError from iroha status stream */
    };
    private Action onComplete = () -> {
        /* default: ignore onComplete from iroha status stream */
    };

    /**
     * Custom timeout strategy.
     */
    public WaitForTerminalStatus(TimeoutStrategy timeoutStrategy) {
        this.timeoutStrategy = timeoutStrategy;
    }

    /**
     * Executed when Iroha sends onError. You can add your listener to log errors or handle them
     * somehow.
     */
    public WaitForTerminalStatus doOnError(Consumer<Throwable> consumer) {
        this.onError = consumer;
        return this;
    }

    /**
     * Executed when Iroha sends onComplete. You can add your listener to log/handle stream
     * completions.
     */
    public WaitForTerminalStatus doOnComplete(Action action) {
        this.onComplete = action;
        return this;
    }


    /**
     * We received terminal status, push onNext result back to user, then complete observable
     */
    private Consumer<Endpoint.ToriiResponse> onTerminal(ObservableEmitter<Endpoint.ToriiResponse> o) {
        return (Endpoint.ToriiResponse t) -> {
            o.onNext(t); // send terminal status back to client
            o.onComplete(); // complete client observable
            terminate(); // terminate re-subscription loop
        };
    }

    private void terminate() {
        this.terminated.set(true);
    }

    private static List<Endpoint.TxStatus> terminal = Arrays.asList(
            Endpoint.TxStatus.STATELESS_VALIDATION_FAILED,
            Endpoint.TxStatus.STATEFUL_VALIDATION_FAILED,
            Endpoint.TxStatus.COMMITTED,
            Endpoint.TxStatus.MST_EXPIRED,
            Endpoint.TxStatus.NOT_RECEIVED,
            Endpoint.TxStatus.REJECTED,
            Endpoint.TxStatus.UNRECOGNIZED
    );

    @Override
    public Observable<Endpoint.ToriiResponse> subscribe(IrohaAPI api, byte[] txhash) {
        TxStatusRouter router = new TxStatusRouter();
        return Observable.create(o -> {
            Consumer<Endpoint.ToriiResponse> callback = this.onTerminal(o);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                terminal.forEach(status -> router.handle(status, callback));
            }

            // as soon as we get non-terminal status, pass it to the caller
            router.handleDefault(o::onNext);

            // re-subscription loop
            do {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    api.txStatus(txhash)
                            .subscribeOn(Schedulers.single())
                            .blockingSubscribe(router::process, this.onError::accept, this.onComplete);
                }
            } while (!terminated.get() && timeoutStrategy.nextTimeout());

        });
    }
}