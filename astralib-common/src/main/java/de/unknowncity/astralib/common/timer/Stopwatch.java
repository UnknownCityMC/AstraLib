package de.unknowncity.astralib.common.timer;

import java.time.Duration;
import java.util.EventListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Stopwatch {
    private final ScheduledExecutorService executorService;
    private boolean running;
    private boolean paused;
    private Duration elapsedDuration = Duration.ZERO;

    public Stopwatch() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start(int stepAmount, TimeUnit timeUnit, Consumer<Duration> runAtPause, Consumer<Duration> runPerStep,  Runnable runOnShutdown) {
        running = true;
        step(stepAmount, timeUnit, runAtPause, runPerStep, runOnShutdown);
    }

    public void start(long stepAmount, TimeUnit timeUnit, Consumer<Duration> runAtPause, Consumer<Duration> runPerStep, Runnable runOnShutdown) {
        running = true;
        step(stepAmount, timeUnit, runAtPause, runPerStep, runOnShutdown);
    }

    private void step(long stepAmount, TimeUnit timeUnit, Consumer<Duration> runAtPause, Consumer<Duration> runPerStep, Runnable runOnShutdown) {
        if (!running) {
            runOnShutdown.run();
            executorService.shutdown();
            return;
        }
        if (paused) {
            runAtPause.accept(elapsedDuration);
        } else {
            runPerStep.accept(elapsedDuration);
        }
        executorService.schedule(() -> {
            step(stepAmount, timeUnit, runAtPause, runPerStep, runOnShutdown);
        }, stepAmount, timeUnit);
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