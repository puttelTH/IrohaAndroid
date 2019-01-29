package com.puttel.app.iroha;


import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import iroha.protocol.CommandService_v1Grpc;
import iroha.protocol.QueryService_v1Grpc;

import java.io.Closeable;
import java.net.URI;
import java.net.URISyntaxException;

public class IrohaAPI implements Closeable {

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
