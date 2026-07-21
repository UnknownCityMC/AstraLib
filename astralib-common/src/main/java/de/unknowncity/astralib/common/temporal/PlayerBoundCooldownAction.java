package de.unknowncity.astralib.common.temporal;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Util to allow actions to run with a cooldown
 * in between executions
 */
public class PlayerBoundCooldownAction {
    private final Map<UUID, Long> lastExecutionMap = new ConcurrentHashMap<>();
    private final Duration cooldownDuration;

    public PlayerBoundCooldownAction(Duration cooldownDuration) {
        this.cooldownDuration = cooldownDuration;
    }

    public void executeBoundToPlayer(UUID uuid, Runnable action) {
        long now = System.currentTimeMillis();
        long lastExecution = lastExecutionMap.computeIfAbsent(uuid, player -> 0L);

        if (lastExecution + cooldownDuration.toMillis() < now) {
            lastExecutionMap.put(uuid, now);
            action.run();
        }
    }

    /**
     * Removes the stored cooldown for a player, e.g. on disconnect
     */
    public void reset(UUID uuid) {
        lastExecutionMap.remove(uuid);
    }

    public void clear() {
        lastExecutionMap.clear();
    }
}
