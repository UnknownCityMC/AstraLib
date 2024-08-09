package de.unknowncity.astralib.paper.plugin.configuration.serializer;

import de.unknowncity.astralib.common.message.lang.Language;
import de.unknowncity.astralib.paper.plugin.configuration.settings.LanguageSetting;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class LanguageSettingSerializer implements TypeSerializer<LanguageSetting> {
    @Override
    public LanguageSetting deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var defaultLanguage = new Language(node.node("default").getString());
        var changeable = node.node("changeable").getBoolean();

        return new LanguageSetting(defaultLanguage, changeable);
    }

    @Override
    public void serialize(Type type, @Nullable LanguageSetting setting, ConfigurationNode node) throws SerializationException {
        if (setting == null) {
            return;
        }

        node.node("default").set(setting.defaultLanguage().langIdentifier());
        node.node("changeable").set(setting.allowChange());
    }
}
