package de.unknowncity.astralib.common.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import de.unknowncity.astralib.common.configuration.annotation.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class YamlAstraConfiguration {

    public static <T extends YamlAstraConfiguration> Optional<T> loadFromFile(Class<T> configurationClass) {
        var configPath = Path.of(configurationClass.getDeclaredAnnotation(Config.class).targetFile()).toFile();
        Logger.getLogger("Configuration").log(Level.INFO, "Loading Configuration from " + configPath);

        if (!configPath.exists()) {
            return Optional.empty();
        }

        var objectMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.findAndRegisterModules();


        try {
            return Optional.of(objectMapper.readValue(configPath, configurationClass));
        } catch (IOException e) {
            Logger.getLogger("Configuration").log(Level.SEVERE, "Error while reading configuration file", e);
            return Optional.empty();
        }
    }

    public void save() {
        var configPath = targetFile();
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

        var objectMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
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
