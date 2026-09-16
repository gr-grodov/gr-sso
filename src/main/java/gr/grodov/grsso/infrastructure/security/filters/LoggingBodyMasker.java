package gr.grodov.grsso.infrastructure.security.filters;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Set;

public class LoggingBodyMasker {
    private static final Set<String> SECRET_FIELDS = Set.of(
        "password", "client_secret", "refresh_token", "access_token",
        "code_verifier", "verifyCode", "authorization"
    );

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    public static String mask(String body) {
        if (body == null || body.isBlank()) {
            return body;
        }

        if (looksLikeJson(body)) {
            return maskJson(body);
        }
        return maskFormEncoded(body);
    }

    private static boolean looksLikeJson(String body) {
        String trimmed = body.trim();
        return trimmed.startsWith("{") || trimmed.startsWith("[");
    }

    private static String maskJson(String body) {
        try {
            JsonNode root = JSON_MAPPER.readTree(body);
            maskNode(root);
            return JSON_MAPPER.writeValueAsString(root);
        } catch (Exception ex) {
            return "[unparseable json]";
        }
    }

    private static void maskNode(JsonNode node) {
        if (node instanceof ObjectNode obj) {
            obj.fieldNames().forEachRemaining(fieldName -> {
                if (SECRET_FIELDS.stream().anyMatch(fieldName::equalsIgnoreCase)) {
                    obj.put(fieldName, "***");
                } else {
                    maskNode(obj.get(fieldName));
                }
            });
        } else if (node instanceof ArrayNode array) {
            array.forEach(LoggingBodyMasker::maskNode);
        }
    }

    private static String maskFormEncoded(String body) {
        StringBuilder result = new StringBuilder();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && SECRET_FIELDS.stream().anyMatch(kv[0]::equalsIgnoreCase)) {
                result.append(kv[0]).append("=***");
            } else {
                result.append(pair);
            }
            result.append('&');
        }
        return !result.isEmpty() ? result.substring(0, result.length() - 1) : "";
    }
}
