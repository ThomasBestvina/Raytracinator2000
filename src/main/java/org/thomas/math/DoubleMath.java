package org.thomas.math;

public class DoubleMath
{
    // INTERNAL_EPSILON is coarser than EPSILON so that floating-point rounding
    // accumulated during arithmetic doesn't cause equality checks to spuriously fail.
    public final static double EPSILON = 0.00001;

    private final static double INTERNAL_EPSILON = 0.001;

    public static boolean equal(double a, double b)
    {
        double c = a-b;
        return c < INTERNAL_EPSILON && c > -INTERNAL_EPSILON;
    }
}