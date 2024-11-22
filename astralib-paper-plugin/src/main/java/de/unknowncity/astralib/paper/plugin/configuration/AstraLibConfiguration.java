package de.unknowncity.astralib.paper.plugin.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.configuration.annotation.Config;
import de.unknowncity.astralib.common.configuration.setting.defaults.ModernDataBaseSetting;
import de.unknowncity.astralib.paper.plugin.configuration.settings.LanguageSetting;

@Config(targetFile = "plugins/AstraLib/astraconfig.yml")
public class AstraLibConfiguration extends YamlAstraConfiguration {

    @JsonProperty
    private ModernDataBaseSetting dataBaseSetting = new ModernDataBaseSetting();

    @JsonProperty
    private LanguageSetting languageSetting = new LanguageSetting();

    public AstraLibConfiguration() {

    }

    public ModernDataBaseSetting dataBaseSetting() {
        return dataBaseSetting;
    }

    public LanguageSetting languageSetting() {
        return languageSetting;
    }
}
