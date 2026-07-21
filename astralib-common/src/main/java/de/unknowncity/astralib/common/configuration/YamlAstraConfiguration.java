package de.unknowncity.astralib.common.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.gson.Gson;
import de.unknowncity.astralib.common.configuration.annotation.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class YamlAstraConfiguration {
    private Path pluginDataPath;

    public void pluginDataPath(Path pluginDataPath) {
        this.pluginDataPath = pluginDataPath;
    }

    public static <T extends YamlAstraConfiguration> Optional<T> loadFromFile(Class<T> configurationClass, Path pluginDataPath) {
        var configPath = pluginDataPath.resolve(Path.of(configurationClass.getDeclaredAnnotation(Config.class).targetFile()));
        Logger.getLogger("Configuration").log(Level.INFO, "Loading Configuration from " + configPath);

        if (!Files.exists(configPath)) {
            return Optional.empty();
        }

        var objectMapper = new YAMLMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.findAndRegisterModules();

        try {
            if (Files.readAllBytes(configPath).length == 0) {
                return Optional.empty();
            }
            var config = objectMapper.readValue(configPath.toFile(), configurationClass);
            config.pluginDataPath(pluginDataPath);
            return Optional.of(config);
        } catch (IOException e) {
            Logger.getLogger("Configuration").log(Level.SEVERE, "Error while reading configuration file", e);
            backupBrokenConfig(configPath);
            return Optional.empty();
        }
    }

    public static <T extends YamlAstraConfiguration> Optional<T> loadFromFile(Class<T> configurationClass) {
        var configPath = Path.of(configurationClass.getDeclaredAnnotation(Config.class).targetFile());
        Logger.getLogger("Configuration").log(Level.INFO, "Loading Configuration from " + configPath);

        if (!Files.exists(configPath)) {
            return Optional.empty();
        }

        var objectMapper = new YAMLMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.findAndRegisterModules();

        try {
            if (Files.readAllBytes(configPath).length == 0) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(configPath.toFile(), configurationClass));
        } catch (IOException e) {
            Logger.getLogger("Configuration").log(Level.SEVERE, "Error while reading configuration file", e);
            backupBrokenConfig(configPath);
            return Optional.empty();
        }
    }

    /**
     * Copies an unreadable configuration file to a ".broken" sibling so that user edits
     * are not lost when the configuration is rewritten with defaults afterwards
     * @param configPath the path of the unreadable configuration file
     */
    private static void backupBrokenConfig(Path configPath) {
        var backupPath = configPath.resolveSibling(configPath.getFileName() + ".broken");
        try {
            Files.copy(configPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            Logger.getLogger("Configuration").log(Level.WARNING, "Backed up broken configuration to " + backupPath);
        } catch (IOException e) {
            Logger.getLogger("Configuration").log(Level.SEVERE, "Failed to back up broken configuration file", e);
        }
    }


    public void save() {
        var configPath = pluginDataPath == null ? targetFile() : pluginDataPath.resolve(targetFile());
        Logger.getLogger("Configuration").log(Level.INFO, "Saving configuration to " + configPath);

        if (!Files.exists(configPath)) {
            try {
                if (!Files.exists(configPath.getParent())) {
                    Files.createDirectories(configPath.getParent());
                }
                Files.createFile(configPath);
            } catch (IOException e) {
                Logger.getLogger("Configuration").log(Level.SEVERE, "Error while saving configuration file", e);
            }
        }

        var objectMapper = new YAMLMapper().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.findAndRegisterModules();

        try {
            objectMapper.writeValue(configPath.toFile(), this);
        } catch (IOException e) {
            Logger.getLogger("Configuration").log(Level.SEVERE, "Error while saving configuration file", e);
        }
    }

    public Path targetFile() {
        return Path.of(this.getClass().getDeclaredAnnotation(Config.class).targetFile());
    }
}
