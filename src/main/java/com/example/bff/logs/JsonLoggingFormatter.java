package com.example.bff.logs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonWriter;
import org.springframework.boot.logging.structured.StructuredLogFormatter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class JsonLoggingFormatter implements StructuredLogFormatter<ILoggingEvent> {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    int aPort;

    private JsonWriter<ILoggingEvent> writer = JsonWriter.<ILoggingEvent>of((members) -> {
        members.add("time", ILoggingEvent::getInstant);
        members.add("level", ILoggingEvent::getLevel);
        members.add("thread", ILoggingEvent::getThreadName);
        members.add("message", ILoggingEvent::getFormattedMessage);
        members.add("application").usingMembers((application) -> {
            application.add("name", appName );
        });
        members.add("node").usingMembers((node) -> {
            node.add("hostname", getHostName());
            node.add("ip", getHostAddress());
        });
        members.add("traceId", (x) -> (x.getMDCPropertyMap().get("traceId") != null) ? x.getMDCPropertyMap().get("traceId") : "null");
    members.add("spanId", (x) -> x.getMDCPropertyMap().get("spanId") != null ? x.getMDCPropertyMap().get("spanId") : "null");
    }).withNewLineAtEnd();


    @Override
    public String format(ILoggingEvent event) {
        return this.writer.writeToString(event);
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }

    private String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}
