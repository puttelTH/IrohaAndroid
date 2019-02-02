package com.puttel.app.iroha;


import android.os.Build;
import com.puttel.app.Setup;
import com.puttel.app.iroha.detail.BuildableAndSignable;
import iroha.protocol.Commands;
import iroha.protocol.Primitive;
import iroha.protocol.TransactionOuterClass;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;


import static com.puttel.app.iroha.Utils.nonNull;
import static com.puttel.app.iroha.detail.Const.accountIdDelimiter;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;

public class TransactionBuilder {
    private FieldValidator validator;
    private Transaction tx;

    private void init(String accountId, Long time) {
        tx = new Transaction();
        if (nonNull(accountId)) {
            setCreatorAccountId(accountId);
        }

        if (nonNull(time)) {
            setCreatedTime(time);
        }

        setQuorum(1 /* default value */);

        this.validator = new FieldValidator();
    }

    /**
     * Both fields are required, therefore we can not create builder without them. However, in genesis
     * block they can be null.
     */
    public TransactionBuilder(String accountId, Instant time) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            init(accountId, time.toEpochMilli());
        }
    }

    public TransactionBuilder(String accountId, Date time) {
        init(accountId, time.getTime());
    }

    public TransactionBuilder(String accountId, Long time) {
        init(accountId, time);
    }

    /* default */ TransactionBuilder(Transaction transaction) {
        tx = transaction;
    }

    public TransactionBuilder disableValidation() {
        this.validator = null;
        return this;
    }

    public TransactionBuilder setCreatorAccountId(String accountId) {
        if (nonNull(this.validator)) {
            this.validator.checkAccountId(accountId);
        }

        tx.reducedPayload.setCreatorAccountId(accountId);
        return this;
    }

    public TransactionBuilder setCreatedTime(Long time) {
        if (nonNull(this.validator)) {
            this.validator.checkTimestamp(time);
        }

        tx.reducedPayload.setCreatedTime(time);
        return this;
    }

    public TransactionBuilder setCreatedTime(Date time) {
        return setCreatedTime(time.getTime());
    }

    public TransactionBuilder setCreatedTime(Instant time) {
        TransactionBuilder tx=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tx= setCreatedTime(time.toEpochMilli());
        }
        return tx;
    }

    public TransactionBuilder setQuorum(int quorum) {
        if (nonNull(this.validator)) {
            this.validator.checkQuorum(quorum);
        }

        tx.reducedPayload.setQuorum(quorum);
        return this;
    }

    public TransactionBuilder createAccount(
            String accountName,
            String domainid,
            byte[] publicKey
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAccount(accountName);
            this.validator.checkDomain(domainid);
            this.validator.checkPublicKey(publicKey);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setCreateAccount(
                                Commands.CreateAccount.newBuilder()
                                        .setAccountName(accountName)
                                        .setDomainId(domainid)
                                        .setPublicKey(Utils.toHex(publicKey)).build()
                        ).build()
        );

        return this;
    }

    public TransactionBuilder createAccount(
            String accountName,
            String domainid,
            PublicKey publicKey
    ) {
        return createAccount(
                accountName,
                domainid,
                publicKey.getEncoded()
        );
    }

    public TransactionBuilder createAccount(
            String accountId,
            PublicKey publicKey
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAccountId(accountId);
        }

        String[] t = accountId.split(accountIdDelimiter);

        return createAccount(
                t[0],
                t[1],
                publicKey.getEncoded()
        );
    }

    public TransactionBuilder transferAsset(
            String sourceAccount,
            String destinationAccount,
            String assetId,
            String description,
            String amount
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAccountId(sourceAccount);
            this.validator.checkAccountId(destinationAccount);
            this.validator.checkAssetId(assetId);
            this.validator.checkDescription(description);
            this.validator.checkAmount(amount);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setTransferAsset(
                                Commands.TransferAsset.newBuilder()
                                        .setSrcAccountId(sourceAccount)
                                        .setDestAccountId(destinationAccount)
                                        .setAssetId(assetId)
                                        .setDescription(description)
                                        .setAmount(amount)
                                        .build()
                        ).build()
        );

        return this;
    }

    public TransactionBuilder transferAsset(
            String sourceAccount,
            String destinationAccount,
            String assetId,
            String description,
            BigDecimal amount
    ) {
        return transferAsset(
                sourceAccount,
                destinationAccount,
                assetId,
                description,
                amount.toPlainString()
        );
    }

    public TransactionBuilder setAccountDetail(
            String accountId,
            String key,
            String value
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAccountId(accountId);
            this.validator.checkAccountDetailsKey(key);
            this.validator.checkAccountDetailsValue(value);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setSetAccountDetail(
                                Commands.SetAccountDetail.newBuilder()
                                        .setAccountId(accountId)
                                        .setKey(key)
                                        .setValue(value)
                                        .build()
                        )
                        .build()
        );

        return this;
    }

    public TransactionBuilder addPeer(
            String address,
            byte[] peerKey
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkPeerAddress(address);
            this.validator.checkPublicKey(peerKey);
        }

        String hex = Setup.bytesToHex(peerKey);

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setAddPeer(
                                Commands.AddPeer.newBuilder()
                                        .setPeer(
                                                Primitive.Peer.newBuilder()
                                                        .setAddress(address)
                                                        .setPeerKey(Utils.toHex(peerKey))
                                        ).build()
                        ).build()
        );

        return this;
    }

    public TransactionBuilder addPeer(
            String address,
            PublicKey peerKey
    ) {
        return addPeer(address, peerKey.getEncoded());
    }

    public TransactionBuilder grantPermission(
            String accountId,
            Primitive.GrantablePermission permission
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAccountId(accountId);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setGrantPermission(
                                Commands.GrantPermission.newBuilder()
                                        .setAccountId(accountId)
                                        .setPermission(permission)
                                        .build()
                        ).build()
        );

        return this;
    }

    public TransactionBuilder grantPermissions(
            String accountId,
            Iterable<Primitive.GrantablePermission> permissions
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            permissions.forEach(p -> this.grantPermission(accountId, p));
        }
        return this;
    }

    public TransactionBuilder createRole(
            String roleName,
            Iterable<? extends Primitive.RolePermission> permissions
    ) {

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder().setCreateRole(
                        Commands.CreateRole.newBuilder()
                                .setRoleName(roleName)
                                .addAllPermissions(permissions)
                                .build()
                ).build()
        );

        return this;
    }

    public TransactionBuilder createDomain(
            String domainId,
            String defaultRole
    ) {

        if (nonNull(this.validator)) {
            this.validator.checkDomain(domainId);
            this.validator.checkRoleName(defaultRole);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setCreateDomain(
                                Commands.CreateDomain.newBuilder()
                                        .setDomainId(domainId)
                                        .setDefaultRole(defaultRole)
                                        .build()
                        )
                        .build()
        );

        return this;
    }

    public TransactionBuilder appendRole(
            String accountId,
            String roleName
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAccountId(accountId);
            this.validator.checkRoleName(roleName);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setAppendRole(
                                Commands.AppendRole.newBuilder()
                                        .setAccountId(accountId)
                                        .setRoleName(roleName)
                                        .build()
                        )
                        .build()
        );

        return this;
    }

    public TransactionBuilder createAsset(
            String assetName,
            String domain,
            Integer precision
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAssetName(assetName);
            this.validator.checkDomain(domain);
            this.validator.checkPrecision(precision);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setCreateAsset(
                                Commands.CreateAsset.newBuilder()
                                        .setAssetName(assetName)
                                        .setDomainId(domain)
                                        .setPrecision(precision)
                                        .build()
                        )
                        .build()
        );

        return this;
    }

    public TransactionBuilder addAssetQuantity(
            String assetId,
            String amount
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAssetId(assetId);
            this.validator.checkAmount(amount);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setAddAssetQuantity(
                                Commands.AddAssetQuantity.newBuilder()
                                        .setAssetId(assetId)
                                        .setAmount(amount)
                                        .build()
                        )
                        .build()
        );

        return this;
    }

    public TransactionBuilder addAssetQuantity(String assetId, BigDecimal amount) {
        return this.addAssetQuantity(assetId, amount.toPlainString());
    }

    public TransactionBuilder addSignatory(String accountId, PublicKey publicKey) {
        return this.addSignatory(accountId, publicKey.getEncoded());
    }

    public TransactionBuilder addSignatory(
            String accountId,
            byte[] publicKey
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAccountId(accountId);
            this.validator.checkPublicKey(publicKey);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setAddSignatory(
                                Commands.AddSignatory.newBuilder()
                                        .setAccountId(accountId)
                                        .setPublicKey(Utils.toHex(publicKey))
                                        .build()
                        )
                        .build()
        );

        return this;
    }

    public TransactionBuilder detachRole(
            String accountId,
            String roleName
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAccountId(accountId);
            this.validator.checkRoleName(roleName);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setDetachRole(
                                Commands.DetachRole.newBuilder()
                                        .setAccountId(accountId)
                                        .setRoleName(roleName)
                                        .build()
                        )
                        .build()
        );

        return this;
    }

    public TransactionBuilder removeSignatory(
            String accountId,
            byte[] publicKey
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAccountId(accountId);
            this.validator.checkPublicKey(publicKey);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setRemoveSignatory(
                                Commands.RemoveSignatory.newBuilder()
                                        .setAccountId(accountId)
                                        .setPublicKey(Utils.toHex(publicKey))
                                        .build()
                        )
                        .build()
        );

        return this;
    }

    public TransactionBuilder removeSignatory(String accountId, PublicKey publicKey) {
        return this.removeSignatory(accountId, publicKey.getEncoded());
    }

    public TransactionBuilder revokePermission(
            String accountId,
            Primitive.GrantablePermission permission
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAccountId(accountId);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setRevokePermission(
                                Commands.RevokePermission.newBuilder()
                                        .setAccountId(accountId)
                                        .setPermission(permission)
                                        .build()
                        )
                        .build()
        );

        return this;
    }

    public TransactionBuilder subtractAssetQuantity(
            String assetId,
            String amount
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAssetId(assetId);
            this.validator.checkAmount(amount);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setSubtractAssetQuantity(
                                Commands.SubtractAssetQuantity.newBuilder()
                                        .setAssetId(assetId)
                                        .setAmount(amount)
                                        .build()
                        )
                        .build()
        );

        return this;
    }

    public TransactionBuilder setAccountQuorum(
            String accountId,
            int quorum
    ) {
        if (nonNull(this.validator)) {
            this.validator.checkAccountId(accountId);
            this.validator.checkQuorum(quorum);
        }

        tx.reducedPayload.addCommands(
                Commands.Command.newBuilder()
                        .setSetAccountQuorum(
                                Commands.SetAccountQuorum.newBuilder()
                                        .setAccountId(accountId)
                                        .setQuorum(quorum)
                                        .build()
                        )
                        .build()
        );

        return this;

    }

    public TransactionBuilder subtractAssetQuantity(String assetId, BigDecimal amount) {
        return this.subtractAssetQuantity(assetId, amount.toPlainString());
    }

    public TransactionBuilder setBatchMeta(TransactionOuterClass.Transaction.Payload.BatchMeta.BatchType batchType, Iterable<String> hashes) {
        tx.batchMeta.setType(batchType);
        tx.batchMeta.addAllReducedHashes(hashes);
        tx.updateBatch();

        return this;
    }

    public BuildableAndSignable<TransactionOuterClass.Transaction> sign(KeyPair keyPair)
            throws Ed25519Sha3.CryptoException {
        return tx.sign(keyPair);
    }

    public Transaction build() {
        tx.updatePayload();
        return tx;
    }
}
