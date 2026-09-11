package org.thomas.light;

import org.thomas.math.Color;
import org.thomas.math.Vector;
import org.thomas.scene.World;

import java.util.List;

public interface Light {
    Color getIntensity();
    Vector getPosition();

    default double intensityAt(Vector point, World world)
    {
        return world.isShadowed(point) ? 0.0 : 1.0;
    }

    List<Vector> samplePoints();
}