package de.unknowncity.astralib.paper.api.message.color;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Helper class to convert the legacy spigot color codes contained in a sting
 * while still preserving function of already existing minimessage tags
 */
public class LegacyChatColor {

    private static final Set<String> SUPPORTED_LEGACY_HEX_PATTERNS = Set.of("&x&.&.&.&.&.&.", "\\{#......}");

    /**
     * Translates old legacy spigot color codes to the modern minimessage format
     * @param legacyChar the char used to identify legacy color codes
     * @param textToTranslate a text containing legacy color codes
     * @return teh original text but with the new color format
     */
    public static String translateToMiniMessage(Character legacyChar, String textToTranslate) {
        String textToReturn = replaceDefaultHexFormat(legacyChar, replaceSpecialHexFormat(legacyChar, textToTranslate));
        var charArray = textToReturn.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (i == 0) {
                continue;
            }
            if (charArray[i-1] == legacyChar) {
                var chatColor = ChatColor.getByChar(charArray[i]);
                String tag = "";
                if (chatColor == null)
                    continue;
                switch (chatColor) {
                    case BOLD -> tag = "b";
                    case MAGIC -> tag = "obf";
                    case STRIKETHROUGH -> tag = "st";
                    case ITALIC -> tag = "i";
                    case UNDERLINE -> tag = "u";
                    default -> tag = chatColor.name().toLowerCase();
                }
                var finalTag = "<" + tag + ">";
                textToReturn = textToReturn.replace("" + legacyChar + charArray[i], finalTag);
            }
        }
        return textToReturn;
    }

    public static String replaceDefaultHexFormat(char legacyChar, String input) {
        var output = input;
        var oldHexFormat = "&x&.&.&.&.&.&.";
        var matcher = Pattern.compile(oldHexFormat).matcher(output);
        String newHexFormat;
        while (matcher.find()) {
            newHexFormat = "<color:#......>";
            var currentOldHexFormat = matcher.group();
            var hexChars = currentOldHexFormat.replace(String.valueOf(legacyChar), "").substring(1);
            newHexFormat = newHexFormat.replace("......", hexChars);
            output = output.replace(currentOldHexFormat, newHexFormat);
        }
        return output;
    }

    public static String replaceSpecialHexFormat(char legacyChar, String input) {
        var output = input;
        var oldHexFormat = "\\{#......}";
        var matcher = Pattern.compile(oldHexFormat).matcher(output);
        String newHexFormat;
        while (matcher.find()) {
            newHexFormat = "<color:#......>";
            var currentOldHexFormat = matcher.group();
            var hexChars = currentOldHexFormat.substring(2, 8);
            newHexFormat = newHexFormat.replace("......", hexChars);
            output = output.replace(currentOldHexFormat, newHexFormat);
        }
        return output;
    }

    public static String translateToRawMessage(Character legacyChar, String textToTranslate) {
        String textToReturn = removeDefaultHexFormat(legacyChar, removeSpecialHexFormat(legacyChar, textToTranslate));
        var charArray = textToReturn.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (i == 0) {
                continue;
            }
            if (charArray[i-1] == legacyChar) {
                textToReturn = textToReturn.replace("" + legacyChar + charArray[i], "");
            }
        }
        return textToReturn;
    }

    public static String removeDefaultHexFormat(char legacyChar, String input) {
        var output = input;
        var oldHexFormat = "&x&.&.&.&.&.&.";
        var matcher = Pattern.compile(oldHexFormat).matcher(output);
        String newHexFormat;
        while (matcher.find()) {
            newHexFormat = "";
            var currentOldHexFormat = matcher.group();
            output = output.replace(currentOldHexFormat, newHexFormat);
        }
        return output;
    }

    public static String removeSpecialHexFormat(char legacyChar, String input) {
        var output = input;
        var oldHexFormat = "\\{#......}";
        var matcher = Pattern.compile(oldHexFormat).matcher(output);
        String newHexFormat;
        while (matcher.find()) {
            newHexFormat = "";
            var currentOldHexFormat = matcher.group();
            output = output.replace(currentOldHexFormat, newHexFormat);
        }
        return output;
    }
}
