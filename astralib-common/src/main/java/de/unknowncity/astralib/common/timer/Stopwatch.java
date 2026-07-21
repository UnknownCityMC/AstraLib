package de.unknowncity.astralib.common.timer;

import de.unknowncity.astralib.common.timer.aborttrigger.AbortTrigger;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Stopwatch extends Timer {
    private volatile Duration elapsedDuration = Duration.ZERO;
    private final Consumer<Duration> runOnPause;

    public Stopwatch(
            TimeUnit timeUnit,
            Runnable runOnFinish,
            Consumer<Duration> runOnStep,
            Consumer<Duration> runOnPause,
            Set<AbortTrigger> abortTriggers
    ) {
        super(timeUnit, runOnFinish, runOnStep, abortTriggers);
        this.runOnPause = runOnPause;
    }

    public static class Builder  {
        private Consumer<Duration> runOnPause;
        protected Runnable runOnFinish;
        protected Consumer<Duration> runOnStep;
        protected TimeUnit timeUnit = TimeUnit.SECONDS;
        protected Set<AbortTrigger> abortTriggers = new HashSet<>();

        public Builder withRunOnFinish(Runnable runOnFinish) {
            this.runOnFinish = runOnFinish;
            return this;
        }

        public Builder withRunOnStep(Consumer<Duration> runOnStep) {
            this.runOnStep = runOnStep;
            return this;
        }

        public Builder withTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
            return this;
        }

        public Builder withAbortTriggers(AbortTrigger... abortTriggers) {
            this.abortTriggers.addAll(Arrays.asList(abortTriggers));
            return this;
        }

        public Stopwatch.Builder withRunOnPause(Consumer<Duration> runOnPause) {
            this.runOnPause = runOnPause;
            return this;
        }

        public Stopwatch build() {
            return new Stopwatch(timeUnit, runOnFinish, runOnStep, runOnPause, abortTriggers);
        }
    }

    public static Stopwatch.Builder builder() {
        return new Stopwatch.Builder();
    }

    public Duration elapsed() {
        return elapsedDuration;
    }

    public void start(long stepAmount) {
        executorService = Executors.newSingleThreadScheduledExecutor();
        elapsedDuration = Duration.ZERO;
        running = true;
        step(stepAmount);
    }

    private void step(long stepAmount) {
        // A stopwatch has no natural end, finishing means being stopped
        if (!running) {
            if (runOnFinish != null) {
                runOnFinish.run();
            }
            executorService.shutdown();
            return;
        }

        for (AbortTrigger abortTrigger : abortTriggers) {
            if (abortTrigger.checkForPotentialTrigger()) {
                abortTrigger.runOnAbort().run();
                executorService.shutdown();
                return;
            }
        }

        if (paused) {
            if (runOnPause != null) {
                runOnPause.accept(elapsedDuration);
            }
        } else {
            if (runOnStep != null) {
                runOnStep.accept(elapsedDuration);
            }
        }

        executorService.schedule(() -> {
            if (!paused) {
                elapsedDuration = elapsedDuration.plus(stepAmount, timeUnit.toChronoUnit());
            }
            step(stepAmount);
        }, stepAmount, timeUnit);
    }
}
