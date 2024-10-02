package de.unknowncity.astralib.common.configuration.setting.serializer;

import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.configuration.setting.defaults.DataBaseSetting;
import de.unknowncity.astralib.common.database.DataBaseDriver;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @deprecated version 0.4.0 introduces a new config system based on jackson
 * Use {@link YamlAstraConfiguration} instead
 */
@Deprecated(forRemoval = true, since = "0.4.0")
public class DatabaseSettingSerializer implements TypeSerializer<DataBaseSetting> {
    @Override
    public DataBaseSetting deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var driver = DataBaseDriver.valueOf(node.node("driver").getString());
        var host = node.node("host").getString();
        var port = node.node("port").getInt();
        var database = node.node("database").getString();
        var username = node.node("username").getString();
        var password = node.node("password").getString();
        var maxPoolSize = node.node("connections", "max-pool-size").getInt();
        var minIdleConnections = node.node("connections", "minIdleConnections").getInt();
        var sqlitePath = Path.of(Objects.requireNonNull(node.node("sqlite-path").getString()));
        return new DataBaseSetting(
           driver, host, port, database, username, password, maxPoolSize, minIdleConnections, sqlitePath
        );
    }

    @Override
    public void serialize(Type type, @Nullable DataBaseSetting setting, ConfigurationNode node) throws SerializationException {
        if (setting == null) {
            return;
        }
        node.node("driver").set(setting.dataBaseDriver().name());
        node.node("host").set(setting.host());
        node.node("port").set(setting.port());
        node.node("database").set(setting.database());
        node.node("username").set(setting.userName());
        node.node("password").set(setting.password());
        node.node("connections", "max-pool-size").set(setting.maxPoolSize());
        node.node("connections", "min-idle").set(setting.minIdleConnections());
        node.node("sqlite-path").set(setting.sqliteDataBasePath().toString());
    }
}
