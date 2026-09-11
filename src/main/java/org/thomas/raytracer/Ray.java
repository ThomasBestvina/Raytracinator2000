package org.thomas.raytracer;

import org.thomas.math.Matrix;
import org.thomas.math.Vector;

public class Ray {
    private final Vector origin;
    private final Vector direction;

    /**
     *
     * @param from Origin of ray
     * @param to Direction of ray.
     */
    public Ray(Vector from, Vector to)
    {
        origin = from;
        direction = to;
    }

    public Vector getOrigin()
    {
        return origin;
    }
    public Vector getDirection()
    {
        return direction;
    }

    public Vector position(double p)
    {
        return origin.add(direction.multiply(p));
    }

    public Ray transform(Matrix transform)
    {
        return new Ray(transform.multiply(origin), transform.multiply(direction));
    }
}