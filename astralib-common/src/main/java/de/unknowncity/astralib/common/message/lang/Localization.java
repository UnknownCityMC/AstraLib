package de.unknowncity.astralib.common.message.lang;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Localization {
    private final Path languageFileFolder;
    private final Map<Language, ConfigurationNode> languageConfigurationNodes;

    public Localization(Path languageFileFolder) {
        this.languageConfigurationNodes = new HashMap<>();
        this.languageFileFolder = languageFileFolder;
    }

    public static Builder builder(Path languageFileFolder) {
        return new Builder(languageFileFolder);
    }

    public static class Builder {
        private final Path languagePath;
        private Logger logger;

        public Builder(Path languageFileFolder) {
            this.languagePath = languageFileFolder;
        }

        public Builder withLogger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public Localization buildAndLoad() {
            var localization = new Localization(languagePath);
            localization.loadLanguageFiles(logger);
            return localization;
        }
    }

    private void loadLanguageFiles(Logger logger) {
        var fileNames = languageFileFolder.toFile().list();

        if (fileNames == null) {
            if (logger == null) {
                return;
            }
            logger.warning("No language files found!");
            return;
        }

        Arrays.stream(fileNames).filter(fileName -> fileName.endsWith(".yml")).forEach(fileName -> {
            if (logger != null) {
                logger.info("Found language file: '" + fileName + "'");
            }

            loadLanguageFile(logger, fileName);
        });
    }

    private void loadLanguageFile(Logger logger, String fileName) {
        var path = languageFileFolder.resolve(fileName);
        var yamlConfigurationLoader = YamlConfigurationLoader.builder().path(path).build();

        try {
            var configurationNode = yamlConfigurationLoader.load();
            var language = new Language(fileName.replace(".yml", ""));
            languageConfigurationNodes.put(language, configurationNode);
        } catch (ConfigurateException e) {
            if (logger != null) {
                logger.log(Level.WARNING, "Faild to load language file: '" + fileName + "'", e);
            }
        }
    }

    public ConfigurationNode langNode(Language language) {
        return languageConfigurationNodes.get(language);
    }

    public boolean isValidLanguage(String string) {
        return languageConfigurationNodes.keySet().stream().anyMatch(language -> language.langIdentifier().equals(string));
    }

    public Map<Language, ConfigurationNode> languageConfigurationNodes() {
        return languageConfigurationNodes;
    }

    public List<String> langIdentifiers() {
        return languageConfigurationNodes.keySet().stream().map(Language::langIdentifier).toList();
    }
}
