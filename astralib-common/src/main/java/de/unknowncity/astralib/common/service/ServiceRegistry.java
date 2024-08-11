package de.unknowncity.astralib.common.service;

import de.unknowncity.astralib.common.registry.Registry;

public class ServiceRegistry<I> extends Registry<I, Service<I>> {

    public ServiceRegistry(I plugin) {
        super(plugin);
    }
}
