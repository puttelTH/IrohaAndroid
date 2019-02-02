package com.puttel.app.interactor;

import com.puttel.app.PreferencesUtil;
import com.puttel.app.Setup;
import com.puttel.app.injection.ApplicationModule;
import com.puttel.app.iroha.Transaction;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import iroha.protocol.CommandService_v1Grpc;
import iroha.protocol.TransactionOuterClass;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import lombok.val;

import javax.inject.Inject;
import javax.inject.Named;
import java.security.KeyPair;
import java.util.concurrent.TimeUnit;

import static com.puttel.app.Constants.*;

public class CreateAccountInteractor extends CompletableInteractor<String>{
    private final PreferencesUtil preferenceUtils;
    @Inject
    CreateAccountInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                            @Named(ApplicationModule.UI) Scheduler uiScheduler,
                            PreferencesUtil preferencesUtil){
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferencesUtil;
    }

    @Override
    protected Completable build(String username) {
        return Completable.create(emitter -> {
            ManagedChannel channel= ManagedChannelBuilder
                    .forAddress("192.168.1.55", 50051)
                    .usePlaintext(true)
                    .build();

            KeyPair userKeys = new Ed25519Sha3().generateKeypair();
            KeyPair keysAdmin= Ed25519Sha3.keyPairFromBytes(Setup.decodeHexString(PRIV_KEY),Setup.decodeHexString(PUB_KEY)) ;

            TransactionOuterClass.Transaction tx = Transaction.builder(CREATOR)
                    .createAccount(username, DOMAIN_ID, userKeys.getPublic())
                    .sign(keysAdmin)
                    .build();


            // Send transaction to iroha
            CommandService_v1Grpc.CommandService_v1BlockingStub stub = CommandService_v1Grpc.newBlockingStub(channel)
                    .withDeadlineAfter(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            stub.torii(tx);

            // Check if it was successful
            /*
            if (!isTransactionSuccessful(stub, createAccount)) {
                emitter.onError(new RuntimeException("Transaction failed"));
            } else {
                preferenceUtils.saveKeys(userKeys);
                preferenceUtils.saveUsername(username);
                emitter.onComplete();
            }
            */

        });
    }
}
