package de.unknowncity.astralib.common.command;

import de.unknowncity.astralib.common.message.AbstractMessenger;
import net.kyori.adventure.audience.Audience;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.spongepowered.configurate.NodePath;

import java.util.Map;
import java.util.function.Function;

public final class SimpleCloudCaptionRegistry {
    private static final Map<Caption, String> ARGUMENT_PARSE_CAPTIONS = Map.of(
            StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_BOOLEAN, "boolean",
            StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_CHAR, "char",
            StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_COLOR, "color",
            StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_DURATION, "duration",
            StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM, "enum",
            StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NUMBER, "number",
            StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_REGEX, "regex",
            StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_STRING, "string",
            StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_UUID, "uuid"
    );

    private SimpleCloudCaptionRegistry() {
    }

    /**
     * Registers messenger backed caption providers for clouds standard
     * argument-parse failure captions. Lang path: exception.argument-parse.[type]
     * @param commandManager the command manager whose caption registry gets populated
     * @param messenger the messenger resolving the lang strings
     * @param playerMapper maps a command sender to a platform player, returning null for non-players
     */
    public static <C, P extends Audience> void registerDefaultCaptions(
            CommandManager<C> commandManager,
            AbstractMessenger<P> messenger,
            Function<C, P> playerMapper
    ) {
        ARGUMENT_PARSE_CAPTIONS.forEach((caption, type) -> commandManager.captionRegistry().registerProvider(
                CaptionProvider.forCaption(caption, sender -> messenger.getNullableString(
                        playerMapper.apply(sender), NodePath.path("exception", "argument-parse", type)))
        ));
    }
}
