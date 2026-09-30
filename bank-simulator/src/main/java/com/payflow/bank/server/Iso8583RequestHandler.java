package com.payflow.bank.server;

import com.payflow.bank.config.SimulatorConfig;
import com.payflow.bank.logic.ResponseGenerator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Netty channel handler that processes incoming ISO 8583 requests
 * and generates simulated bank responses.
 *
 * Simplified ISO 8583 format (for simulation):
 * Field layout: MTI(4) + PAN(19) + Amount(12) + RRN(12) = fixed-length message
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class Iso8583RequestHandler extends SimpleChannelInboundHandler<String> {

    private final ResponseGenerator responseGenerator;
    private final SimulatorConfig config;

    public Iso8583RequestHandler(ResponseGenerator responseGenerator, SimulatorConfig config) {
        this.responseGenerator = responseGenerator;
        this.config = config;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        log.debug("Received ISO 8583 request: length={}", message.length());

        // Simulate network latency
        simulateLatency();

        // Parse ISO 8583 fields
        // Wire format from routing-service: MTI(4) + Bitmap(8 binary bytes as chars) + Fields
        // The bitmap bytes may contain non-printable chars — skip them
        String mti = extractField(message, 0, 4);

        // Skip 8-byte binary bitmap (positions 4-11), then parse fields
        // After bitmap: PAN is LLVAR (2-digit length prefix + value)
        // For simplified parsing, extract PAN and Amount from after bitmap
        int fieldStart = 12; // 4 (MTI) + 8 (bitmap)
        String fieldsData = message.length() > fieldStart ? message.substring(fieldStart) : "";

        // Extract PAN (field 2 — LLVAR: 2-digit length + value)
        String pan = "0000000000000000";
        String amount = "000000000000";
        String rrn = "000000";

        if (fieldsData.length() >= 2) {
            try {
                int panLen = Integer.parseInt(fieldsData.substring(0, 2));
                pan = fieldsData.substring(2, 2 + Math.min(panLen, fieldsData.length() - 2));
                int offset = 2 + panLen;
                // Processing code (field 3 — 6 digits fixed)
                offset += 6;
                // Amount (field 4 — 12 digits fixed)
                if (fieldsData.length() >= offset + 12) {
                    amount = fieldsData.substring(offset, offset + 12);
                }
                offset += 12;
                // Trace/RRN (field 11 — 6 digits fixed)
                if (fieldsData.length() >= offset + 6) {
                    rrn = fieldsData.substring(offset, offset + 6);
                }
            } catch (Exception e) {
                log.warn("Could not fully parse ISO 8583 fields, using defaults: {}", e.getMessage());
            }
        }

        log.info("Processing: MTI={}, PAN={}****, Amount={}, RRN={}",
                mti, pan.substring(0, Math.min(6, pan.length())), amount, rrn);

        // Generate response
        String responseCode = responseGenerator.generateResponse(pan, amount);
        String responseMti = "0110"; // Response MTI

        // Build response message (simplified: MTI + responseCode + authCode + RRN)
        // Routing-service reads field 39 (response code) and field 38 (auth code) from parsed message
        // For simplicity, send back a flat string that the routing-service's decoder can handle
        String authCode = responseCode.equals("00") ? String.format("%06d", System.nanoTime() % 1000000) : "000000";
        String response = responseMti + pan + amount + rrn + responseCode + authCode;

        ctx.writeAndFlush(response);
        log.info("Sent response: MTI={}, ResponseCode={}, AuthCode={}, RRN={}", responseMti, responseCode, authCode, rrn);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Channel error: {}", cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("New connection from: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Connection closed: {}", ctx.channel().remoteAddress());
    }

    private void simulateLatency() throws InterruptedException {
        int latency = ThreadLocalRandom.current().nextInt(
                config.getMinLatencyMs(), config.getMaxLatencyMs() + 1);
        Thread.sleep(latency);
    }

    private String extractField(String message, int start, int end) {
        if (message.length() >= end) {
            return message.substring(start, end).trim();
        }
        return message.substring(start).trim();
    }
}