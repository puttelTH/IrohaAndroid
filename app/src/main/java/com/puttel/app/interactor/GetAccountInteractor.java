package com.puttel.app.interactor;

import com.puttel.app.Setup;
import com.puttel.app.injection.ApplicationModule;
import com.puttel.app.iroha.IrohaAPI;
import com.puttel.app.iroha.Query;
import com.puttel.app.iroha.QueryBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import iroha.protocol.CommandService_v1Grpc;
import iroha.protocol.QryResponses;
import iroha.protocol.Queries;
import iroha.protocol.QueryService_v1Grpc;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigInteger;
import java.security.KeyPair;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static com.puttel.app.Constants.CONNECTION_TIMEOUT_SECONDS;
import static com.puttel.app.Constants.CREATOR;
import static com.puttel.app.Constants.DOMAIN_ID;
import static com.puttel.app.Constants.PRIV_KEY;
import static com.puttel.app.Constants.PUB_KEY;
import static com.puttel.app.Constants.QUERY_COUNTER;

public class GetAccountInteractor extends SingleInteractor<QryResponses.Account, String> {

    @Inject
    GetAccountInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                         @Named(ApplicationModule.UI) Scheduler uiScheduler){
        super(jobScheduler, uiScheduler);
    }

    @Override
    protected Single<QryResponses.Account> build(String accountId) {
        return Single.create(emitter -> {
            long currentTime = System.currentTimeMillis();
            KeyPair keysAdmin= Ed25519Sha3.keyPairFromBytes(Setup.decodeHexString(PRIV_KEY),Setup.decodeHexString(PUB_KEY)) ;
            ManagedChannel channel= ManagedChannelBuilder
                    .forAddress("192.168.1.55", 50051)
                    .usePlaintext(true)
                    .build();
            System.out.println("Api...");
            Queries.Query q = new QueryBuilder(CREATOR, currentTime, QUERY_COUNTER)
                    .getAccount(accountId + "@" + DOMAIN_ID)
                    .buildSigned(keysAdmin);
            QueryService_v1Grpc.QueryService_v1BlockingStub queryStub = QueryService_v1Grpc.newBlockingStub(channel)
                    .withDeadlineAfter(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            QryResponses.QueryResponse res =queryStub.find(q);

            QryResponses.Account account = res.getAccountResponse().getAccount();
            System.out.println("Account Id = " + account.getAccountId());
            System.out.println("Domain = " + account.getDomainId());
            System.out.println("done!");
            emitter.onSuccess(res.getAccountResponse().getAccount());
        });

    }


}
