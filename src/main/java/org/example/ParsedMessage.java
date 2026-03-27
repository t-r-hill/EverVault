package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

record ParsedMessage(String type, String relayUuid, double elapsed, long timestamp, LocalDateTime dateTime) {

    ParsedMessage(Message message) {
        this(null, null, message);
    }

    ParsedMessage(String tstamp, DateTimeFormatter formatter, Message message) {
        LocalDateTime parsedTimeStamp;
        if (tstamp == null || formatter == null) {
            parsedTimeStamp = null;
        } else {
            parsedTimeStamp = LocalDateTime.parse(tstamp, formatter);
        }
        this(
                message.type(),
                message.relayUuid(),
                message.elapsed() == null ? 0.0 : Double.parseDouble(message.elapsed()),
                message.timestamp() == null ? 0 : Long.parseLong(message.timestamp()),
                parsedTimeStamp
        );
    }

}
