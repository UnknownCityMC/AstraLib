package de.unknowncity.astralib.common.registry;

import de.unknowncity.astralib.common.registry.registrable.ClosableRegistrable;
import de.unknowncity.astralib.common.registry.registrable.Registrable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Registry<I, T extends Registrable<I>> {
    protected final Set<Registrable<I>> registered;
    protected final I plugin;

    public Registry(I plugin) {
        this.registered = new HashSet<>();
        this.plugin = plugin;
    }

    /**
     * Register a new registrable
     * @param registrable an instance of a new registrable
     */
    public void register(T registrable) {
        this.registered.add(registrable);
    }

    /**
     * Unregisters a registrable
     * @param registrableClass The registrable to unregister
     */
    public void unregister(Class<? extends T> registrableClass) {
        var optionalRegistrable = this.registered
                .stream()
                .filter(potential -> potential.getClass().equals(registrableClass))
                .findFirst();
        if (optionalRegistrable.isEmpty()) {
            return;
        }
        var registrable = optionalRegistrable.get();
        if (registrable instanceof ClosableRegistrable<I> closable) {
            closable.shutdown();
        }
        this.registered.remove(registrable);
    }

    /**
     * Gets a registrable if present
     * @param registrableClass the type of the registrable
     * @return the registrable if present, else empty
     */
    public <R extends T> R getRegistered(Class<R> registrableClass) {
        var possibleSetting = registered.stream().filter(registrableClass::isInstance).findFirst();

        return (possibleSetting.map(registrableClass::cast).orElse(null));
    }

    /**
     * Gets all registered entries
     * @return a set with all registered entries
     */
    public Set<Registrable<I>> getAllRegistered() {
        return Collections.unmodifiableSet(registered);
    }
}
