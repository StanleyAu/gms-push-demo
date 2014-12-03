package com.genesys.gms.mobile.push.demo.data;

import android.text.TextUtils;
import com.genesys.gms.mobile.push.demo.ui.expandableRecycler.LogEntry;
import com.squareup.otto.Bus;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Stan on 12/3/2014.
 */
@Singleton
public class RetrofitLogCollector {
    private static final String REQUEST_PREFIX = "--->";
    private static final String RESPONSE_PREFIX = "<---";
    private static final String ERROR_PREFIX = "----";

    private final Bus bus;
    private DateTime timestamp;
    private StringBuilder builder;
    private LogEntry.MessageType messageType;
    private String title;

    @Inject
    public RetrofitLogCollector(Bus bus) {
        // We only care to post to the bus, don't register
        this.bus = bus;
        this.timestamp = null;
        this.builder = new StringBuilder();
        this.messageType = null;
        this.title = "";
    }

    public synchronized void append(String message) {
        if(!title.isEmpty()) {
            builder.append(message);
            if(message.startsWith(REQUEST_PREFIX) ||
                message.startsWith(RESPONSE_PREFIX) ||
                message.startsWith(ERROR_PREFIX)) {
                shipIt();
                return;
            }
            builder.append("\r\n");
        } else {
            // First message, extract title
            String[] tokens = message.split(" ");
            if(message.startsWith(REQUEST_PREFIX)) {
                if(tokens.length >= 3) {
                    title = tokens[1] + " " + tokens[2];
                } else {
                    title = "Request";
                }
                messageType = LogEntry.MessageType.REQUEST;
            } else if(message.startsWith(RESPONSE_PREFIX)) {
                if(tokens.length >= 3) {
                    title = tokens[1] + " " + tokens[2];
                } else {
                    title = "Response";
                }
                messageType = LogEntry.MessageType.RESPONSE;
            } else if(message.startsWith(ERROR_PREFIX)) {
                if(tokens.length >= 2) {
                    title = tokens[1];
                } else {
                    title = "Error";
                }
                messageType = LogEntry.MessageType.ERROR;
            } else {
                if(tokens.length >= 2) {
                    title = tokens[1];
                } else {
                    title = "Unknown";
                }
                messageType = LogEntry.MessageType.UNKNOWN;
            }
            timestamp = DateTime.now();
            builder.append(message + "\r\n");
        }

    }

    private void shipIt() {
        LogEntry entry = new LogEntry(title, builder.toString(), timestamp, messageType);
        title = "";
        builder.setLength(0);
        timestamp = null;
        messageType = null;
        bus.post(entry);
    }
}
