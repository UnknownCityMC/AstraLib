package de.unknowncity.astralib.paper.plugin.configuration.settings;

import de.unknowncity.astralib.common.configuration.setting.Setting;
import de.unknowncity.astralib.common.message.lang.Language;

public class LanguageSetting implements Setting {

        private Language defaultLanguage = Language.ENGLISH;

        private boolean allowChange = true;

        public LanguageSetting(Language defaultLanguage, boolean allowChange) {
                this.defaultLanguage = defaultLanguage;
                this.allowChange = allowChange;
        }

        public LanguageSetting() {

        }

        public Language defaultLanguage() {
                return defaultLanguage;
        }

        public void setDefaultLanguage(Language defaultLanguage) {
                this.defaultLanguage = defaultLanguage;
        }

        public boolean allowChange() {
                return allowChange;
        }

        public void setAllowChange(boolean allowChange) {
                this.allowChange = allowChange;
        }
}

