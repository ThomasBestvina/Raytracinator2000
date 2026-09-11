package org.thomas.parser;

import org.thomas.light.PointLight;
import org.thomas.material.Material;
import org.thomas.material.PhongShadingModel;
import org.thomas.material.ShadingModel;
import org.thomas.material.Texture;
import org.thomas.math.Color;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.normalperturber.NormalMap;
import org.thomas.pattern.*;
import org.thomas.scene.Camera;
import org.thomas.scene.World;
import org.thomas.shape.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SceneLoader {
    public static class LoadedScene {
        public final World world;
        public final Camera camera;
        public LoadedScene(World world, Camera camera) {
            this.world = world;
            this.camera = camera;
        }
    }

    public static LoadedScene load(String path) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(path)));
        Map<String, Object> root = asMap(JsonParser.parse(json));

        Path baseDir = Paths.get(path).toAbsolutePath().getParent();

        Map<String, Material> materials = parseMaterials(root, baseDir);
        World world = new World();
        parseShapes(root, materials, world, baseDir);
        parseLights(root, world);
        Camera camera = parseCamera(root);

        if(root.containsKey("background"))
        {
            String envPath = baseDir.resolve(asString(root.get("background"))).normalize().toString();
            world.setEnvironment(new Texture(envPath));
        }

        return new LoadedScene(world, camera);
    }


    private static Camera parseCamera(Map<String, Object> root) {
        Map<String, Object> c = asMap(root.get("camera"));
        int width  = (int) asDouble(c.get("width"));
        int height = (int) asDouble(c.get("height"));
        double fov = Math.toRadians(asDouble(c.get("fov")));

        Vector from = parsePoint(asList(c.get("from")));
        Vector to   = parsePoint(asList(c.get("to")));
        Vector up   = parseVector(asList(c.get("up")));

        boolean enabled = false;
        int samples = 1;

        if(c.containsKey("antialiasing")) {
            Map<String, Object> aa = asMap(c.get("antialiasing"));
            enabled = (Boolean) aa.get("enabled");
            samples = 1;
            if(enabled)
            {
                samples = (int) asDouble(aa.get("samples"));
            }
        }

        Camera cam = new Camera(width, height, fov, enabled, samples);
        cam.setTransform(World.viewTransform(from, to, up));

        return cam;
    }

    private static void parseLights(Map<String, Object> root, World world) {
        if (!root.containsKey("lights")) return;
        for (Object entry : asList(root.get("lights"))) {
            Map<String, Object> l = asMap(entry);
            String type = asString(l.get("type"));
            if (type.equals("point")) {
                Vector position = parsePoint(asList(l.get("position")));
                Color color     = parseColor(asList(l.get("color")));
                world.addLight(new PointLight(position, color));
            } else {
                throw new RuntimeException("Unknown light type: " + type);
            }
        }
    }


    private static Map<String, Material> parseMaterials(Map<String, Object> root, Path baseDir) throws IOException {
        Map<String, Material> result = new LinkedHashMap<>();
        if (!root.containsKey("materials")) return result;
        Map<String, Object> mats = asMap(root.get("materials"));
        for (Map.Entry<String, Object> entry : mats.entrySet()) {
            result.put(entry.getKey(), parseMaterial(asMap(entry.getValue()),baseDir));
        }
        return result;
    }

    private static Material parseMaterial(Map<String, Object> m, Path baseDir) throws IOException {
        Material mat = new Material();
        if (m.containsKey("color"))       mat.albedo = parseColor(asList(m.get("color")));
        if (m.containsKey("reflective"))  mat.reflective  = asDouble(m.get("reflective"));
        if (m.containsKey("transparency"))mat.transparency= asDouble(m.get("transparency"));
        if (m.containsKey("refractive"))  mat.refractive  = asDouble(m.get("refractive"));
        if (m.containsKey("castsShadow")) mat.castsShadow = (Boolean) m.get("castsShadow");
        if (m.containsKey("pattern"))     mat.pattern     = parsePattern(asMap(m.get("pattern")), baseDir);
        if (m.containsKey("normal_map")) {
            String normalPath = baseDir.resolve(asString(m.get("normal_map"))).normalize().toString();
            mat.normalPerturber = new NormalMap(new Texture(normalPath));
        }
        if (m.containsKey("shading")) {
            mat.shadingModel = parseShadingModel(asMap(m.get("shading")));
        }
        return mat;
    }

    private static ShadingModel parseShadingModel(Map<String, Object> s) {
        String model = asString(s.get("model"));
        switch (model) {
            case "phong": {
                double ambient   = s.containsKey("ambient")   ? asDouble(s.get("ambient"))   : 0.1;
                double diffuse   = s.containsKey("diffuse")   ? asDouble(s.get("diffuse"))   : 0.9;
                double specular  = s.containsKey("specular")  ? asDouble(s.get("specular"))  : 0.9;
                double shininess = s.containsKey("shininess") ? asDouble(s.get("shininess")) : 200.0;
                return new PhongShadingModel(ambient, diffuse, specular, shininess);
            }
            default:
                throw new RuntimeException("Unknown shading model: " + model);
        }
    }

    private static Pattern parsePattern(Map<String, Object> p, Path baseDir) throws IOException {
        String type = asString(p.get("type"));

        Color a,b;

        Pattern pattern;
        switch (type) {
            case "checkers":
                a = parseColor(asList(p.get("a")));
                b = parseColor(asList(p.get("b")));
                pattern = new CheckersPattern(a, b);
                break;
            case "stripe":
                a = parseColor(asList(p.get("a")));
                b = parseColor(asList(p.get("b")));
                pattern = new StripePattern(a, b);
                break;
            case "ring":
                a = parseColor(asList(p.get("a")));
                b = parseColor(asList(p.get("b")));
                pattern = new RingPattern(a, b);
                break;
            case "gradient":
                a = parseColor(asList(p.get("a")));
                b = parseColor(asList(p.get("b")));
                pattern = new LinearGradientPattern(a, b);
                break;
            case "texture":
                String texpath = baseDir.resolve(asString(p.get("texture"))).normalize().toString();
                pattern = new TexturePattern(new Texture(texpath));
                break;
            default: throw new RuntimeException("Unknown pattern type: " + type);
        }
        if (p.containsKey("transform")) {
            pattern.setTransform(parseTransformList(asList(p.get("transform"))));
        }

        return pattern;
    }


    private static void parseShapes(Map<String, Object> root,
                                    Map<String, Material> materials,
                                    World world, Path baseDir) throws IOException {
        if (!root.containsKey("shapes")) return;

        List<Shape> finiteShapes = new ArrayList<>();

        for (Object entry : asList(root.get("shapes"))) {
            Map<String, Object> s = asMap(entry);
            String type = asString(s.get("type"));

            Material mat = resolveMaterial(s, materials, baseDir);
            Matrix transform = s.containsKey("transform")
                    ? parseTransformList(asList(s.get("transform")))
                    : Matrix.identityMatrix();

            switch (type) {
                case "plane": {
                    Plane plane = new Plane();
                    plane.setMaterial(mat);
                    plane.setTransform(transform);
                    world.addShape(plane);   // infinite and thus should not be on bvh
                    break;
                }
                case "sphere": {
                    Sphere sphere = new Sphere();
                    sphere.setMaterial(mat);
                    sphere.setTransform(transform);
                    finiteShapes.add(sphere);
                    break;
                }
                case "cube": {
                    Cube cube = new Cube();
                    cube.setMaterial(mat);
                    cube.setTransform(transform);
                    finiteShapes.add(cube);
                    break;
                }
                case "triangle": {
                    List<Object> p1 = asList(s.get("p1"));
                    List<Object> p2 = asList(s.get("p2"));
                    List<Object> p3 = asList(s.get("p3"));
                    Triangle tri = new Triangle(
                            parsePoint(p1), parsePoint(p2), parsePoint(p3));
                    tri.setMaterial(mat);
                    tri.setTransform(transform);
                    finiteShapes.add(tri);
                    break;
                }
                case "obj": {
                    String objPath = asString(s.get("path"));
                    objPath = baseDir.resolve(objPath).normalize().toString();
                    System.out.println("Parsing OBJ: " + objPath);
                    String contents = OBJParser.fileToString(objPath);
                    ParsedObject parsed = OBJParser.Parse(contents);
                    System.out.println("Lines ignored: " + OBJParser.linesIgnored);

                    List<Shape> triangles = new ArrayList<>();
                    flattenGroup(parsed.defaultGroup, transform, triangles);
                    System.out.println("Triangles: " + triangles.size());

                    for (Shape t : triangles) t.setMaterial(mat);
                    finiteShapes.addAll(triangles);
                    break;
                }
                default:
                    throw new RuntimeException("Unknown shape type: " + type);
            }
        }

        if (!finiteShapes.isEmpty()) {
            BVHNode bvh = BVHNode.build(finiteShapes);
            world.addShape(bvh);
        }
    }


    private static Matrix parseTransformList(List<Object> transforms) {
        if (transforms.isEmpty()) return Matrix.identityMatrix();

        List<Object> reversed = new ArrayList<>(transforms);
        Collections.reverse(reversed);

        Matrix result = Matrix.identityMatrix();
        for (Object t : reversed) {
            result = result.multiply(parseSingleTransform(asList(t)));
        }
        return result;
    }

    private static Matrix parseSingleTransform(List<Object> t) {
        String op = asString(t.get(0));
        switch (op) {
            case "translate":
                return Matrix.translation(asDouble(t.get(1)), asDouble(t.get(2)), asDouble(t.get(3)));
            case "scale":
                return Matrix.scalar(asDouble(t.get(1)), asDouble(t.get(2)), asDouble(t.get(3)));
            case "rotate_x":
                return Matrix.rotationX(Math.toRadians(asDouble(t.get(1))));
            case "rotate_y":
                return Matrix.rotationY(Math.toRadians(asDouble(t.get(1))));
            case "rotate_z":
                return Matrix.rotationZ(Math.toRadians(asDouble(t.get(1))));
            default:
                throw new RuntimeException("Unknown transform operation: " + op);
        }
    }


    private static void flattenGroup(Group group, Matrix parentTransform, List<Shape> out) {
        Matrix worldTransform = parentTransform.multiply(group.getTransform());
        for (Shape child : group.getChildren()) {
            if (child instanceof Group) {
                flattenGroup((Group) child, worldTransform, out);
            } else {
                child.setTransform(worldTransform.multiply(child.getTransform()));
                child.setParent(null);
                out.add(child);
            }
        }
    }


    private static Material resolveMaterial(Map<String, Object> shape,
                                            Map<String, Material> materials, Path baseDir) throws IOException {
        if (!shape.containsKey("material")) return new Material();
        Object m = shape.get("material");
        if (m instanceof String) {
            String name = (String) m;
            if (!materials.containsKey(name)) {
                throw new RuntimeException("Unknown material: " + name);
            }
            return materials.get(name);
        }
        return parseMaterial(asMap(m), baseDir);
    }


    private static Color parseColor(List<Object> list) {
        return new Color(asDouble(list.get(0)), asDouble(list.get(1)), asDouble(list.get(2)));
    }

    private static Vector parsePoint(List<Object> list) {
        return Vector.point(asDouble(list.get(0)), asDouble(list.get(1)), asDouble(list.get(2)));
    }

    private static Vector parseVector(List<Object> list) {
        return Vector.vector3(asDouble(list.get(0)), asDouble(list.get(1)), asDouble(list.get(2)));
    }


    /*
    Json type coercions
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object o) {
        if (o instanceof Map) return (Map<String, Object>) o;
        throw new RuntimeException("Expected JSON object, got: " + (o == null ? "null" : o.getClass().getSimpleName()));
    }

    @SuppressWarnings("unchecked")
    private static List<Object> asList(Object o) {
        if (o instanceof List) return (List<Object>) o;
        throw new RuntimeException("Expected JSON array, got: " + (o == null ? "null" : o.getClass().getSimpleName()));
    }

    private static String asString(Object o) {
        if (o instanceof String) return (String) o;
        throw new RuntimeException("Expected string, got: " + (o == null ? "null" : o.getClass().getSimpleName()));
    }

    private static double asDouble(Object o) {
        if (o instanceof Double) return (Double) o;
        throw new RuntimeException("Expected number, got: " + (o == null ? "null" : o.getClass().getSimpleName()));
    }
}