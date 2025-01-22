package de.unknowncity.astralib.common.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A utility class for sorting maps
 */
public class MapSorting {
    /**
     * Sorts a map according to the values, from highest to lowest (descending)
     * @param map the map to sort by its values
     * @return a new sorted map
     * @param <K> the type of the key
     * @param <V> the type of the value
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(Map<K, V> map) {
        var list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Sorts a map according to the values, from lowest to highest (ascending)
     * @param map the map to sort by its values
     * @return a new sorted map
     * @param <K> the type of the key
     * @param <V> the type of the value
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueAsc(Map<K, V> map) {
        var list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.naturalOrder()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}