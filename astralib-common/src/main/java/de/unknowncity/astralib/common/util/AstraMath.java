package de.unknowncity.astralib.common.util;

public class AstraMath {

    public static boolean isBetween(double value, double boundOne, double boundTwo) {
        return boundOne < boundTwo ? (boundOne <= value && value <= boundTwo) : (boundTwo <= value && value <= boundOne);
    }
}
