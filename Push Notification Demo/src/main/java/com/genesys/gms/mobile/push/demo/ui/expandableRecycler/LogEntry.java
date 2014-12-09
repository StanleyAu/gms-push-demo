package com.genesys.gms.mobile.push.demo.ui.expandableRecycler;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by Stan on 12/2/2014.
 */
public class LogEntry {
    private final String title;
    private final String content;
    private final DateTime timestamp;
    private final MessageType messageType;

    public LogEntry(String title, String content, DateTime timestamp, MessageType messageType) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.messageType = messageType;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "title=" + title +
                ",content=" + content +
                ",timestamp=" + timestamp.withZone(DateTimeZone.UTC).toString("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'") +
                ",messageType=" + messageType +
                "]";
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public enum MessageType {
        REQUEST,
        RESPONSE,
        ERROR,
        UNKNOWN
    }
}
