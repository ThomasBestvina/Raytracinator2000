import org.junit.jupiter.api.Test;
import org.thomas.parser.JsonParser;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {
    @Test
    void parsesInteger() {
        assertEquals(42.0, (Double) JsonParser.parse("42"));
    }

    @Test
    void parsesNegativeNumber() {
        assertEquals(-3.14, (Double) JsonParser.parse("-3.14"), 1e-10);
    }

    @Test
    void parsesScientificNotation() {
        assertEquals(1.5e10, (Double) JsonParser.parse("1.5e10"), 1.0);
    }

    @Test
    void parsesTrue() {
        assertEquals(Boolean.TRUE, JsonParser.parse("true"));
    }

    @Test
    void parsesFalse() {
        assertEquals(Boolean.FALSE, JsonParser.parse("false"));
    }

    @Test
    void parsesNull() {
        assertNull(JsonParser.parse("null"));
    }

    @Test
    void parsesString() {
        assertEquals("hello", JsonParser.parse("\"hello\""));
    }

    @Test
    void parsesStringWithEscapedQuote() {
        assertEquals("say \"hi\"", JsonParser.parse("\"say \\\"hi\\\"\""));
    }

    @Test
    void parsesStringWithEscapedBackslash() {
        assertEquals("a\\b", JsonParser.parse("\"a\\\\b\""));
    }

    @Test
    void parsesStringWithNewlineEscape() {
        assertEquals("a\nb", JsonParser.parse("\"a\\nb\""));
    }

    @Test
    void parsesStringWithUnicodeEscape() {
        assertEquals("\u0041", JsonParser.parse("\"\\u0041\""));
    }

    @Test
    void parsesEmptyArray() {
        List<Object> list = asList(JsonParser.parse("[]"));
        assertTrue(list.isEmpty());
    }

    @Test
    void parsesNumberArray() {
        List<Object> list = asList(JsonParser.parse("[1, 2, 3]"));
        assertEquals(3, list.size());
        assertEquals(1.0, (Double) list.get(0));
        assertEquals(2.0, (Double) list.get(1));
        assertEquals(3.0, (Double) list.get(2));
    }

    @Test
    void parsesMixedArray() {
        List<Object> list = asList(JsonParser.parse("[1, \"two\", true, null]"));
        assertEquals(4, list.size());
        assertEquals(1.0, (Double) list.get(0));
        assertEquals("two", list.get(1));
        assertEquals(Boolean.TRUE, list.get(2));
        assertNull(list.get(3));
    }

    @Test
    void parsesNestedArray() {
        List<Object> outer = asList(JsonParser.parse("[[1, 2], [3, 4]]"));
        assertEquals(2, outer.size());
        List<Object> inner = asList(outer.get(0));
        assertEquals(1.0, (Double) inner.get(0));
        assertEquals(2.0, (Double) inner.get(1));
    }

    @Test
    void parsesEmptyObject() {
        Map<String, Object> map = asMap(JsonParser.parse("{}"));
        assertTrue(map.isEmpty());
    }

    @Test
    void parsesSingleFieldObject() {
        Map<String, Object> map = asMap(JsonParser.parse("{\"x\": 1.0}"));
        assertEquals(1.0, (Double) map.get("x"));
    }

    @Test
    void parsesMultiFieldObject() {
        Map<String, Object> map = asMap(JsonParser.parse("{\"a\": 1, \"b\": \"hello\", \"c\": true}"));
        assertEquals(1.0, (Double) map.get("a"));
        assertEquals("hello", map.get("b"));
        assertEquals(Boolean.TRUE, map.get("c"));
    }

    @Test
    void parsesNestedObject() {
        Map<String, Object> outer = asMap(JsonParser.parse("{\"inner\": {\"x\": 42}}"));
        Map<String, Object> inner = asMap(outer.get("inner"));
        assertEquals(42.0, (Double) inner.get("x"));
    }

    @Test
    void parsesObjectWithArray() {
        Map<String, Object> map = asMap(JsonParser.parse("{\"coords\": [1, 2, 3]}"));
        List<Object> coords = asList(map.get("coords"));
        assertEquals(3, coords.size());
        assertEquals(2.0, (Double) coords.get(1));
    }

    @Test
    void parsesArrayOfObjects() {
        List<Object> list = asList(JsonParser.parse("[{\"x\": 1}, {\"x\": 2}]"));
        assertEquals(1.0, (Double) asMap(list.get(0)).get("x"));
        assertEquals(2.0, (Double) asMap(list.get(1)).get("x"));
    }

    @Test
    void handlesLeadingAndTrailingWhitespace() {
        assertEquals(1.0, (Double) JsonParser.parse("  1  "));
    }

    @Test
    void handlesWhitespaceInsideObject() {
        Map<String, Object> map = asMap(JsonParser.parse("{ \"a\" : 1 , \"b\" : 2 }"));
        assertEquals(1.0, (Double) map.get("a"));
        assertEquals(2.0, (Double) map.get("b"));
    }

    @Test
    void handlesWhitespaceInsideArray() {
        List<Object> list = asList(JsonParser.parse("[ 1 , 2 , 3 ]"));
        assertEquals(3, list.size());
    }

    @Test
    void throwsOnTrailingContent() {
        assertThrows(RuntimeException.class, () -> JsonParser.parse("1 2"));
    }

    @Test
    void throwsOnUnterminatedString() {
        assertThrows(RuntimeException.class, () -> JsonParser.parse("\"hello"));
    }

    @Test
    void throwsOnUnterminatedArray() {
        assertThrows(RuntimeException.class, () -> JsonParser.parse("[1, 2"));
    }

    @Test
    void throwsOnUnterminatedObject() {
        assertThrows(RuntimeException.class, () -> JsonParser.parse("{\"a\": 1"));
    }

    @Test
    void throwsOnMissingColon() {
        assertThrows(RuntimeException.class, () -> JsonParser.parse("{\"a\" 1}"));
    }

    @Test
    void throwsOnEmptyInput() {
        assertThrows(RuntimeException.class, () -> JsonParser.parse(""));
    }

    @Test
    void throwsOnUnknownToken() {
        assertThrows(RuntimeException.class, () -> JsonParser.parse("xyz"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object o) {
        return (Map<String, Object>) o;
    }

    @SuppressWarnings("unchecked")
    private List<Object> asList(Object o) {
        return (List<Object>) o;
    }

}