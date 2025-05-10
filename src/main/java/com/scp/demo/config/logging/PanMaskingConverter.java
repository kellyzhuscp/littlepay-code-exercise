package com.scp.demo.config.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class PanMaskingConverter extends ClassicConverter {
    private static final int PAN_MASK_LENGTH = 4;
    private static final String MASKED_PAN_PATTERN = "****%s";
    private static final String PAN_PREFIX = "PAN=";

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        if (message == null || !message.contains(PAN_PREFIX)) {
            return message;
        }

        // Find PAN values in the message and mask them
        StringBuilder maskedMessage = new StringBuilder(message);
        int startIndex = 0;
        while ((startIndex = maskedMessage.indexOf(PAN_PREFIX, startIndex)) != -1) {
            int valueStart = startIndex + PAN_PREFIX.length();
            int valueEnd = findValueEnd(maskedMessage, valueStart);

            String pan = maskedMessage.substring(valueStart, valueEnd).trim();
            String maskedPan = maskPan(pan);
            maskedMessage.replace(valueStart, valueEnd, maskedPan);
            startIndex = valueStart + maskedPan.length();
        }

        return maskedMessage.toString();
    }

    private int findValueEnd(StringBuilder message, int valueStart) {
        // Look for common delimiters
        int commaIndex = message.indexOf(",", valueStart);
        int braceIndex = message.indexOf("}", valueStart);
        int spaceIndex = message.indexOf(" ", valueStart);
        int newlineIndex = message.indexOf("\n", valueStart);

        // Find the earliest delimiter
        int minIndex = Integer.MAX_VALUE;
        if (commaIndex != -1) minIndex = Math.min(minIndex, commaIndex);
        if (braceIndex != -1) minIndex = Math.min(minIndex, braceIndex);
        if (spaceIndex != -1) minIndex = Math.min(minIndex, spaceIndex);
        if (newlineIndex != -1) minIndex = Math.min(minIndex, newlineIndex);

        return minIndex == Integer.MAX_VALUE ? message.length() : minIndex;
    }

    private String maskPan(String pan) {
        if (pan == null || pan.isEmpty()) {
            return "****";
        }
        
        // Remove any quotes or spaces
        pan = pan.trim().replaceAll("[\"']", "");
        
        if (pan.length() <= PAN_MASK_LENGTH) {
            return "****";
        }
        
        return String.format(MASKED_PAN_PATTERN, pan.substring(pan.length() - PAN_MASK_LENGTH));
    }
} 