package de.unknowncity.astralib.common.configuuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unknowncity.astralib.common.configuration.YamlAstraConfiguration;
import de.unknowncity.astralib.common.configuration.annotation.Config;
import de.unknowncity.astralib.common.configuration.setting.defaults.ModernDataBaseSetting;
import de.unknowncity.astralib.common.configuration.setting.defaults.ModernRedisSetting;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AstraLibConfigurationTest {
    private TestConfig config;

    @Test
    void testSaveLoad() {
        this.config = new TestConfig();

        config.save();

        var loadedConfig = TestConfig.loadFromFile(TestConfig.class).get();

        assertEquals(config.setting().testInt(), loadedConfig.setting().testInt());
        assertEquals(config.setting().testString(), loadedConfig.setting().testString);
        assertEquals(config.testBoolean(), loadedConfig.testBoolean());
    }


    @Test
    void testChange() {
        var changedConfig = new TestConfig();
        changedConfig.save();
        changedConfig.testBoolean(true);
        changedConfig.save();

        TestConfigDummy changedLoadedConfig = null;

        var configOpt = TestConfigDummy.loadFromFile(TestConfigDummy.class);

        changedLoadedConfig = configOpt.orElseGet(TestConfigDummy::new);

        changedLoadedConfig.save();

        assertTrue(changedLoadedConfig.testBoolean());
        assertEquals(changedLoadedConfig.testDouble(), 0.1);
        var oldSqlitePath = changedConfig.dataBaseSetting().sqliteDataBasePath().toString();
        var newSqlitePath = changedLoadedConfig.dataBaseSetting().sqliteDataBasePath().toString();

        assertEquals(oldSqlitePath, newSqlitePath);
    }
    @Config(targetFile = "D:\\Development\\Projects\\UnknownCity\\AstraLib\\astralib-paper\\src\\test\\resources\\config.yml")
    public static class TestConfig extends YamlAstraConfiguration {

        @JsonProperty
        private TestSetting setting = new TestSetting();

        @JsonProperty
        private boolean testBoolean = false;

        @JsonProperty
        private String testString = "hehehe";

        @JsonProperty
        private ModernDataBaseSetting dataBaseSetting = new ModernDataBaseSetting();

        @JsonProperty
        private ModernRedisSetting redisSetting = new ModernRedisSetting();

        public TestConfig() {

        }

        public TestSetting setting() {
            return setting;
        }

        public boolean testBoolean() {
            return testBoolean;
        }

        public void testBoolean(boolean testBoolean) {
            this.testBoolean = testBoolean;
        }

        public String testString() {
            return testString;
        }

        public ModernRedisSetting redisSetting() {
            return redisSetting;
        }

        public ModernDataBaseSetting dataBaseSetting() {
            return dataBaseSetting;
        }
    }

    @Config(targetFile = "D:\\Development\\Projects\\UnknownCity\\AstraLib\\astralib-paper\\src\\test\\resources\\config.yml")
    public static class TestConfigDummy extends YamlAstraConfiguration {

        @JsonProperty
        private TestSetting setting = new TestSetting();

        @JsonProperty
        private boolean testBoolean = false;

        @JsonProperty
        private ModernDataBaseSetting dataBaseSetting = new ModernDataBaseSetting();

        @JsonProperty
        private ModernRedisSetting redisSetting = new ModernRedisSetting();

        @JsonProperty
        private double testDouble = 0.1;

        public TestConfigDummy() {

        }

        public TestSetting setting() {
            return setting;
        }

        public boolean testBoolean() {
            return testBoolean;
        }

        public double testDouble() {
            return testDouble;
        }

        public void testBoolean(boolean testBoolean) {
            this.testBoolean = testBoolean;
        }

        public ModernRedisSetting redisSetting() {
            return redisSetting;
        }

        public ModernDataBaseSetting dataBaseSetting() {
            return dataBaseSetting;
        }
    }

    public static class TestSetting {

        @JsonProperty
        private String testString = "lol";

        @JsonProperty
        private int testInt = 1;

        public TestSetting() {

        }

        public String testString() {
            return testString;
        }

        public int testInt() {
            return testInt;
        }
    }
}