package de.unknowncity.astralib.common.message;

import de.unknowncity.astralib.common.util.DurationFormatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.time.Duration;

/**
 * A utility class for easy access to MiniMessage placeholders for
 * often used Java classes, like duration
 */
public class CommonPlaceholders {

    /**
     * Gets a collection of placeholders for a {@link Duration}
     * Possible placeholders:
     * <days>, <hours>, <minutes>, <seconds>, <millis>, <duration> (formatted using {@link DurationFormatter})
     * @param duration the duration to get the placeholders for
     * @return the placeholders for the duration
     */
    public static TagResolver[] duration(Duration duration) {
        var days = duration.toHours() == 0 ? "" : String.valueOf(duration.toDaysPart());
        var hours = duration.toHours() == 0 ? "" : String.valueOf(duration.toHoursPart());
        var minutes = duration.toMinutesPart() == 0 ? "" : String.valueOf(duration.toMinutesPart());
        var seconds = duration.toSecondsPart() == 0 ? "" : String.valueOf(duration.toSecondsPart());
        var millis = duration.toSecondsPart() == 0 ? "" : String.valueOf(duration.toMillisPart());

        return new TagResolver[]{
                Placeholder.parsed("days", days),
                Placeholder.parsed("hours", hours),
                Placeholder.parsed("minutes", minutes),
                Placeholder.parsed("seconds", seconds),
                Placeholder.parsed("millis", millis),
                Placeholder.parsed("duration", DurationFormatter.formatDuration(duration)),
        };
    }
}