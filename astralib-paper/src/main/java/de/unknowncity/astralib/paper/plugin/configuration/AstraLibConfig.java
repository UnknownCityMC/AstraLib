package de.unknowncity.astralib.paper.plugin.configuration;

import de.unknowncity.astralib.common.configuration.ApplicableAstraConfiguration;
import de.unknowncity.astralib.common.configuration.setting.defaults.DataBaseSetting;
import de.unknowncity.astralib.paper.plugin.configuration.settings.LanguageSetting;

public class AstraLibConfig extends ApplicableAstraConfiguration {
    private final DataBaseSetting dataBaseSetting;
    private final LanguageSetting languageSetting;

    public AstraLibConfig(DataBaseSetting dataBaseSetting, LanguageSetting languageSetting) {
        this.dataBaseSetting = dataBaseSetting;
        this.languageSetting = languageSetting;
    }

    public DataBaseSetting dataBaseSetting() {
        return dataBaseSetting;
    }

    public LanguageSetting languageSetting() {
        return languageSetting;
    }
}
