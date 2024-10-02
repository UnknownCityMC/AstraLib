package de.unknowncity.astralib.paper.plugin.configuration.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unknowncity.astralib.common.message.lang.Language;

public class LanguageSetting  {

        @JsonProperty
        private Language defaultLanguage = Language.ENGLISH;

        @JsonProperty
        private boolean allowChange = true;

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

