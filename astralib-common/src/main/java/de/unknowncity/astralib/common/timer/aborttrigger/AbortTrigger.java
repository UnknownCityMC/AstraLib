package de.unknowncity.astralib.common.timer.aborttrigger;

public abstract class AbortTrigger {
    protected Runnable runOnAbort;
    public AbortTrigger(Runnable runOnAbort) {
        this.runOnAbort = runOnAbort;
    }

    public abstract boolean checkForPotentialTrigger();


    public Runnable runOnAbort() {
        return runOnAbort;
    }
}
