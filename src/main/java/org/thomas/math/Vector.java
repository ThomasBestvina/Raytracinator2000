package org.thomas.math;

public class Vector
{
    public double x;
    public double y;
    public double z;
    public double w;
    public Vector(double x, double y, double z, double w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    public static Vector vector3(double x, double y, double z)
    {
        return new Vector(x,y,z,0);
    }

    public static Vector point(double x, double y, double z)
    {
        return new Vector(x,y,z,1);
    }

    /*
    Returns true if it is a vector, false if it is a point.
     */
    public boolean isVector()
    {
        return DoubleMath.equal(w, 0);
    }

    /*
    Returns true if it is a point, false if it is a vector.
     */
    public boolean isPoint()
    {
        return DoubleMath.equal(w, 1);
    }

    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof Vector v)){
            return false;
        }
        return DoubleMath.equal(x,v.x) && DoubleMath.equal(y,v.y) && DoubleMath.equal(z,v.z) && DoubleMath.equal(w,v.w);
    }

    public Vector negate()
    {
        return new Vector(-x,-y,-z,-w);
    }

    public double getFromAxis(int axis)
    {
        if(axis == 0)
            return x;
        if(axis == 1)
            return y;
        if(axis == 2)
            return z;
        return w;
    }

    public Vector add(Vector v)
    {
        return new Vector(v.x + this.x, v.y + this.y, v.z + this.z, v.w + this.w);
    }
    public Vector subtract(Vector v)
    {
        return new Vector(this.x - v.x, this.y - v.y, this.z - v.z, this.w - v.w);
    }
    public Vector multiply(double s)
    {
        return new Vector(this.x*s, this.y*s, this.z*s, this.w*s);
    }
    public Vector divide(double d)
    {
        return new Vector(this.x/d, y/d, z/d, w/d);
    }

    public double magnitude()
    {
        return Math.sqrt(x*x + y*y + z*z + w*w);
    }

    public Vector normalize()
    {
        double magnitude = magnitude();
        return new Vector(x/magnitude, y/magnitude, z/magnitude, w/magnitude);
    }

    public double dot(Vector v)
    {
        return this.x * v.x + this.y * v.y + this.z * v.z  + this.w * v.w;
    }

    public Vector cross(Vector v)
    {
        return Vector.vector3(
                this.y * v.z - this.z * v.y,
                this.z * v.x - this.x * v.z,
                this.x * v.y - this.y * v.x
        );
    }

    public Vector reflect(Vector n) {
        return this.subtract(n.multiply( 2*this.dot(n)));
    }
}
