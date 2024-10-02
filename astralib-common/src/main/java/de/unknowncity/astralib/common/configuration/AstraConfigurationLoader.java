package de.unknowncity.astralib.common.configuration;

import de.unknowncity.astralib.common.configuration.setting.defaults.DataBaseSetting;
import de.unknowncity.astralib.common.configuration.setting.defaults.RedisSetting;
import de.unknowncity.astralib.common.configuration.setting.serializer.DatabaseSettingSerializer;
import de.unknowncity.astralib.common.configuration.setting.serializer.RedisSettingSerializer;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @deprecated version 0.4.0 introduces a new config system based on jackson
 * Use {@link YamlAstraConfiguration} instead
 */
@Deprecated(forRemoval = true, since = "0.4.0")
public class AstraConfigurationLoader {
    private final Logger logger;;

    public AstraConfigurationLoader(Logger logger) {
        this.logger = logger;
    }

    public <T extends ApplicableAstraConfiguration> void saveDefaultConfig(T astraConfiguration, Path path, Consumer<TypeSerializerCollection.Builder> typeSerializerBuilder) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                astraConfiguration.apply(path);
                saveConfig(astraConfiguration, typeSerializerBuilder);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to save default configuration", e);
            }
        }
    }

    public final <T extends ApplicableAstraConfiguration> void saveConfig(T astraConfiguration, Consumer<TypeSerializerCollection.Builder> typeSerializerBuilder) {
        var loader = buildLoader(astraConfiguration.path(), typeSerializerBuilder);

        var node = loader.createNode();

        try {
            node.set(astraConfiguration);
            loader.save(node);
        } catch (ConfigurateException e) {
            logger.log(Level.SEVERE, "Failed to save default configuration", e);
        }
    }


    public final <T extends ApplicableAstraConfiguration> Optional<T> loadConfiguration(Path path, Class<T> configClass, Consumer<TypeSerializerCollection.Builder> typeSerializerBuilder) {
        var loader = buildLoader(path, typeSerializerBuilder);

        try {
            var astraConfig = loader.load().get(configClass);
            if (astraConfig == null) {
                return Optional.empty();
            }
            return Optional.of(astraConfig);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load configuration: " + path, e);
        }
        return Optional.empty();
    }

    public void saveDefaultConfigFile(Path from, Path to) {
        if (Files.exists(to)) {
            return;
        }

        try (var resourceAsStream = getClass().getResourceAsStream("/" + from.toString())) {
            if (resourceAsStream == null) {
                logger.log(
                        Level.SEVERE, "Failed to save " + from + ". " +
                                "The plugin developer tried to save a file that does not exist in the plugins jar file!"
                );
                return;
            }
            Files.createDirectories(to.getParent());
            try (var outputStream = Files.newOutputStream(to)) {
                resourceAsStream.transferTo(outputStream);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save " + from, e);
        }
    }

    private YamlConfigurationLoader buildLoader(Path path, Consumer<TypeSerializerCollection.Builder> typeSerializerBuilder) {
        var loaderBuilder = YamlConfigurationLoader.builder().path(path);
        loaderBuilder.nodeStyle(NodeStyle.BLOCK);
        loaderBuilder.defaultOptions(opts ->
                opts.serializers(build -> {
                    typeSerializerBuilder.accept(build);
                    build.register(DataBaseSetting.class, new DatabaseSettingSerializer());
                    build.register(RedisSetting.class, new RedisSettingSerializer());
                })
        );
        return loaderBuilder.build();
    }
}
