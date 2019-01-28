package com.puttel.app.iroha;

import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import iroha.protocol.QryResponses;
import iroha.protocol.Queries;
import iroha.protocol.QueryService_v1Grpc;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyPair;


public class IrohaAPI {
    public static final Ed25519Sha3 crypto = new Ed25519Sha3();
    KeyPair keysAdmin= Ed25519Sha3.keyPairFromBytes("f101537e319568c765b2cc89698325604991dca57b9716b58016b253506cab70".getBytes(Charset.forName("UTF-8")),"313a07e6384776ed95447710d15e59148473ccfc052a681317a72a69f2a49910".getBytes(Charset.forName("UTF-8")));
    String gw="192.168.1.55";
    //String gw="172.31.58.205";
    String creator="admin@test";
    String domain="utth";
    long startQueryCounter = 1;

    public IrohaAPI(){
        KeyPair aKP= crypto.generateKeypair();
        System.out.println(aKP.getPrivate().toString()+"---"+aKP.getPublic().toString()+"----"+aKP.getPrivate().getEncoded().toString());
    }

}
