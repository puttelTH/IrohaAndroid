package com.puttel.app.iroha.subscription;

import com.puttel.app.iroha.IrohaAPI;
import io.reactivex.Observable;
import iroha.protocol.Endpoint;

/**
 * Wait until Iroha calls onComplete once. Does not resubscribe.
 */
public class WaitUntilCompleted implements SubscriptionStrategy {

    @Override
    public Observable<Endpoint.ToriiResponse> subscribe(IrohaAPI api, byte[] txhash) {
        return api.txStatus(txhash);
    }
}
