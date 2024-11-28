package de.unknowncity.astralib.common.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceUtils {

    public static void saveDefaultResource(String from, Path to, Path dataPath, ClassLoader classLoader, Logger logger) {
        if (Files.exists(dataPath.resolve(to))) {
            return;
        }

        try (var resourceAsStream = classLoader.getResourceAsStream(from)) {
            if (resourceAsStream == null) {
                logger.log(
                        Level.SEVERE, "Failed to save " + from + ". " +
                                "The plugin developer tried to save a file that does not exist in the plugins jar file!"
                );
                return;
            }
            Files.createDirectories(dataPath.resolve(to.getParent()));
            try (var outputStream = Files.newOutputStream(dataPath.resolve(to))) {
                resourceAsStream.transferTo(outputStream);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save " + from, e);
        }
    }
}
