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

public class Countdown extends Timer {

    public Countdown(
            TimeUnit timeUnit,
            Optional<Runnable> runOnFinish,
            Optional<Consumer<Duration>> runOnStep,
            Set<AbortTrigger> abortTriggers
    ) {
        super(timeUnit, runOnFinish, runOnStep, abortTriggers);
    }

    public static class Builder {
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

        public Countdown build() {
            return new Countdown(timeUnit, runOnFinish, runOnStep, abortTriggers);
        }
    }

    public static Countdown.Builder builder() {
        return new Countdown.Builder();
    }

    public void start(int timeSpan) {
        start(Long.valueOf(timeSpan));
    }

    public void start(long timeSpan) {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        running = true;
        step(timeSpan);
    }

    private void step(long timeSpan) {
        if (!running) {
            executorService.shutdown();
            return;
        }

        for (AbortTrigger abortTrigger : abortTriggers) {
            if (abortTrigger.checkForPotentialTrigger()) {
                executorService.shutdown();
                return;
            }
        }

        if (timeSpan <= 0) {
            runOnFinish.ifPresent(Runnable::run);
            executorService.schedule(() -> {
                runOnStep.ifPresent(consumer -> consumer.accept(Duration.of(timeSpan, timeUnit.toChronoUnit())));
            }, 50, TimeUnit.MILLISECONDS);
            executorService.shutdown();
            return;
        }

        runOnStep.ifPresent(consumer -> consumer.accept(Duration.of(timeSpan, timeUnit.toChronoUnit())));
        executorService.schedule(() -> {
            step(paused ? timeSpan : timeSpan - 1);
        }, 1, timeUnit);
    }
}
