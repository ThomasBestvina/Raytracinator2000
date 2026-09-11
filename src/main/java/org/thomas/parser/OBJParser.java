package org.thomas.parser;

import org.thomas.math.Vector;
import org.thomas.shape.Group;
import org.thomas.shape.SmoothTriangle;
import org.thomas.shape.Triangle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OBJParser {
    public static int linesIgnored;

    public static String fileToString(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    public static ParsedObject Parse(String filecontents)
    {
        linesIgnored = 0;
        ParsedObject result = new ParsedObject();
        String currentGroup = "";
        String[] lines = filecontents.split("\n");

        for (String line : lines)
        {
            String[] l = line.split(" ");

            switch (l[0])
            {
                case "v":
                    if (l.length != 4) { linesIgnored++; break; }
                    result.vertices.add(Vector.point(
                            Double.parseDouble(l[1]),
                            Double.parseDouble(l[2]),
                            Double.parseDouble(l[3])));
                    break;

                case "vn":
                    if (l.length != 4) { linesIgnored++; break; }
                    result.normals.add(Vector.vector3(
                            Double.parseDouble(l[1]),
                            Double.parseDouble(l[2]),
                            Double.parseDouble(l[3])));
                    break;

                case "vt":
                    if (l.length < 3) { linesIgnored++; break; }
                    result.texCoords.add(new double[]{
                            Double.parseDouble(l[1]),
                            Double.parseDouble(l[2])});
                    break;

                case "f":
                    if (l.length < 4) { linesIgnored++; break; }

                    int[][] faceVerts = new int[l.length - 1][];
                    for (int i = 1; i < l.length; i++) {
                        faceVerts[i - 1] = parseFaceVertex(l[i]);
                    }

                    boolean hasNormals   = faceVerts[0][2] != -1;
                    boolean hasTexCoords = faceVerts[0][1] != -1 && !result.texCoords.isEmpty();

                    if (l.length == 4) {
                        Triangle tri = hasNormals
                                ? new SmoothTriangle(
                                result.vertices.get(faceVerts[0][0]),
                                result.vertices.get(faceVerts[1][0]),
                                result.vertices.get(faceVerts[2][0]),
                                result.normals.get(faceVerts[0][2]),
                                result.normals.get(faceVerts[1][2]),
                                result.normals.get(faceVerts[2][2]))
                                : new Triangle(
                                result.vertices.get(faceVerts[0][0]),
                                result.vertices.get(faceVerts[1][0]),
                                result.vertices.get(faceVerts[2][0]));
                        if (hasTexCoords) {
                            tri.uv1 = result.texCoords.get(faceVerts[0][1]);
                            tri.uv2 = result.texCoords.get(faceVerts[1][1]);
                            tri.uv3 = result.texCoords.get(faceVerts[2][1]);
                        }
                        addToGroup(result, currentGroup, tri);
                    } else {
                        List<Vector> verts = new ArrayList<>();
                        List<Vector> norms = new ArrayList<>();
                        for (int[] fv : faceVerts) {
                            verts.add(result.vertices.get(fv[0]));
                            if (hasNormals) norms.add(result.normals.get(fv[2]));
                        }
                        List<Triangle> tris = hasNormals
                                ? (hasTexCoords
                                   ? fanTriangulation(verts, norms, faceVerts, result.texCoords)
                                   : fanTriangulation(verts, norms))
                                : (hasTexCoords
                                   ? fanTriangulation(verts, faceVerts, result.texCoords)
                                   : fanTriangulation(verts));
                        for (Triangle t : tris) addToGroup(result, currentGroup, t);
                    }
                    break;

                case "g":
                    if (l.length > 2) { linesIgnored++; break; }
                    if (l.length == 1) {
                        currentGroup = "";
                    } else {
                        currentGroup = l[1];
                        if (!result.groups.containsKey(currentGroup)) {
                            result.groups.put(currentGroup, new Group());
                        }
                    }
                    break;

                default:
                    linesIgnored++;
            }
        }
        return result;
    }

    private static List<Triangle> fanTriangulation(List<Vector> vertices)
    {
        List<Triangle> result = new LinkedList<>();
        for (int i = 1; i < vertices.size() - 1; i++) {
            result.add(new Triangle(vertices.get(0), vertices.get(i), vertices.get(i + 1)));
        }
        return result;
    }

    private static List<Triangle> fanTriangulation(List<Vector> vertices, int[][] faceVerts, List<double[]> texCoords)
    {
        List<Triangle> result = new LinkedList<>();
        for (int i = 1; i < vertices.size() - 1; i++) {
            Triangle t = new Triangle(vertices.get(0), vertices.get(i), vertices.get(i + 1));
            t.uv1 = texCoords.get(faceVerts[0][1]);
            t.uv2 = texCoords.get(faceVerts[i][1]);
            t.uv3 = texCoords.get(faceVerts[i + 1][1]);
            result.add(t);
        }
        return result;
    }

    private static List<Triangle> fanTriangulation(List<Vector> vertices, List<Vector> normals)
    {
        List<Triangle> result = new LinkedList<>();
        for (int i = 1; i < vertices.size() - 1; i++) {
            result.add(new SmoothTriangle(
                    vertices.get(0), vertices.get(i), vertices.get(i + 1),
                    normals.get(0),  normals.get(i),  normals.get(i + 1)));
        }
        return result;
    }

    private static List<Triangle> fanTriangulation(List<Vector> vertices, List<Vector> normals, int[][] faceVerts, List<double[]> texCoords)
    {
        List<Triangle> result = new LinkedList<>();
        for (int i = 1; i < vertices.size() - 1; i++) {
            Triangle t = new SmoothTriangle(
                    vertices.get(0), vertices.get(i), vertices.get(i + 1),
                    normals.get(0),  normals.get(i),  normals.get(i + 1));
            t.uv1 = texCoords.get(faceVerts[0][1]);
            t.uv2 = texCoords.get(faceVerts[i][1]);
            t.uv3 = texCoords.get(faceVerts[i + 1][1]);
            result.add(t);
        }
        return result;
    }

    private static int[] parseFaceVertex(String face)
    {
        String[] parts = face.split("/");
        int vertexIndex  = Integer.parseInt(parts[0]) - 1;
        int textureIndex = -1;
        int normalIndex  = -1;
        if (parts.length >= 2 && !parts[1].isEmpty())
            textureIndex = Integer.parseInt(parts[1]) - 1;
        if (parts.length >= 3 && !parts[2].isEmpty())
            normalIndex = Integer.parseInt(parts[2]) - 1;
        return new int[]{vertexIndex, textureIndex, normalIndex};
    }

    private static void addToGroup(ParsedObject result, String currentGroup, Triangle tri)
    {
        if (currentGroup.isEmpty()) {
            result.defaultGroup.addChild(tri);
        } else {
            result.groups.get(currentGroup).addChild(tri);
        }
    }
}