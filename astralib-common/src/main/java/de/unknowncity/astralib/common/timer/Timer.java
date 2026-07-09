package de.unknowncity.astralib.common.timer;

import de.unknowncity.astralib.common.timer.aborttrigger.AbortTrigger;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class Timer {
    protected ScheduledExecutorService executorService;
    // Mutated from caller threads, read inside the scheduler thread
    protected volatile boolean running;
    protected volatile boolean paused;
    protected final Runnable runOnFinish;
    protected final Consumer<Duration> runOnStep;
    protected final TimeUnit timeUnit;
    protected final Set<AbortTrigger> abortTriggers;

    public Timer(
            TimeUnit timeUnit,
            Runnable runOnFinish,
            Consumer<Duration> runOnStep,
            Set<AbortTrigger> abortTriggers
    ) {
        this.timeUnit = timeUnit;
        this.runOnFinish = runOnFinish;
        this.runOnStep = runOnStep;
        this.abortTriggers = abortTriggers;
    }

    public void setPaused(boolean pause) {
        paused = pause;
    }

    public void abort() {
        running = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRunning() {
        return running;
    }
}
