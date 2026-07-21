package de.unknowncity.astralib.common.database.dao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared pool for blocking JDBC calls so daos don't occupy the common fork-join pool
 */
public final class DaoExecutor {
    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger(1);
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2, runnable -> {
        var thread = new Thread(runnable, "astralib-dao-" + THREAD_COUNTER.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    });

    private DaoExecutor() {

    }
}
