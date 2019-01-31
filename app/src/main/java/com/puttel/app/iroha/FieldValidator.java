package com.puttel.app.iroha;

import lombok.NonNull;
import lombok.val;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;

import static com.puttel.app.iroha.ValidationException.Type.*;
import static com.puttel.app.iroha.detail.Const.*;

/**
 * Stateless validator for transaction and query fields.
 */
public class FieldValidator {

    public void checkAmount(@NonNull String amount) {
        BigDecimal am;

        try {
            am = new BigDecimal(amount);
        } catch (Exception e) {
            throw new ValidationException(AMOUNT, e.toString());
        }

        if (am.signum() < 0) {
            throw new ValidationException(AMOUNT, "BigInteger must be positive");
        }

        if (am.unscaledValue().bitLength() > 256) {
            throw new ValidationException(AMOUNT, "BigInteger does not fit into uint256");
        }
    }

    public void checkAccount(@NonNull String account) {
        Matcher m = accountPattern.matcher(account);
        if (!m.matches()) {
            throw new ValidationException(
                    ACCOUNT_NAME,
                    "Invalid account. Expected '%s', got '%s'",
                    accountPattern.pattern(),
                    account
            );
        }
    }

    public void checkDomain(@NonNull String domain) {
        try {
            URI uri = new URI(null, null, domain, 0, null, null, null);
        } catch (URISyntaxException e) {
            throw new ValidationException(DOMAIN, "Domain name is invalid: '%s'", domain);
        }
    }

    public void checkAccountId(@NonNull String accountId) {
        String[] t = accountId.split(accountIdDelimiter);
        if (t.length != 2) {
            throw new ValidationException(ACCOUNT_ID, "Valid format is account@domain, got '%s'",
                    accountId);
        }

        try {
            this.checkAccount(t[0]);
            this.checkDomain(t[1]);
        } catch (ValidationException e) {
            throw new ValidationException(
                    ACCOUNT_ID,
                    "Valid format is account@domain, got '%s'. Details: '%s'.",
                    accountId,
                    e.getMessage()
            );
        }

    }

    public void checkQuorum(int quorum) {
        if (quorum < 1) {
            throw new ValidationException(QUORUM, "Quorum must be positive");
        }

        if (quorum > 128) {
            throw new ValidationException(QUORUM, "Quorum should be 0 < quorum <= 128; given %d", quorum);
        }
    }


    public void checkAssetId(@NonNull String assetId) {
        String[] t = assetId.split(assetIdDelimiter);
        if (t.length != 2) {
            throw new ValidationException(
                    ASSET_ID,
                    "Valid format is asset#domain, got '%s'",
                    assetId);
        }

        try {
            this.checkAssetName(t[0]);
            this.checkDomain(t[1]);
        } catch (ValidationException e) {
            throw new ValidationException(
                    ASSET_ID,
                    "Valid format is asset#domain, got '%s'. Details: '%s'.",
                    assetId,
                    e.getMessage()
            );
        }
    }


    public void checkAccountDetailsKey(@NonNull String key) {
        Matcher m = accountDetailsKeyPattern.matcher(key);
        if (!m.matches()) {
            throw new ValidationException(
                    DETAILS_KEY,
                    "Invalid key. Expected '%s', got '%s'",
                    accountDetailsKeyPattern.pattern(),
                    key
            );
        }
    }


    public void checkAccountDetailsValue(@NonNull String value) {
        if (!(value.length() <= accountDetailsMaxLength)) {
            throw new ValidationException(DETAILS_VALUE,
                    "Invalid details value, exceeded maximum length in '%d'. Got '%d'",
                    accountDetailsMaxLength, value.length());
        }
    }

    public void checkPeerAddress(@NonNull String address) {
        String m = String.format("Expected ip:port, got '%s'", address);
        String[] t = address.split(hostPortDelimiter);
        if (t.length != 2) {
            throw new ValidationException(PEER_ADDRESS, m);
        }

        try {
            String host = t[0];
            int port = Integer.parseInt(t[1]);
            URI uri = new URI(null, null, host, port, null, null, null);
        } catch (Exception e) {
            throw new ValidationException(PEER_ADDRESS, "%s. %s.", m, e.toString());
        }
    }

    public void checkPublicKey(@NonNull byte[] peerKey) {
        if (peerKey.length != 32) {
            throw new ValidationException(PUBKEY, "Public key must be 32 bytes length, got '%d'",
                    peerKey.length);
        }
    }

    public void checkRoleName(@NonNull String roleName) {
        Matcher m = roleNamePattern.matcher(roleName);
        if (!m.find()) {
            throw new ValidationException(ROLE_NAME,
                    "Role name is invalid, should match: '%s'", roleNamePattern.pattern());
        }
    }

    public void checkAssetName(@NonNull String assetName) {
        Matcher m = assetNamePattern.matcher(assetName);
        if (!m.matches()) {
            throw new ValidationException(ASSET_NAME,
                    "Invalid asset name. Expected '%s', got '%s'",
                    assetNamePattern.pattern(),
                    assetName);
        }
    }

    public void checkPrecision(@NonNull Integer precision) {
        if (precision < 0 || precision > 255) {
            throw new ValidationException(PRECISION,
                    String.format("Invalid precision: '%d'. Should be 0<=precision<=255", precision));
        }
    }

    public void checkTimestamp(@NonNull Long time) {
        if (time < 0) {
            throw new ValidationException(TIMESTAMP, "Time must be positive");
        }
    }

    public void checkDescription(String description) {
        if (description == null) {
            return;
        }

        int len = description.length();
        if (len > 64) {
            throw new ValidationException(DESCRIPTION, "Max length is 64, given string length is '%d'",
                    len);
        }
    }

    public void checkPageSize(@NonNull Integer size) {
        if (size < 1) {
            throw new ValidationException(PAGE_SIZE, "Page size must be bigger than 0, got '%d'", size);
        }
    }

    public void checkHashLength(@NonNull String hash) {
        int length = hash.length();
        if (length != 64) {
            throw new ValidationException(HASH_LENGTH, "Hash length must be equal to 64, got '%d'",
                    length);
        }
    }
}