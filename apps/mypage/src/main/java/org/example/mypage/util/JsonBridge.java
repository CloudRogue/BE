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


    public static com.fasterxml.jackson.databind.JsonNode fromText(String valueText) {
        if (valueText == null) return null;
        try {
            return JACKSON.readTree(valueText);
        } catch (Exception e) {

            return JACKSON.getNodeFactory().textNode(valueText);
        }
    }

    public static Object toPlainValue(com.fasterxml.jackson.databind.JsonNode node) {
        if (node == null || node.isNull()) return null;
        try {

            return JACKSON.readValue(node.toString(), Object.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("JsonNode -> plain Object convert failed", e);
        }
    }

}
