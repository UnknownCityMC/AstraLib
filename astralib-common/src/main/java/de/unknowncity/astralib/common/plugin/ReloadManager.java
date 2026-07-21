package de.unknowncity.astralib.common.plugin;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry of all plugins that can be reloaded via /astralib reload [plugin].
 * Plugins building on the AstraLib base classes are registered automatically
 * with their {@link AstraPlugin#onPluginReload()} as reload action.
 */
public final class ReloadManager {
    private static final Map<String, Runnable> RELOADABLES = new ConcurrentHashMap<>();

    private ReloadManager() {

    }

    public static void register(String pluginName, Runnable reloadAction) {
        RELOADABLES.put(pluginName, reloadAction);
    }

    public static void unregister(String pluginName) {
        RELOADABLES.remove(pluginName);
    }

    public static boolean isRegistered(String pluginName) {
        return RELOADABLES.containsKey(pluginName);
    }

    public static Set<String> registeredPluginNames() {
        return Collections.unmodifiableSet(RELOADABLES.keySet());
    }

    /**
     * Runs the reload action of a registered plugin
     * @return false if no plugin with that name is registered
     */
    public static boolean reload(String pluginName) {
        var reloadAction = RELOADABLES.get(pluginName);
        if (reloadAction == null) {
            return false;
        }
        reloadAction.run();
        return true;
    }
}
