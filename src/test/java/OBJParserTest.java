import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thomas.math.Vector;
import org.thomas.parser.OBJParser;
import org.thomas.parser.ParsedObject;
import org.thomas.shape.Group;
import org.thomas.shape.SmoothTriangle;
import org.thomas.shape.Triangle;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OBJParserTest {
    @Test void ignoringUnrecognizedLines()
    {
        String gibberish =
                "The quick brown fox jumps over the lazy dog\n" +
                        "The quickish greenish fox jumps over the awake dog\n" +
                        "Gibberish is hard to right\n" +
                        "Okay? Do you get that?\n" +
                        "Glad we're on the same page.\n" +
                        "v anbdsh";

        OBJParser.Parse(gibberish);
        Assertions.assertEquals(6, OBJParser.linesIgnored);
    }

    @Test void vertexRecords()
    {
        String file =   "v -1 1 0\n" +
                        "v -1.0000 0.5000 0.0000\n" +
                        "v 1 0 0\n" +
                        "v 1 1 0";
        ParsedObject result = OBJParser.Parse(file);
        assertEquals(Vector.point(-1, 1, 0), result.vertices.get(0));
        assertEquals(Vector.point(-1, 0.5, 0), result.vertices.get(1));
        assertEquals(Vector.point(1, 0, 0), result.vertices.get(2));
        assertEquals(Vector.point(1, 1, 0), result.vertices.get(3));
    }

    @Test void parsingTriangleFaces()
    {
        String file =   "v -1 1 0\n" +
                        "v -1 0 0\n" +
                        "v 1 0 0\n" +
                        "v 1 1 0\n" +
                        "f 1 2 3\n\n" +
                        "f 1 3 4";
        ParsedObject result = OBJParser.Parse(file);
        Triangle t1 = (Triangle)result.defaultGroup.getChildren().get(0);
        Triangle t2 = (Triangle)result.defaultGroup.getChildren().get(1);

        assertEquals(t1.p1, result.vertices.get(0));
        assertEquals(t1.p2, result.vertices.get(1));
        assertEquals(t1.p3, result.vertices.get(2));
        assertEquals(t2.p1, result.vertices.get(0));
        assertEquals(t2.p2, result.vertices.get(2));
        assertEquals(t2.p3, result.vertices.get(3));
    }

    @Test void triangulatingPolygons()
    {
        String file =   "v -1 1 0\n" +
                        "v -1 0 0\n" +
                        "v 1 0 0\n" +
                        "v 1 1 0\n" +
                        "v 0 2 0\n" +
                        "f 1 2 3 4 5";
        ParsedObject result = OBJParser.Parse(file);
        Triangle t1 = (Triangle)result.defaultGroup.getChildren().get(0);
        Triangle t2 = (Triangle)result.defaultGroup.getChildren().get(1);
        Triangle t3 = (Triangle)result.defaultGroup.getChildren().get(2);

        assertEquals(t1.p1, result.vertices.get(0));
        assertEquals(t1.p2, result.vertices.get(1));
        assertEquals(t1.p3, result.vertices.get(2));
        assertEquals(t2.p1, result.vertices.get(0));
        assertEquals(t2.p2, result.vertices.get(2));
        assertEquals(t2.p3, result.vertices.get(3));
        assertEquals(t3.p1, result.vertices.get(0));
        assertEquals(t3.p2, result.vertices.get(3));
        assertEquals(t3.p3, result.vertices.get(4));
    }

    @Test void trianglesInGroups()
    {
        String file =   "v -1 1 0\n" +
                        "v -1 0 0\n" +
                        "v 1 0 0\n" +
                        "v 1 1 0\n" +
                        "g FirstGroup\n" +
                        "f 1 2 3\n" +
                        "g SecondGroup\n" +
                        "f 1 3 4";
        ParsedObject result = OBJParser.Parse(file);
        Group g1 = result.groups.get("FirstGroup");
        Group g2 = result.groups.get("SecondGroup");

        Triangle t1 = (Triangle)g1.getChildren().getFirst();
        Triangle t2 = (Triangle)g2.getChildren().getFirst();
        assertEquals(t1.p1, result.vertices.get(0));
        assertEquals(t1.p2, result.vertices.get(1));
        assertEquals(t1.p3, result.vertices.get(2));
        assertEquals(t2.p1, result.vertices.get(0));
        assertEquals(t2.p2, result.vertices.get(2));
        assertEquals(t2.p3, result.vertices.get(3));
    }

    @Test void vertexNormalRecords()
    {
        String file =
                "vn 0 0 1\n" +
                "vn 0.707 0 -0.707\n" +
                "vn 1 2 3";
        ParsedObject result = OBJParser.Parse(file);
        assertEquals(Vector.vector3(0, 0, 1), result.normals.get(0));
        assertEquals(Vector.vector3(0.707, 0, -0.707), result.normals.get(1));
        assertEquals(Vector.vector3(1, 2, 3), result.normals.get(2));
    }

    @Test void facesWithNormals()
    {
        String file =
                "v 0 1 0\n" +
                "v -1 0 0\n" +
                "v 1 0 0\n" +
                "vn -1 0 0\n" +
                "vn 1 0 0\n" +
                "vn 0 1 0\n" +
                "f 1//3 2//1 3//2\n" +
                "f 1/0/3 2/102/1 3/14/2";
        ParsedObject result = OBJParser.Parse(file);
        SmoothTriangle t1 = (SmoothTriangle)result.defaultGroup.getChildren().get(0);
        SmoothTriangle t2 = (SmoothTriangle)result.defaultGroup.getChildren().get(1);

        assertEquals(t1.p1, result.vertices.get(0));
        assertEquals(t1.p2, result.vertices.get(1));
        assertEquals(t1.p3, result.vertices.get(2));
        assertEquals(t1.n1, result.normals.get(2));
        assertEquals(t1.n2, result.normals.get(0));
        assertEquals(t1.n3, result.normals.get(1));
        assertEquals(t2.p1, result.vertices.get(0));
        assertEquals(t2.p2, result.vertices.get(1));
        assertEquals(t2.p3, result.vertices.get(2));
        assertEquals(t2.n1, result.normals.get(2));
        assertEquals(t2.n2, result.normals.get(0));
        assertEquals(t2.n3, result.normals.get(1));
    }
}