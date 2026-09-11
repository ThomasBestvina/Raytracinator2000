import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.thomas.material.Material;
import org.thomas.material.PhongShadingModel;
import org.thomas.math.Color;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.pattern.*;
import org.thomas.parser.SceneLoader;
import org.thomas.shape.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SceneLoaderTest {

    @TempDir
    Path tempDir;

    private SceneLoader.LoadedScene load(String json) throws IOException {
        Path file = tempDir.resolve("scene.json");
        Files.writeString(file, json);
        return SceneLoader.load(file.toString());
    }

    private String baseScene(String shapesJson) {
        return "{"
                + "\"camera\": {\"width\": 100, \"height\": 50, \"fov\": 90,"
                + "             \"from\": [0,0,-5], \"to\": [0,0,0], \"up\": [0,1,0]},"
                + "\"lights\": [{\"type\": \"point\", \"position\": [0,10,0], \"color\": [1,1,1]}],"
                + "\"materials\": {},"
                + "\"shapes\": [" + shapesJson + "]"
                + "}";
    }

    private static final double EPS = 1e-9;

    @Test
    void cameraResolutionIsLoaded() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(""));
        assertEquals(100, scene.camera.getHsize());
        assertEquals(50,  scene.camera.getVsize());
    }

    @Test
    void cameraFovIsConvertedFromDegrees() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(""));
        assertEquals(Math.toRadians(90), scene.camera.getFov(), EPS);
    }

    @Test
    void cameraTransformIsSet() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(""));
        // view transform from [0,0,-5] looking at origin should not be identity
        assertNotEquals(Matrix.identityMatrix(), scene.camera.getTransform());
    }

    @Test
    void pointLightPositionIsLoaded() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(""));
        Vector pos = scene.world.getLights().getFirst().getPosition();
        assertEquals(Vector.point(0, 10, 0), pos);
    }

    @Test
    void pointLightColorIsLoaded() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(""));
        Color c = scene.world.getLights().getFirst().getIntensity();
        assertEquals(new Color(1, 1, 1), c);
    }

    @Test
    void unknownLightTypeThrows() {
        assertThrows(RuntimeException.class, () -> load(baseScene("").replace("\"point\"", "\"area\"")));
    }

    @Test
    void namedMaterialColorIsApplied() throws IOException {
        String json = "{"
                + "\"camera\": {\"width\": 10, \"height\": 10, \"fov\": 60,"
                + "             \"from\": [0,0,-5], \"to\": [0,0,0], \"up\": [0,1,0]},"
                + "\"lights\": [{\"type\": \"point\", \"position\": [0,10,0], \"color\": [1,1,1]}],"
                + "\"materials\": {\"red\": {\"color\": [1,0,0]}},"
                + "\"shapes\": [{\"type\": \"plane\", \"material\": \"red\", \"transform\": []}]"
                + "}";
        SceneLoader.LoadedScene scene = load(json);
        Shape plane = scene.world.getShapes().get(0);
        assertEquals(new Color(1, 0, 0), plane.getMaterial().albedo);
    }

    @Test
    void namedMaterialPhysicalPropertiesAreApplied() throws IOException {
        String json = "{"
                + "\"camera\": {\"width\": 10, \"height\": 10, \"fov\": 60,"
                + "             \"from\": [0,0,-5], \"to\": [0,0,0], \"up\": [0,1,0]},"
                + "\"lights\": [{\"type\": \"point\", \"position\": [0,10,0], \"color\": [1,1,1]}],"
                + "\"materials\": {\"m\": {"
                + "    \"shading\": {\"model\": \"phong\", \"ambient\": 0.3, \"diffuse\": 0.7,"
                + "                  \"specular\": 0.5, \"shininess\": 100},"
                + "    \"reflective\": 0.4, \"transparency\": 0.9, \"refractive\": 1.5}},"
                + "\"shapes\": [{\"type\": \"plane\", \"material\": \"m\", \"transform\": []}]"
                + "}";
        SceneLoader.LoadedScene scene = load(json);
        Material mat = scene.world.getShapes().get(0).getMaterial();
        PhongShadingModel phong = (PhongShadingModel) mat.shadingModel;
        assertEquals(0.3,  phong.ambient,      EPS);
        assertEquals(0.7,  phong.diffuse,      EPS);
        assertEquals(0.5,  phong.specular,     EPS);
        assertEquals(100,  phong.shininess,    EPS);
        assertEquals(0.4,  mat.reflective,     EPS);
        assertEquals(0.9,  mat.transparency,   EPS);
        assertEquals(1.5,  mat.refractive,     EPS);
    }

    @Test
    void unknownNamedMaterialThrows() {
        String json = "{"
                + "\"camera\": {\"width\": 10, \"height\": 10, \"fov\": 60,"
                + "             \"from\": [0,0,-5], \"to\": [0,0,0], \"up\": [0,1,0]},"
                + "\"lights\": [{\"type\": \"point\", \"position\": [0,10,0], \"color\": [1,1,1]}],"
                + "\"materials\": {},"
                + "\"shapes\": [{\"type\": \"plane\", \"material\": \"doesNotExist\", \"transform\": []}]"
                + "}";
        assertThrows(RuntimeException.class, () -> load(json));
    }

    @Test
    void inlineMaterialIsApplied() throws IOException {
        String json = "{"
                + "\"camera\": {\"width\": 10, \"height\": 10, \"fov\": 60,"
                + "             \"from\": [0,0,-5], \"to\": [0,0,0], \"up\": [0,1,0]},"
                + "\"lights\": [{\"type\": \"point\", \"position\": [0,10,0], \"color\": [1,1,1]}],"
                + "\"materials\": {},"
                + "\"shapes\": [{\"type\": \"plane\","
                + "              \"material\": {\"color\": [0,1,0], \"reflective\": 0.2},"
                + "              \"transform\": []}]"
                + "}";
        SceneLoader.LoadedScene scene = load(json);
        Material mat = scene.world.getShapes().get(0).getMaterial();
        assertEquals(new Color(0, 1, 0), mat.albedo);
        assertEquals(0.2, mat.reflective, EPS);
    }

    private String sceneWithPattern(String patternJson) {
        return "{"
                + "\"camera\": {\"width\": 10, \"height\": 10, \"fov\": 60,"
                + "             \"from\": [0,0,-5], \"to\": [0,0,0], \"up\": [0,1,0]},"
                + "\"lights\": [{\"type\": \"point\", \"position\": [0,10,0], \"color\": [1,1,1]}],"
                + "\"materials\": {\"m\": {\"pattern\": " + patternJson + "}},"
                + "\"shapes\": [{\"type\": \"plane\", \"material\": \"m\", \"transform\": []}]"
                + "}";
    }

    @Test
    void checkersPatternIsLoaded() throws IOException {
        SceneLoader.LoadedScene scene = load(sceneWithPattern(
                "{\"type\": \"checkers\", \"a\": [1,0,0], \"b\": [0,0,1]}"));
        Pattern p = scene.world.getShapes().get(0).getMaterial().pattern;
        assertInstanceOf(CheckersPattern.class, p);
    }

    @Test
    void stripePatternIsLoaded() throws IOException {
        SceneLoader.LoadedScene scene = load(sceneWithPattern(
                "{\"type\": \"stripe\", \"a\": [1,1,1], \"b\": [0,0,0]}"));
        Pattern p = scene.world.getShapes().get(0).getMaterial().pattern;
        assertInstanceOf(StripePattern.class, p);
    }

    @Test
    void ringPatternIsLoaded() throws IOException {
        SceneLoader.LoadedScene scene = load(sceneWithPattern(
                "{\"type\": \"ring\", \"a\": [1,0,0], \"b\": [1,1,1]}"));
        Pattern p = scene.world.getShapes().get(0).getMaterial().pattern;
        assertInstanceOf(RingPattern.class, p);
    }

    @Test
    void gradientPatternIsLoaded() throws IOException {
        SceneLoader.LoadedScene scene = load(sceneWithPattern(
                "{\"type\": \"gradient\", \"a\": [0,0,0], \"b\": [1,1,1]}"));
        Pattern p = scene.world.getShapes().get(0).getMaterial().pattern;
        assertInstanceOf(LinearGradientPattern.class, p);
    }

    @Test
    void unknownPatternTypeThrows() {
        assertThrows(RuntimeException.class, () -> load(sceneWithPattern(
                "{\"type\": \"polkadot\", \"a\": [0,0,0], \"b\": [1,1,1]}")));
    }

    @Test
    void patternTransformIsApplied() throws IOException {
        SceneLoader.LoadedScene scene = load(sceneWithPattern(
                "{\"type\": \"checkers\", \"a\": [1,0,0], \"b\": [0,0,1],"
                        + "\"transform\": [[\"scale\", 2, 2, 2]]}"));
        Pattern p = scene.world.getShapes().get(0).getMaterial().pattern;
        Matrix expected = Matrix.scalar(2, 2, 2);
        assertEquals(expected, p.getTransform());
    }

    @Test
    void planeIsAddedDirectlyToWorld() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"plane\", \"transform\": []}"));
        // World should contain exactly the plane (no BVH since no finite shapes)
        assertEquals(1, scene.world.getShapes().size());
        assertInstanceOf(Plane.class, scene.world.getShapes().get(0));
    }

    @Test
    void planeTransformIsApplied() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"plane\", \"transform\": [[\"translate\", 0, 1, 0]]}"));
        Shape plane = scene.world.getShapes().get(0);
        Matrix expected = Matrix.translation(0, 1, 0);
        assertEquals(expected, plane.getTransform());
    }

    @Test
    void sphereIsWrappedInBVH() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"sphere\", \"transform\": []}"));
        assertEquals(1, scene.world.getShapes().size());
        assertInstanceOf(BVHNode.class, scene.world.getShapes().get(0));
    }

    @Test
    void cubeIsWrappedInBVH() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"cube\", \"transform\": []}"));
        assertEquals(1, scene.world.getShapes().size());
        assertInstanceOf(BVHNode.class, scene.world.getShapes().get(0));
    }

    @Test
    void planeAndSphereProducePlaneAndBVH() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"plane\", \"transform\": []},"
                        + "{\"type\": \"sphere\", \"transform\": []}"));
        assertEquals(2, scene.world.getShapes().size());
        assertInstanceOf(Plane.class,   scene.world.getShapes().get(0));
        assertInstanceOf(BVHNode.class, scene.world.getShapes().get(1));
    }

    @Test
    void unknownShapeTypeThrows() {
        assertThrows(RuntimeException.class, () -> load(baseScene(
                "{\"type\": \"teapot\", \"transform\": []}")));
    }

    @Test
    void translateTransformIsApplied() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"plane\", \"transform\": [[\"translate\", 1, 2, 3]]}"));
        Matrix t = scene.world.getShapes().get(0).getTransform();
        assertEquals(Matrix.translation(1, 2, 3), t);
    }

    @Test
    void scaleTransformIsApplied() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"plane\", \"transform\": [[\"scale\", 2, 3, 4]]}"));
        Matrix t = scene.world.getShapes().get(0).getTransform();
        assertEquals(Matrix.scalar(2, 3, 4), t);
    }

    @Test
    void rotateXTransformIsApplied() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"plane\", \"transform\": [[\"rotate_x\", 90]]}"));
        Matrix t = scene.world.getShapes().get(0).getTransform();
        assertEquals(Matrix.rotationX(Math.toRadians(90)), t);
    }

    @Test
    void rotateYTransformIsApplied() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"plane\", \"transform\": [[\"rotate_y\", 180]]}"));
        Matrix t = scene.world.getShapes().get(0).getTransform();
        assertEquals(Matrix.rotationY(Math.toRadians(180)), t);
    }

    @Test
    void rotateZTransformIsApplied() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"plane\", \"transform\": [[\"rotate_z\", 45]]}"));
        Matrix t = scene.world.getShapes().get(0).getTransform();
        assertEquals(Matrix.rotationZ(Math.toRadians(45)), t);
    }

    @Test
    void multipleTransformsAreChainedInDeclarationOrder() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"plane\", \"transform\": ["
                        + "[\"translate\", 1, 0, 0],"
                        + "[\"rotate_y\", 90]"
                        + "]}"));
        Matrix t = scene.world.getShapes().get(0).getTransform();
        Matrix expected = Matrix.translation(1, 0, 0).multiply(Matrix.rotationY(Math.toRadians(90)));
        assertEquals(expected, t);
    }

    @Test
    void emptyTransformListYieldsIdentity() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"plane\", \"transform\": []}"));
        assertEquals(Matrix.identityMatrix(), scene.world.getShapes().get(0).getTransform());
    }

    @Test
    void unknownTransformOperationThrows() {
        assertThrows(RuntimeException.class, () -> load(baseScene(
                "{\"type\": \"plane\", \"transform\": [[\"shear\", 1, 0, 0, 0, 0, 0]]}")));
    }

    @Test
    void missingMaterialKeyYieldsDefaults() throws IOException {
        SceneLoader.LoadedScene scene = load(baseScene(
                "{\"type\": \"plane\", \"transform\": []}"));
        Material mat = scene.world.getShapes().get(0).getMaterial();
        Material defaults = new Material();
        assertEquals(((PhongShadingModel)defaults.shadingModel).ambient,   ((PhongShadingModel)mat.shadingModel).ambient,   EPS);
        assertEquals(((PhongShadingModel)defaults.shadingModel).diffuse,   ((PhongShadingModel)mat.shadingModel).diffuse,   EPS);
        assertEquals(((PhongShadingModel)defaults.shadingModel).specular,  ((PhongShadingModel)mat.shadingModel).specular,  EPS);
        assertEquals(((PhongShadingModel)defaults.shadingModel).shininess, ((PhongShadingModel)mat.shadingModel).shininess, EPS);
        assertEquals(defaults.albedo,     mat.albedo);
        assertNull(mat.pattern);
    }
}