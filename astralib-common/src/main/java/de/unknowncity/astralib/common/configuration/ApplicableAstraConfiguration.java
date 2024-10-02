package de.unknowncity.astralib.common.configuration;

import java.nio.file.Path;

/**
 * @deprecated version 0.4.0 introduces a new config system based on jackson
 * Use {@link YamlAstraConfiguration} instead
 */
@Deprecated(forRemoval = true, since = "0.4.0")
public class ApplicableAstraConfiguration implements AstraConfiguration {
    private AstraConfigurationLoader configurationLoader;
    private Path path;

    public void apply(Path path) {
        this.path = path;
    }


    public Path path() {
        return path;
    }
}
