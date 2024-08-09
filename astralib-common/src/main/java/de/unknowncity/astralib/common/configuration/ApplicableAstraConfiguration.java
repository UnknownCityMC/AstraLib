package de.unknowncity.astralib.common.configuration;

import java.nio.file.Path;
import java.util.Map;

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
