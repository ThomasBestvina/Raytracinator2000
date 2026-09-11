import org.junit.jupiter.api.Test;
import org.thomas.math.Color;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.raytracer.Ray;
import org.thomas.scene.Camera;
import org.thomas.scene.Canvas;
import org.thomas.scene.World;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CameraTest {
    @Test
    void cameraConstruction() {
        Camera c = new Camera(160, 120, Math.PI / 2);
        assertEquals(160, c.getHsize());
        assertEquals(120, c.getVsize());
        assertEquals(Math.PI/2, c.getFov());
        assertEquals(Matrix.identityMatrix(), c.getTransform());
    }

    @Test
    void PixelSizeHorizontalCanvas() {
        Camera c = new Camera(200, 125, Math.PI / 2);
        assertEquals(0.01, c.getPixelSize(), 0.0001);
    }

    @Test
    void PixelSizeVerticalCanvas() {
        Camera c = new Camera(125, 200, Math.PI / 2);
        assertEquals(0.01, c.getPixelSize(), 0.0001);
    }

    @Test
    void ConstructingRayThroughCenterOfCanvas(){
        Camera c = new Camera(201,101,Math.PI/2);
        Ray r = c.rayForPixel(100,50);
        assertEquals(Vector.point(0,0,0), r.getOrigin());
        assertEquals(Vector.vector3(0,0,-1), r.getDirection());
    }
    @Test
    void ConstructingRayCornerOfCanvas(){
        Camera c = new Camera(201,101,Math.PI/2);
        Ray r = c.rayForPixel(0,0);
        assertEquals(Vector.point(0,0,0), r.getOrigin());
        assertEquals(Vector.vector3(0.66519,0.33259,-0.66851), r.getDirection());
    }
    @Test
    void ConstructingRayAfterCameraTransformed(){
        Camera c = new Camera(201,101,Math.PI/2);
        c.setTransform(Matrix.rotationY(Math.PI/4).multiply(Matrix.translation(0,-2,5)));
        Ray r = c.rayForPixel(100,50);
        assertEquals(Vector.point(0,2,-5), r.getOrigin());
        assertEquals(Vector.vector3(Math.sqrt(2)/2, 0, -Math.sqrt(2)/2), r.getDirection());
    }

    @Test
    void RenderingWorldWithCamera()
    {
        World w = World.defaultWorld();
        Camera c = new Camera(11,11,Math.PI/2);
        Vector from = Vector.point(0,0,-5);
        Vector to = Vector.point(0,0,0);
        Vector up = Vector.vector3(0,1,0);
        c.setTransform(World.viewTransform(from,to,up));

        Canvas image = c.render(w);

        Color i = image.getColor(5,5);

        assertEquals(new Color(0.38066,0.47583, 0.2855), image.getColor(5,5));
    }
}