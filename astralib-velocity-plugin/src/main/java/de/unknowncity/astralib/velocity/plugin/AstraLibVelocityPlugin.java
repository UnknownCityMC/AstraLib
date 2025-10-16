package de.unknowncity.astralib.velocity.plugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import de.unknowncity.astralib.common.platform.AstraLib;
import de.unknowncity.astralib.common.redis.RedisService;
import de.unknowncity.astralib.common.redis.RedisUriBuilder;
import de.unknowncity.astralib.velocity.paltform.VelocityAstraPlatform;
import de.unknowncity.astralib.velocity.plugin.configuration.AstraLibConfiguration;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "astralib", name = "AstraLib", version = "0.1.0-SNAPSHOT", authors = {"TheZexquex"})
public class AstraLibVelocityPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private AstraLibConfiguration configuration;

    @Inject
    public AstraLibVelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        initializeConfiguration();

        var platform = new VelocityAstraPlatform(this, server);
        var redisService = new RedisService(RedisUriBuilder.build(configuration.redis()));

        AstraLib.initialize(
                platform,
                redisService
        );
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        AstraLib.getRedis().shutdown();
        AstraLib.getRedis().asyncCommands().
    }

    private void initializeConfiguration() {
        var configOpt = AstraLibConfiguration.loadFromFile(AstraLibConfiguration.class, dataDirectory);

        this.configuration = configOpt.orElseGet(AstraLibConfiguration::new);
        this.configuration.save();
    }

    public Logger logger() {
        return logger;
    }
}