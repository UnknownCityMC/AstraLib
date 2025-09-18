package de.unknowncity.astralib.common.util;

import java.time.Duration;

/**
 * A utility class for formatting a {@link Duration}
 */
public class DurationFormatter {

    /**
     * Formats a duration with a given format, removing 0d, 0h, 0m or 0s
     * @param format the format to format the duration to
     * @param duration the duration to format
     * @return a string with formatted duration
     */
    public static String formatDuration(String format, Duration duration) {
        String formattedString = String.format(
                format,
                duration.toDaysPart(),
                duration.toHoursPart(),
                duration.toMinutesPart(),
                duration.toSecondsPart()
        );

        formattedString = formattedString.replaceAll("\\b0+[dhms]\\b", "").replaceAll("\\s+", " ").trim();
        return formattedString;
    }

    /**
     * Formats a duration using the default format
     * "%dd %dh %dm %ds"
     * for formatting, removing 0d, 0h, 0m or 0s
     * @param duration the duration to format
     * @return a string with formatted duration
     */
    public static String formatDuration(Duration duration) {
        String formattedString = String.format(
                "%dd %dh %dm %ds",
                duration.toDaysPart(),
                duration.toHoursPart(),
                duration.toMinutesPart(),
                duration.toSecondsPart()
        );

        formattedString = formattedString.replaceAll("\\b0+[dhms]\\b", "").replaceAll("\\s+", " ").trim();
        return formattedString;
    }
}