package com.puttel.app.iroha;

import com.google.protobuf.InvalidProtocolBufferException;
import com.puttel.app.iroha.detail.BuildableAndSignable;
import com.puttel.app.iroha.detail.Hashable;
import com.puttel.app.iroha.detail.ReducedHashable;
import iroha.protocol.TransactionOuterClass;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Date;

public class Transaction extends
        Hashable<TransactionOuterClass.Transaction.Payload.Builder>  // should be Payload.Builder
        implements BuildableAndSignable<TransactionOuterClass.Transaction>,
        ReducedHashable {
    private TransactionOuterClass.Transaction.Builder tx = TransactionOuterClass.Transaction
            .newBuilder();

    /* default */ TransactionOuterClass.Transaction.Payload.ReducedPayload.Builder reducedPayload = TransactionOuterClass.Transaction.Payload.ReducedPayload.newBuilder();

    /* default */ TransactionOuterClass.Transaction.Payload.BatchMeta.Builder batchMeta = TransactionOuterClass.Transaction.Payload.BatchMeta.newBuilder();

    /* default */ void updatePayload() {
        tx.setPayload(
                getProto()
                        .setReducedPayload(reducedPayload)
        );
    }

    /* default */ void updateBatch() {
        tx.setPayload(
                getProto()
                        .setBatch(batchMeta)
        );
    }

    /* default */ Transaction() {
        super(TransactionOuterClass.Transaction.Payload.newBuilder());
    }

    /* default */ Transaction(TransactionOuterClass.Transaction tx) {
        super(TransactionOuterClass.Transaction.Payload.newBuilder(tx.getPayload()));
        this.tx = TransactionOuterClass.Transaction.newBuilder(tx);
        this.reducedPayload = TransactionOuterClass.Transaction.Payload.ReducedPayload.newBuilder(tx.getPayload().getReducedPayload());
        this.batchMeta = TransactionOuterClass.Transaction.Payload.BatchMeta.newBuilder(tx.getPayload().getBatch());
    }

    @Override
    public BuildableAndSignable<TransactionOuterClass.Transaction> sign(KeyPair keyPair) {
        updatePayload();
        tx.addSignatures(Utils.sign(this, keyPair));
        return this;
    }

    @Override
    public TransactionOuterClass.Transaction build() {
        updatePayload();
        return tx.build();
    }

    @Override
    public byte[] getReducedHash() {
        return Utils.reducedHash(tx.getPayload().getReducedPayload());
    }

    @Override
    public String getReducedHashHex() {
        return Utils.toHex(getReducedHash());
    }

    public TransactionBuilder makeMutable() {
        tx.clearSignatures();
        return new TransactionBuilder(this);
    }

    public static Transaction parseFrom(TransactionOuterClass.Transaction input) {
        return new Transaction(input);
    }

    public static Transaction parseFrom(byte[] input) throws InvalidProtocolBufferException {
        TransactionOuterClass.Transaction proto = TransactionOuterClass.Transaction.parseFrom(input);
        return new Transaction(proto);
    }

    public static TransactionBuilder builder(String accountId, Long date) {
        return new TransactionBuilder(accountId, date);
    }

    public static TransactionBuilder builder(String accountId, Date date) {
        return new TransactionBuilder(accountId, date);
    }

    public static TransactionBuilder builder(String accountId, Instant time) {
        return new TransactionBuilder(accountId, time);
    }

    public static TransactionBuilder builder(String accountId) {
        return builder(accountId, System.currentTimeMillis());
    }
}
