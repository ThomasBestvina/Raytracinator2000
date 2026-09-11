package org.thomas.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {
    public static Object parse(String json)
    {
        JsonParser p = new JsonParser(json);
        Object result = p.parseValue();
        p.skipWhitespace();
        if(p.pos != p.src.length())
        {
            throw new RuntimeException("Unexpected Content At Position");
        }
        return result;
    }

    private final String src;
    private int pos = 0;

    private JsonParser(String src) {
        this.src = src;
    }

    private Object parseValue()
    {
        skipWhitespace();
        if (pos >= src.length()) throw new RuntimeException("Unexpected end of input");
        char c = src.charAt(pos);
        if (c == '{') return parseObject();
        if (c == '[') return parseArray();
        if (c == '"') return parseString();
        if (c == 't' || c == 'f') return parseBoolean();
        if (c == 'n') return parseNull();
        if (c == '-' || Character.isDigit(c)) return parseNumber();
        throw new RuntimeException("Unexpected character '" + c + "' at position " + pos);
    }

    private Map<String, Object> parseObject() {
        expect('{');
        Map<String, Object> map = new LinkedHashMap<>();
        skipWhitespace();
        if (peek() == '}') { pos++; return map; }
        while (true) {
            skipWhitespace();
            String key = parseString();
            skipWhitespace();
            expect(':');
            Object value = parseValue();
            map.put(key, value);
            skipWhitespace();
            char next = src.charAt(pos);
            if (next == '}') { pos++; break; }
            if (next == ',') { pos++; continue; }
            throw new RuntimeException("Expected ',' or '}' at position " + pos);
        }
        return map;
    }

    private List<Object> parseArray() {
        expect('[');
        List<Object> list = new ArrayList<>();
        skipWhitespace();
        if (peek() == ']') { pos++; return list; }
        while (true) {
            list.add(parseValue());
            skipWhitespace();
            char next = src.charAt(pos);
            if (next == ']') { pos++; break; }
            if (next == ',') { pos++; continue; }
            throw new RuntimeException("Expected ',' or ']' at position " + pos);
        }
        return list;
    }


    public String parseString()
    {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (pos < src.length()) {
            char c = src.charAt(pos++);
            if (c == '"') return sb.toString();
            if (c == '\\') {
                char esc = src.charAt(pos++);
                switch (esc) {
                    case '"':  sb.append('"');  break;
                    case '\\': sb.append('\\'); break;
                    case '/':  sb.append('/');  break;
                    case 'n':  sb.append('\n'); break;
                    case 'r':  sb.append('\r'); break;
                    case 't':  sb.append('\t'); break;
                    case 'b':  sb.append('\b'); break;
                    case 'f':  sb.append('\f'); break;
                    case 'u': {
                        String hex = src.substring(pos, pos + 4);
                        sb.append((char) Integer.parseInt(hex, 16));
                        pos += 4;
                        break;
                    }
                    default: throw new RuntimeException("Unknown escape \\" + esc);
                }
            } else {
                sb.append(c);
            }
        }
        throw new RuntimeException("Unterminated string");
    }

    private Double parseNumber()
    {
        int start = pos;
        if(peek() == '-') pos++;
        while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
        if (pos < src.length() && src.charAt(pos) == '.') {
            pos++;
            while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
        }
        if (pos < src.length() && (src.charAt(pos) == 'e' || src.charAt(pos) == 'E')) {
            pos++;
            if (pos < src.length() && (src.charAt(pos) == '+' || src.charAt(pos) == '-')) pos++;
            while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
        }
        return Double.parseDouble(src.substring(start, pos));

    }


    private Boolean parseBoolean()
    {
        if(src.startsWith("true", pos)) {pos += 4; return true;}
        else if(src.startsWith("false", pos)) {pos += 5; return false;}
        throw new RuntimeException("Expected boolean at position " + pos);
    }


    private Object parseNull()
    {
        if(src.startsWith("null", pos)) { pos += 4; return null; }
        throw new RuntimeException("Expected null at position " + pos);
    }

    private void skipWhitespace()
    {
        while(pos < src.length() && Character.isWhitespace(src.charAt(pos))) pos++;
    }

    private char peek()
    {
        skipWhitespace();
        return src.charAt(pos);
    }

    private void expect(char expected)
    {
        skipWhitespace();;
        if(pos >= src.length() || src.charAt(pos) != expected)
        {
            throw new RuntimeException("Expected " + expected + " at position " + src.charAt(pos));
        }
        pos++;
    }

}