package de.unknowncity.astralib.common.temporal;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Util to allow actions to run with a cooldown
 * in between executions
 */
public class PlayerBoundCooldownAction {
    private final Map<UUID, Long> lastExecutionMap = new HashMap<>();
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
}
