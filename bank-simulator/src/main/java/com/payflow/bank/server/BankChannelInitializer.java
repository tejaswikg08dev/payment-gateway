package com.payflow.bank.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Channel pipeline configuration for the bank simulator TCP server.
 * Sets up frame decoding, string encoding, and the ISO 8583 request handler.
 */
@Component
public class BankChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Iso8583RequestHandler requestHandler;

    public BankChannelInitializer(Iso8583RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Idle state handler: close connections idle for 60 seconds
        pipeline.addLast("idleStateHandler",
                new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));

        // Frame decoder: 4-byte length header (matches routing-service's LengthFieldPrepender(4))
        pipeline.addLast("frameDecoder",
                new LengthFieldBasedFrameDecoder(8192, 0, 4, 0, 4));

        // Frame encoder: prepend 4-byte length header
        pipeline.addLast("frameEncoder",
                new LengthFieldPrepender(4));

        // String codec
        pipeline.addLast("stringDecoder", new StringDecoder(StandardCharsets.UTF_8));
        pipeline.addLast("stringEncoder", new StringEncoder(StandardCharsets.UTF_8));

        // Business logic handler
        pipeline.addLast("requestHandler", requestHandler);
    }
}