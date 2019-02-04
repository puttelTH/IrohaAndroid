package com.puttel.app.interactor;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.protobuf.InvalidProtocolBufferException;

import java.math.BigInteger;

import javax.inject.Inject;
import javax.inject.Named;

import com.puttel.app.Constants;
import com.puttel.app.PreferencesUtil;
import com.puttel.app.injection.ApplicationModule;
import io.grpc.ManagedChannel;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import iroha.protocol.QryResponses;
import iroha.protocol.Queries;
import iroha.protocol.QueryService_v1Grpc;

import static com.puttel.app.Constants.DOMAIN_ID;
import static com.puttel.app.Constants.QUERY_COUNTER;

public class GetAccountDetailsInteractor extends SingleInteractor<String, Void>{
    private final PreferencesUtil preferenceUtils;


    @Inject
    GetAccountDetailsInteractor(@Named(ApplicationModule.JOB) Scheduler jobScheduler,
                                @Named(ApplicationModule.UI) Scheduler uiScheduler,
                                PreferencesUtil preferenceUtils) {
        super(jobScheduler, uiScheduler);
        this.preferenceUtils = preferenceUtils;
    }

    @Override
    protected Single<String> build(Void v) {
        return Single.create(emitter -> {
            long currentTime = System.currentTimeMillis();
            Keypair userKeys = preferenceUtils.retrieveKeys();
            String username = preferenceUtils.retrieveUsername();

            UnsignedQuery accountDetails = modelQueryBuilder.creatorAccountId(username + "@" + DOMAIN_ID)
                    .queryCounter(BigInteger.valueOf(QUERY_COUNTER))
                    .createdTime(BigInteger.valueOf(currentTime))
                    .getAccountDetail(username + "@" + DOMAIN_ID)
                    .build();

            protoQueryHelper = new ModelProtoQuery(accountDetails);
            ByteVector queryBlob = protoQueryHelper.signAndAddSignature(userKeys).finish().blob();
            byte bquery[] = toByteArray(queryBlob);

            Queries.Query protoQuery = null;
            try {
                protoQuery = Queries.Query.parseFrom(bquery);
            } catch (InvalidProtocolBufferException e) {
                emitter.onError(e);
            }

            QueryService_v1Grpc.QueryService_v1BlockingStub queryStub = QueryService_v1Grpc.newBlockingStub(channel);
            QryResponses.QueryResponse queryResponse = queryStub.find(protoQuery);

            JsonElement jsonElement = new Gson().fromJson(queryResponse.getAccountDetailResponse().getDetail(), JsonObject.class).get(username + "@" + DOMAIN_ID);
            ;
            String detail = jsonElement != null ? jsonElement.getAsJsonObject().get(Constants.ACCOUNT_DETAILS).getAsString() : "";

            emitter.onSuccess(detail);
        });
    }
}
