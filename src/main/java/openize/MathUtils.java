/*
 * Openize.HEIC
 * Copyright (c) 2024-2026 Openize Pty Ltd.
 *
 * This file is part of Openize.HEIC.
 *
 * Openize.HEIC is available under Openize license, which is
 * available along with Openize.HEIC sources.
 */

package openize;

/**
 * <p>
 *     Provides additional mathematics methods.
 * </p>
 */
public final class MathUtils
{
    /**
     * <p>
     *  Returns the natural logarithm (base {@code newBase}) of a double value.
     * </p>
     * @param value a value
     * @param newBase a base
     * @return the value of ln(value), the natural logarithm of value.
     */
    public static double log(double value, double newBase)
    {
        if (newBase == 1.0)
        {
            return Double.NaN;
        }
        else
        {
            double result = Math.log(value) / Math.log(newBase);
            return result == 0.0 ? 0.0 : result;
        }
    }

    /**
     * Returns the smallest (closest to negative infinity)
     * {@code double} value that is greater than or equal to the
     * argument and is equal to a mathematical integer.
     *
     *
     * @param   value  a value.
     * @return  the smallest (closest to negative infinity)
     *          floating-point value that is greater than or equal to
     *          the argument and is equal to a mathematical integer.
     */
    public static double ceiling(double value)
    {
        if (!Double.isInfinite(value) && !Double.isNaN(value))
        {
            double result = Math.floor(value);
            if (result != value)
            {
                ++result;
            }

            return result;
        }
        else
        {
            return value;
        }
    }

    /**
     * <p>
     *     Converts the double value to int.
     * </p>
     * @param doubleValue The double value.
     * @return The int value.
     */
    public static int f64_s32(double doubleValue)
    {
        return (doubleValue == doubleValue
                && !(doubleValue < -2.147483647E9)
                && !(doubleValue > 2.147483647E9))
                ? (int) doubleValue
                : Integer.MIN_VALUE;
    }
}
