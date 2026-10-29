package com.cine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Json {
    private Json() {
    }

    public static Object parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        Parser parser = new Parser(text);
        Object value = parser.parseValue();
        parser.skipWhitespace();
        if (!parser.atEnd()) {
            throw new ApiException(400, "El JSON enviado no es válido.");
        }
        return value;
    }

    public static Map<String, Object> parseObject(String text) {
        Object value = parse(text);
        if (value == null) {
            return new LinkedHashMap<>();
        }
        if (!(value instanceof Map<?, ?> map)) {
            throw new ApiException(400, "Se esperaba un objeto JSON.");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) map;
        return result;
    }

    public static String stringify(Object value) {
        StringBuilder sb = new StringBuilder();
        write(value, sb);
        return sb.toString();
    }

    private static void write(Object value, StringBuilder sb) {
        if (value == null) {
            sb.append("null");
            return;
        }
        if (value instanceof String text) {
            writeString(text, sb);
            return;
        }
        if (value instanceof Boolean || value instanceof Number) {
            sb.append(value);
            return;
        }
        if (value instanceof Map<?, ?> map) {
            sb.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    sb.append(',');
                }
                first = false;
                writeString(String.valueOf(entry.getKey()), sb);
                sb.append(':');
                write(entry.getValue(), sb);
            }
            sb.append('}');
            return;
        }
        if (value instanceof Iterable<?> iterable) {
            sb.append('[');
            boolean first = true;
            for (Object item : iterable) {
                if (!first) {
                    sb.append(',');
                }
                first = false;
                write(item, sb);
            }
            sb.append(']');
            return;
        }
        if (value instanceof Object[] array) {
            sb.append('[');
            for (int i = 0; i < array.length; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                write(array[i], sb);
            }
            sb.append(']');
            return;
        }
        writeString(String.valueOf(value), sb);
    }

    private static void writeString(String text, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }

    private static final class Parser {
        private final String source;
        private int position;

        private Parser(String source) {
            this.source = source;
        }

        private Object parseValue() {
            skipWhitespace();
            if (atEnd()) {
                throw error();
            }
            char c = source.charAt(position);
            return switch (c) {
                case '{' -> parseObject();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't' -> parseLiteral("true", Boolean.TRUE);
                case 'f' -> parseLiteral("false", Boolean.FALSE);
                case 'n' -> parseLiteral("null", null);
                default -> parseNumber();
            };
        }

        private Map<String, Object> parseObject() {
            expect('{');
            Map<String, Object> map = new LinkedHashMap<>();
            skipWhitespace();
            if (peek() == '}') {
                position++;
                return map;
            }
            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expect(':');
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                char c = peek();
                if (c == ',') {
                    position++;
                    continue;
                }
                if (c == '}') {
                    position++;
                    return map;
                }
                throw error();
            }
        }

        private List<Object> parseArray() {
            expect('[');
            List<Object> list = new ArrayList<>();
            skipWhitespace();
            if (peek() == ']') {
                position++;
                return list;
            }
            while (true) {
                list.add(parseValue());
                skipWhitespace();
                char c = peek();
                if (c == ',') {
                    position++;
                    continue;
                }
                if (c == ']') {
                    position++;
                    return list;
                }
                throw error();
            }
        }

        private String parseString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (!atEnd()) {
                char c = source.charAt(position++);
                if (c == '"') {
                    return sb.toString();
                }
                if (c == '\\') {
                    if (atEnd()) {
                        throw error();
                    }
                    char escape = source.charAt(position++);
                    switch (escape) {
                        case '"' -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        case '/' -> sb.append('/');
                        case 'b' -> sb.append('\b');
                        case 'f' -> sb.append('\f');
                        case 'n' -> sb.append('\n');
                        case 'r' -> sb.append('\r');
                        case 't' -> sb.append('\t');
                        case 'u' -> {
                            if (position + 4 > source.length()) {
                                throw error();
                            }
                            String hex = source.substring(position, position + 4);
                            try {
                                sb.append((char) Integer.parseInt(hex, 16));
                            } catch (NumberFormatException ex) {
                                throw error();
                            }
                            position += 4;
                        }
                        default -> throw error();
                    }
                } else {
                    sb.append(c);
                }
            }
            throw error();
        }

        private Object parseNumber() {
            int start = position;
            while (!atEnd()) {
                char c = source.charAt(position);
                if (c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E' || (c >= '0' && c <= '9')) {
                    position++;
                } else {
                    break;
                }
            }
            if (start == position) {
                throw error();
            }
            String token = source.substring(start, position);
            try {
                if (token.contains(".") || token.contains("e") || token.contains("E")) {
                    return Double.parseDouble(token);
                }
                return Long.parseLong(token);
            } catch (NumberFormatException ex) {
                throw error();
            }
        }

        private Object parseLiteral(String literal, Object value) {
            if (source.startsWith(literal, position)) {
                position += literal.length();
                return value;
            }
            throw error();
        }

        private void expect(char expected) {
            skipWhitespace();
            if (atEnd() || source.charAt(position) != expected) {
                throw error();
            }
            position++;
        }

        private char peek() {
            if (atEnd()) {
                throw error();
            }
            return source.charAt(position);
        }

        private void skipWhitespace() {
            while (!atEnd()) {
                char c = source.charAt(position);
                if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                    position++;
                } else {
                    break;
                }
            }
        }

        private boolean atEnd() {
            return position >= source.length();
        }

        private ApiException error() {
            return new ApiException(400, "El JSON enviado no es válido.");
        }
    }
}
