package de.unknowncity.astralib.velocity.plugin.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.configuration.annotation.Config;
import de.unknowncity.astralib.common.configuration.setting.defaults.ModernDataBaseSetting;
import de.unknowncity.astralib.common.configuration.setting.defaults.RedisSetting;
import de.unknowncity.astralib.velocity.plugin.configuration.settings.LanguageSetting;

@Config(targetFile = "astraconfig.yml")
public class AstraLibConfiguration extends YamlAstraConfiguration {
    @JsonProperty
    private ModernDataBaseSetting database = new ModernDataBaseSetting();

    @JsonProperty
    private LanguageSetting language = new LanguageSetting();

    @JsonProperty
    private RedisSetting redis = new RedisSetting();

    public AstraLibConfiguration() {

    }

    public ModernDataBaseSetting database() {
        return database;
    }

    public RedisSetting redis() {
        return redis;
    }

    public LanguageSetting language() {
        return language;
    }
}
