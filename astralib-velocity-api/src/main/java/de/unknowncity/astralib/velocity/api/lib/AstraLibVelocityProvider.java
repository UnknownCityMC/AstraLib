package de.unknowncity.astralib.velocity.api.lib;

/**
 * Velocity has no services manager like Bukkit, so the AstraLib velocity plugin
 * registers its {@link AstraLibVelocity} instance here for other plugins to consume.
 */
public final class AstraLibVelocityProvider {
    private static AstraLibVelocity astraLib;

    private AstraLibVelocityProvider() {

    }

    public static void register(AstraLibVelocity astraLibVelocity) {
        astraLib = astraLibVelocity;
    }

    public static boolean isAvailable() {
        return astraLib != null;
    }

    public static AstraLibVelocity get() {
        if (astraLib == null) {
            throw new IllegalStateException("AstraLib velocity has not been initialized yet!");
        }
        return astraLib;
    }
}
