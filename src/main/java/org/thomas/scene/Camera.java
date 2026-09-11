package org.thomas.scene;

import org.thomas.math.Color;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.raytracer.Ray;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Camera {
    private int hsize;
    private int vsize;
    private double fov;
    private double pixelSize;
    private double halfWidth;
    private double halfHeight;
    private Matrix transform = Matrix.identityMatrix();
    private boolean antiAlias;
    private int antiAliasSamples;

    public Camera(int hsize, int vsize, double fov) {
        this(hsize, vsize, fov, false, 0);
    }

    public Camera(int hsize, int vsize, double fov, boolean antiAlias, int antiAliasSamples) {
        this.hsize = hsize;
        this.vsize = vsize;
        this.fov = fov;
        this.antiAlias = antiAlias;
        this.antiAliasSamples = antiAliasSamples;
        setPixelSize();
    }

    public void setTransform(Matrix transform) {
        this.transform = transform;
    }

    public int getHsize() {
        return hsize;
    }

    public int getVsize() {
        return vsize;
    }

    public double getFov() {
        return fov;
    }

    public Matrix getTransform() {
        return transform;
    }

    private void setPixelSize() {
        double half_view = Math.tan(fov/2);
        double aspect = (double) hsize / vsize;

        if(aspect >= 1) {
            halfWidth = half_view;
            halfHeight = half_view / aspect;
        }
        else{
            halfWidth = half_view * aspect;
            halfHeight = half_view;
        }

        pixelSize = (halfWidth * 2) / hsize;
    }

    public double getPixelSize() {
        return pixelSize;
    }

    public Ray rayForPixel(int px, int py) {
        return rayForPixel(px,py,0.5,0.5);
    }

    public Ray rayForPixel(int px, int py, double offsetX, double offsetY)
    {
        double xOffset = (px + offsetX) * pixelSize;
        double yOffset = (py + offsetY) * pixelSize;
        double world_x = halfWidth - xOffset;
        double world_y = halfHeight - yOffset;
        Matrix inverseTransform = transform.inverse();
        Vector pixel = inverseTransform.multiply(Vector.point(world_x, world_y, -1));
        Vector origin = inverseTransform.multiply(Vector.point(0, 0, 0));
        Vector direction = pixel.subtract(origin).normalize();
        return new Ray(origin, direction);
    }


    public Canvas render(World w) {
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(vsize);

        int total_rows = vsize;
        AtomicInteger completedRows = new AtomicInteger(0);

        Canvas image = new Canvas(hsize, vsize);

        for(int y = 0; y < vsize; y++){
            final int fy = y;
            int zero = 0;
            System.out.printf("\rRendering: [%-50s] %d%%", "=".repeat(zero), zero);
            executor.submit(() -> {
                for(int x = 0; x < hsize; x++){
                    Color color;
                    if(antiAlias && antiAliasSamples > 1){
                        double r = 0, g = 0, b = 0;
                        int n = antiAliasSamples;

                        double [][] corners = {
                                {0.0,0.0}, {1.0, 0.0}, {0.0, 1.0}, {1.0, 1.0}
                        };

                        Color[] cornerColors = new Color[4];
                        for(int i = 0; i < 4; i++){
                            Ray ray = rayForPixel(x, fy, corners[i][0], corners[i][1]);
                            cornerColors[i] = w.colorAt(ray);
                        }

                        if(cornersAreSimilar(cornerColors, 0.05)){
                            for(Color c : cornerColors){
                                r += c.r(); g += c.g(); b += c.b();
                            }
                            color = new Color(r/4, g/4, b/4);
                        } else {
                            for(int sy = 0; sy < n; sy++){
                                for(int sx = 0; sx < n; sx++){
                                    double offsetX = (sx + Math.random()) / n;
                                    double offsetY = (sy + Math.random()) / n;
                                    Ray ray = rayForPixel(x, fy, offsetX, offsetY);
                                    Color sample = w.colorAt(ray);
                                    r += sample.r(); g += sample.g(); b += sample.b();
                                }
                            }
                            int totalSamples = n * n;
                            color = new Color(r/totalSamples, g/totalSamples, b/totalSamples);
                        }
                    }
                    else {
                        Ray r = rayForPixel(x, fy);
                        color = w.colorAt(r);

                    }
                    image.setColor(x, fy, color);
                }
                int completed = completedRows.incrementAndGet();
                int percent = (completed * 100) / total_rows;
                System.out.printf("\rRendering: [%-50s] %d%%", "=".repeat(percent / 2), percent);
                latch.countDown();
            });
        }
        try{
            latch.await();
            System.out.println("\rRendering: [==================================================] 100%");
            System.out.println("Done.");
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
        return image;
    }

    private boolean cornersAreSimilar(Color[] corners, double threshold){
        for(int i = 1; i < corners.length; i++){
            if(Math.abs(corners[0].r() - corners[i].r()) > threshold ||
                    Math.abs(corners[0].g() - corners[i].g()) > threshold ||
                    Math.abs(corners[0].b() - corners[i].b()) > threshold){
                return false;
            }
        }
        return true;
    }
}