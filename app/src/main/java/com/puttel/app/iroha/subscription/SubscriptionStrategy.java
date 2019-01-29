package com.puttel.app.iroha.subscription;

import com.puttel.app.iroha.IrohaAPI;
import io.reactivex.Observable;
import iroha.protocol.Endpoint;

public interface SubscriptionStrategy {

    Observable<Endpoint.ToriiResponse> subscribe(IrohaAPI api, byte[] txhash);
}
