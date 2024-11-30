package de.unknowncity.astralib.common.timer;

import de.unknowncity.astralib.common.timer.aborttrigger.AbortTrigger;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class Timer {
    protected ScheduledExecutorService executorService;
    protected boolean running;
    protected boolean paused;
    protected Optional<Runnable> runOnFinish;
    protected Optional<Consumer<Duration>> runOnStep;
    protected TimeUnit timeUnit;
    protected Set<AbortTrigger> abortTriggers;

    public Timer(
            TimeUnit timeUnit,
            Optional<Runnable> runOnFinish,
            Optional<Consumer<Duration>> runOnStep,
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
