package com.puttel.app.iroha.detail;

public interface ReducedHashable {

    byte[] getReducedHash();

    String getReducedHashHex();
}
