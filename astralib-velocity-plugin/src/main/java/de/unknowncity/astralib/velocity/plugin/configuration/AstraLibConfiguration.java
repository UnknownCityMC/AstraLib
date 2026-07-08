package de.unknowncity.astralib.velocity.plugin.configuration;

import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.configuration.annotation.Config;
import de.unknowncity.astralib.common.configuration.setting.defaults.ModernDataBaseSetting;
import de.unknowncity.astralib.common.configuration.setting.defaults.RedisSetting;

@Config(targetFile = "astraconfig.yml")
public class AstraLibConfiguration extends YamlAstraConfiguration {

    private ModernDataBaseSetting database = new ModernDataBaseSetting();

    private RedisSetting redis = new RedisSetting();

    public AstraLibConfiguration() {

    }

    public ModernDataBaseSetting database() {
        return database;
    }

    public RedisSetting redis() {
        return redis;
    }
}
