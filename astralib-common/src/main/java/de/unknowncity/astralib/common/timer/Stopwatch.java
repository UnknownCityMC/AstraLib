package de.unknowncity.astralib.common.timer;

import de.unknowncity.astralib.common.timer.aborttrigger.AbortTrigger;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Stopwatch extends Timer {
    private Duration elapsedDuration = Duration.ZERO;
    private Optional<Consumer<Duration>> runOnPause = Optional.empty();

    public Stopwatch(
            TimeUnit timeUnit,
            Optional<Runnable> runOnFinish,
            Optional<Consumer<Duration>> runOnStep,
            Optional<Consumer<Duration>> runOnPause,
            Set<AbortTrigger> abortTriggers
    ) {
        super(timeUnit, runOnFinish, runOnStep, abortTriggers);
        this.runOnPause = runOnPause;
    }

    public static class Builder  {
        private Optional<Consumer<Duration>> runOnPause = Optional.empty();
        protected Optional<Runnable> runOnFinish = Optional.empty();
        protected Optional<Consumer<Duration>> runOnStep = Optional.empty();
        protected TimeUnit timeUnit = TimeUnit.SECONDS;
        protected Set<AbortTrigger> abortTriggers = new HashSet<>();

        public Builder withRunOnFinish(Runnable runOnFinish) {
            this.runOnFinish = Optional.of(runOnFinish);
            return this;
        }

        public Builder withRunOnStep(Consumer<Duration> runOnStep) {
            this.runOnStep = Optional.of(runOnStep);
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
            this.runOnPause = Optional.of(runOnPause);
            return this;
        }

        public Stopwatch build() {
            return new Stopwatch(timeUnit, runOnFinish, runOnStep, runOnPause, abortTriggers);
        }
    }

    public static Stopwatch.Builder builder() {
        return new Stopwatch.Builder();
    }

    public void start(int stepAmount) {
        start(Long.valueOf(stepAmount));
    }

    public void start(long stepAmount) {
        executorService = Executors.newSingleThreadScheduledExecutor();
        running = true;
        step(stepAmount);
    }

    private void step(long stepAmount) {
        if (!running) {
            runOnFinish.ifPresent(Runnable::run);
            executorService.shutdown();
            return;
        }

        for (AbortTrigger abortTrigger : abortTriggers) {
            if (abortTrigger.checkForPotentialTrigger()) {
                executorService.shutdown();
                return;
            }
        }

        if (paused) {
            runOnPause.ifPresent(consumer -> consumer.accept(elapsedDuration));
        } else {
            runOnStep.ifPresent(consumer -> consumer.accept(elapsedDuration));
        }
        executorService.schedule(() -> {
            step(stepAmount);
        }, stepAmount, timeUnit);
    }
}