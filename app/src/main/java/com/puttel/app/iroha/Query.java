package com.puttel.app.iroha;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Date;


import android.os.Build;
import com.puttel.app.iroha.detail.Hashable;
import iroha.protocol.Queries;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3.CryptoException;


public class Query
        extends Hashable<Queries.Query.Payload.Builder> {

    private Queries.QueryPayloadMeta.Builder meta;
    private Queries.Query.Builder q = Queries.Query.newBuilder();

    public Query(Queries.QueryPayloadMeta.Builder meta) {
        super(Queries.Query.Payload.newBuilder());

        this.meta = meta;
    }

    private void updatePayload() {
        getProto().setMeta(meta);
        q.setPayload(getProto());
    }

    public Queries.Query buildSigned(KeyPair keyPair) throws CryptoException {
        updatePayload();
        q.setSignature(Utils.sign(this, keyPair));
        return q.build();
    }

    public Queries.Query buildUnsigned() {
        updatePayload();
        return q.build();
    }

    public static QueryBuilder builder(String accountId, Long time, long counter) {
        return new QueryBuilder(accountId, time, counter);
    }

    public static QueryBuilder builder(String accountId, Date time, long counter) {
        return new QueryBuilder(accountId, time, counter);
    }

    public static QueryBuilder builder(String accountId, Instant time, long counter) {
        return new QueryBuilder(accountId, time, counter);
    }

    public static QueryBuilder builder(String accountId, long counter) {
        QueryBuilder qb=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            qb= new QueryBuilder(accountId, Instant.now(), counter);
        }
        return qb;
    }
}