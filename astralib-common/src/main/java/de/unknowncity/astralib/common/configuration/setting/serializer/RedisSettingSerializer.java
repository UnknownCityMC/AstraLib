package de.unknowncity.astralib.common.configuration.setting.serializer;

import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.configuration.setting.defaults.RedisSetting;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * @deprecated version 0.4.0 introduces a new config system based on jackson
 * Use {@link YamlAstraConfiguration} instead
 */
@Deprecated(forRemoval = true, since = "0.4.0")
public class RedisSettingSerializer implements TypeSerializer<RedisSetting> {
    @Override
    public RedisSetting deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var host = node.node("host").getString();
        var port = node.node("port").getInt();
        var password = node.node("password").getString();
        return new RedisSetting(host, port, password);
    }

    @Override
    public void serialize(Type type, @Nullable RedisSetting setting, ConfigurationNode node) throws SerializationException {
        if (setting == null) {
            return;
        }
        node.node("host").set(setting.host());
        node.node("port").set(setting.port());
        node.node("password").set(setting.password());
    }
}
