package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.SocketResponseSetting;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class MocoSocketServer extends BaseServerRunner<SocketResponseSetting> {
    private final ActualSocketServer serverSetting;

    public MocoSocketServer(final ActualSocketServer serverSetting) {
        this.serverSetting = serverSetting;
    }

    @Override
    protected BaseActualServer<SocketResponseSetting> serverSetting() {
        return this.serverSetting;
    }

    @Override
    protected ChannelInitializer<SocketChannel> channelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("aggregator", new MocoAggregator());
                pipeline.addLast("handler", new MocoSocketHandler(serverSetting));
            }
        };
    }
}
