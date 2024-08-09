package de.unknowncity.astralib.common.service;

import de.unknowncity.astralib.common.registry.Registry;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ServiceRegistry<I> extends Registry<I, Service<I>> {

    public ServiceRegistry(I plugin) {
        super(plugin);
    }
}
