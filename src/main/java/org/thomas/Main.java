package org.thomas;
import org.thomas.parser.SceneLoader;
import org.thomas.scene.Canvas;
import java.io.IOException;

public class Main {
    static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: java Main <scene.json> <output.ppm>");
            System.exit(1);
        }

        String scenePath  = args[0];
        String outputPath = args[1];

        System.out.println("Loading scene: " + scenePath);
        SceneLoader.LoadedScene scene = SceneLoader.load(scenePath);

        System.out.println("Rendering...");
        Canvas canvas = scene.camera.render(scene.world);

        canvas.writeToFile(outputPath);
        System.out.println("Done -> " + outputPath);
    }
}