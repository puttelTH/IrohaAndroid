package com.puttel.app.iroha;

import java.security.KeyPair;

public class QueryAPI {

    private IrohaAPI api;
    private KeyPair keyPair;
    private String accountId;

    public QueryAPI(IrohaAPI api, String accountId, KeyPair keyPair) {
        this.api = api;
        this.accountId = accountId;
        this.keyPair = keyPair;
    }

}
