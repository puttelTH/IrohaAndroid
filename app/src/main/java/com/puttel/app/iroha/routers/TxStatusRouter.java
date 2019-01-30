package com.puttel.app.iroha.routers;

import iroha.protocol.Endpoint;

public class TxStatusRouter extends Router<Endpoint.ToriiResponse, Endpoint.TxStatus> {

    public TxStatusRouter() {
        super(Endpoint.ToriiResponse::getTxStatus);
    }
}
