package de.unknowncity.astralib.paper.plugin.configuration.serializer;

import de.unknowncity.astralib.common.configuration.setting.defaults.DataBaseSetting;
import de.unknowncity.astralib.paper.plugin.configuration.AstraLibConfig;
import de.unknowncity.astralib.paper.plugin.configuration.settings.LanguageSetting;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class AstraLibConfigSerializer implements TypeSerializer<AstraLibConfig> {
    @Override
    public AstraLibConfig deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var databaseSettings = node.node("database").get(DataBaseSetting.class);
        var languageSettings = node.node("language").get(LanguageSetting.class);
        return new AstraLibConfig(databaseSettings, languageSettings);
    }

    @Override
    public void serialize(Type type, @Nullable AstraLibConfig config, ConfigurationNode node) throws SerializationException {
        if (config == null) {
            return;
        }

        node.node("database").set(config.dataBaseSetting());
        node.node("language").set(config.languageSetting());
    }
}
