package com.puttel.app.iroha;


import com.puttel.app.iroha.detail.StreamObserverToEmitter;
import com.puttel.app.iroha.subscription.SubscriptionStrategy;
import com.puttel.app.iroha.subscription.WaitUntilCompleted;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.Observable;
import iroha.protocol.*;

import java.io.Closeable;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;

import static com.puttel.app.iroha.Utils.createTxList;
import static com.puttel.app.iroha.Utils.createTxStatusRequest;

public class IrohaAPI implements Closeable {
    private static final WaitUntilCompleted defaultStrategy = new WaitUntilCompleted();

    private ManagedChannel channel;
    private URI uri;
    private CommandService_v1Grpc.CommandService_v1BlockingStub cmdStub;
    private CommandService_v1Grpc.CommandService_v1Stub cmdStreamingStub;
    private QueryService_v1Grpc.QueryService_v1BlockingStub queryStub;
    private QueryService_v1Grpc.QueryService_v1Stub queryStreamingStub;

    public IrohaAPI(URI uri) {
        this(uri.getHost(), uri.getPort());
    }
    public IrohaAPI(String host, int port) {
        this(
                ManagedChannelBuilder
                        .forAddress(host, port)
                        .usePlaintext(true)
                        .build()
        );
        try {
            this.uri = new URI("grpc", null, host, port, null, null, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    public IrohaAPI(ManagedChannel channel) {
        this.channel = channel;
        this.setChannelForBlockingCmdStub(channel)
                .setChannelForBlockingQueryStub(channel)
                .setChannelForStreamingCmdStub(channel)
                .setChannelForStreamingQueryStub(channel);
    }
    public IrohaAPI setChannelForBlockingCmdStub(Channel channel) {
        cmdStub = CommandService_v1Grpc.newBlockingStub(channel);
        return this;
    }

    public IrohaAPI setChannelForStreamingCmdStub(Channel channel) {
        cmdStreamingStub = CommandService_v1Grpc.newStub(channel);
        return this;
    }

    public IrohaAPI setChannelForBlockingQueryStub(Channel channel) {
        queryStub = QueryService_v1Grpc.newBlockingStub(channel);
        return this;
    }

    public IrohaAPI setChannelForStreamingQueryStub(Channel channel) {
        queryStreamingStub = QueryService_v1Grpc.newStub(channel);
        return this;
    }
    /**
     * Send transaction synchronously, then subscribe for transaction status stream.
     *
     * It uses {@link WaitUntilCompleted} subscription strategy by default.
     *
     * @param tx protobuf transaction.
     * @return observable. Use {@code Observable.blockingSubscribe(...)} or {@code
     * Observable.subscribe} for synchronous or asynchronous subscription.
     */
    public Observable<Endpoint.ToriiResponse> transaction(TransactionOuterClass.Transaction tx) {
        return transaction(tx, defaultStrategy);
    }

    public Observable<Endpoint.ToriiResponse> transaction(TransactionOuterClass.Transaction tx,
                                                          SubscriptionStrategy strategy) {
        transactionSync(tx);
        byte[] hash = Utils.hash(tx);
        return strategy.subscribe(this, hash);
    }

    public QueryAPI getQueryAPI(String accountId, KeyPair keyPair) {
        return new QueryAPI(this, accountId, keyPair);
    }

    /**
     * Send transaction synchronously.
     *
     * Blocking call.
     *
     * @param tx protobuf transaction.
     */
    public void transactionSync(TransactionOuterClass.Transaction tx) {
        cmdStub.torii(tx);
    }

    /**
     * Send query synchronously.
     *
     * @param query protobuf query.
     */
    public QryResponses.QueryResponse query(Queries.Query query) {
        return queryStub.find(query);
    }

    /**
     * Subscribe for blocks in iroha. You need to have special permission to do that.
     *
     * @param query protobuf query.
     */
    public Observable<QryResponses.BlockQueryResponse> blocksQuery(Queries.BlocksQuery query) {
        return Observable.create(
                o -> queryStreamingStub.fetchCommits(query, new StreamObserverToEmitter<>(o))
        );
    }

    /**
     * Synchronously send list of transactions.
     *
     * Blocking call.
     */
    public void transactionListSync(Iterable<TransactionOuterClass.Transaction> txList) {
        cmdStub.listTorii(createTxList(txList));
    }

    /**
     * Asynchronously ask for transaction status.
     *
     * @param txHash hash of transaction for status query.
     * @return {@link Observable}
     */
    public Observable<Endpoint.ToriiResponse> txStatus(byte[] txHash) {
        Endpoint.TxStatusRequest req = createTxStatusRequest(txHash);
        return Observable.create(
                o -> cmdStreamingStub.statusStream(req, new StreamObserverToEmitter<>(o))
        );
    }

    /**
     * Synchronously ask to transaction status.
     *
     * @param txHash hash of transaction for status query
     */
    public Endpoint.ToriiResponse txStatusSync(byte[] txHash) {
        return cmdStub.status(createTxStatusRequest(txHash));
    }
    /**
     * Close GRPC connection.
     */
    public void terminate() {
        if (!channel.isTerminated()) {
            channel.shutdownNow();
        }
    }

    @Override
    public void finalize() {
        terminate();
    }

    @Override
    public void close() {
        terminate();
    }
}
