package com.payflow.routing.config;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyConfig {

    private static final Logger log = LoggerFactory.getLogger(NettyConfig.class);

    @Value("${netty.worker-threads:4}")
    private int workerThreads;

    @Value("${netty.connection-pool.max-connections:10}")
    private int maxConnections;

    @Value("${netty.connection-pool.max-idle-time-seconds:60}")
    private int maxIdleTimeSeconds;

    @Bean(destroyMethod = "shutdownGracefully")
    public EventLoopGroup workerGroup() {
        log.info("Creating Netty EventLoopGroup with {} worker threads", workerThreads);
        return new NioEventLoopGroup(workerThreads);
    }

    public int getMaxConnections(){
        return maxConnections;
    }

    public int getMaxIdleTimeSeconds(){
        return maxIdleTimeSeconds;
    }
}
