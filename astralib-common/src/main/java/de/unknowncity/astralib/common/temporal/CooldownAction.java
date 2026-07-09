package de.unknowncity.astralib.common.temporal;

import java.time.Duration;

/**
 * Util to allow actions to run with a cooldown
 * in between executions
 */
public class CooldownAction {
    private volatile long lastExecution = 0;
    private final Duration cooldownDuration;

    public CooldownAction(Duration cooldownDuration) {
        this.cooldownDuration = cooldownDuration;
    }

    public void execute(Runnable action) {
        long now = System.currentTimeMillis();
        if (lastExecution + cooldownDuration.toMillis() < now) {
            lastExecution = now;
            action.run();
        }
    }
}
