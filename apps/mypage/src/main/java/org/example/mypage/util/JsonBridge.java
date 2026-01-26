package org.example.mypage.util;



public final class JsonBridge {
    private JsonBridge() {}

    private static final com.fasterxml.jackson.databind.ObjectMapper JACKSON =
            new com.fasterxml.jackson.databind.ObjectMapper();

    public static com.fasterxml.jackson.databind.JsonNode toFasterxml(tools.jackson.databind.JsonNode toolsNode) {
        if (toolsNode == null || toolsNode.isNull()) return null;
        try {
            return JACKSON.readTree(toolsNode.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("tools JsonNode -> fasterxml JsonNode convert failed", e);
        }
    }
}
