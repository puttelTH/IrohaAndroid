package com.puttel.app.iroha;

import static com.puttel.app.iroha.MainApi.bytesToHex;
import static iroha.protocol.BlockOuterClass.Block.BlockVersionCase.BLOCK_V1;
import static jp.co.soramitsu.crypto.ed25519.Ed25519Sha3.privateKeyFromBytes;
import static jp.co.soramitsu.crypto.ed25519.Ed25519Sha3.publicKeyFromBytes;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.puttel.app.iroha.detail.Hashable;
import iroha.protocol.*;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;

import lombok.val;
import org.apache.commons.codec.DecoderException;
import org.spongycastle.jcajce.provider.digest.SHA3;

public class Utils {

    /**
     * @deprecated use {@code Utils.parseHexKeypair}
     */
    @Deprecated
    public static KeyPair keyPair(String hexPublicKey, String hexPrivateKey) {
        return parseHexKeypair(hexPublicKey, hexPrivateKey);
    }

    public static KeyPair parseHexKeypair(String hexPublicKey, String hexPrivateKey) {
        return new KeyPair(
                parseHexPublicKey(hexPublicKey),
                parseHexPrivateKey(hexPrivateKey)
        );
    }

    public static PublicKey parseHexPublicKey(String hexPublicKey) {
        PublicKey pub=null;
        try {
            pub= publicKeyFromBytes(org.apache.commons.codec.binary.Hex.decodeHex(hexPublicKey.toCharArray()));
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return pub;
    }

    public static PrivateKey parseHexPrivateKey(String hexPrivateKey) {
        PrivateKey pk = null;
        try {
            pk= privateKeyFromBytes(org.apache.commons.codec.binary.Hex.decodeHex(hexPrivateKey.toCharArray()));
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return pk;
    }

    public static byte[] reducedHash(TransactionOuterClass.Transaction tx) {
        return reducedHash(tx.getPayload().getReducedPayload());
    }

    public static byte[] reducedHash(
            TransactionOuterClass.Transaction.Payload.ReducedPayload reducedPayload) {
        SHA3.Digest256 sha3 = new SHA3.Digest256();
        byte[] data = reducedPayload.toByteArray();
        return sha3.digest(data);
    }

    public static byte[] hash(TransactionOuterClass.Transaction tx) {
        SHA3.Digest256 sha3 = new SHA3.Digest256();
        byte[] data = tx.getPayload().toByteArray();
        return sha3.digest(data);
    }

    public static byte[] hash(BlockOuterClass.Block_v1 b) {
        SHA3.Digest256 sha3 = new SHA3.Digest256();
        byte[] data = b.getPayload().toByteArray();
        return sha3.digest(data);
    }

    public static byte[] hash(BlockOuterClass.Block b) {
        byte[] a = new byte[0];
        switch (b.getBlockVersionCase()) {
            case BLOCK_V1:
                a= hash(b.getBlockV1());
            case BLOCKVERSION_NOT_SET:
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Block has undefined version: %s", b.getBlockVersionCase()));
        }
        return a;
    }

    public static byte[] hash(Queries.Query q) {
        SHA3.Digest256 sha3 = new SHA3.Digest256();
        byte[] data = q.getPayload().toByteArray();
        return sha3.digest(data);
    }

    /* default */
    static <T extends Hashable> Primitive.Signature sign(T t, KeyPair kp) {
        byte[] rawSignature = new Ed25519Sha3().rawSign(t.hash(), kp);

        return Primitive.Signature.newBuilder()
                .setSignature(
                        Utils.toHex(rawSignature)
                )
                .setPublicKey(
                        Utils.toHex(kp.getPublic().getEncoded())
                )
                .build();
    }

    // this method is here only because some old versions of Android do not have Objects.nonNull
    public static boolean nonNull(Object obj) {
        return obj != null;
    }

    public static Endpoint.TxStatusRequest createTxStatusRequest(byte[] hash) {
        return Endpoint.TxStatusRequest.newBuilder()
                .setTxHash(Utils.toHex(hash))
                .build();
    }

    public static Endpoint.TxList createTxList(Iterable<TransactionOuterClass.Transaction> list) {
        return Endpoint.TxList.newBuilder()
                .addAllTransactions(list)
                .build();
    }
/*
    public static Iterable<TransactionOuterClass.Transaction> createTxOrderedBatch(
            Iterable<TransactionOuterClass.Transaction> list, KeyPair keyPair) {
        return createBatch(list, TransactionOuterClass.Transaction.Payload.BatchMeta.BatchType.ORDERED, keyPair);
    }

    public static Iterable<TransactionOuterClass.Transaction> createTxAtomicBatch(
            Iterable<TransactionOuterClass.Transaction> list, KeyPair keyPair) {
        return createBatch(list, TransactionOuterClass.Transaction.Payload.BatchMeta.BatchType.ATOMIC, keyPair);

    }

    private static Iterable<String> getBatchHashesHex(
            Iterable<TransactionOuterClass.Transaction> list) {
        return StreamSupport.stream(list.spliterator(), false)
                .map(tx -> toHex(reducedHash(tx)))
                .collect(Collectors.toList());
    }

    private static Iterable<TransactionOuterClass.Transaction> createBatch(
            Iterable<TransactionOuterClass.Transaction> list, TransactionOuterClass.Transaction.Payload.BatchMeta.BatchType batchType, KeyPair keyPair) {
        final Iterable<String> batchHashes = getBatchHashesHex(list);
        return StreamSupport.stream(list.spliterator(), false)
                .map(tx -> TransactionOuterClass.Transaction
                        .parseFrom(tx)
                        .makeMutable()
                        .setBatchMeta(batchType, batchHashes)
                        .sign(keyPair)
                        .build()
                )
                .collect(Collectors.toList());
    }
    */

    public static String toHex(byte[] b) {
        return bytesToHex(b);
    }
}
