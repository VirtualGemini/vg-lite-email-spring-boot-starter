package com.velox.email.support.util;

import com.velox.email.common.message.EmailCommonMessages;
import com.velox.email.exception.EmailSendException;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public final class ManagedEmailExecutor implements Executor, AutoCloseable {

    private static final long SHUTDOWN_TIMEOUT_SECONDS = 5L;
    private static final Runnable NO_OP_CLOSE = () -> {
    };

    private final Executor executor;
    private final Runnable closeAction;

    private ManagedEmailExecutor(Executor executor, Runnable closeAction) {
        this.executor = requireExecutor(executor);
        this.closeAction = closeAction != null ? closeAction : NO_OP_CLOSE;
    }

    public static ManagedEmailExecutor direct() {
        return new ManagedEmailExecutor(Runnable::run, NO_OP_CLOSE);
    }

    public static ManagedEmailExecutor fixedThreadPool(int concurrencyLimit, String threadNamePrefix) {
        ExecutorService executorService = Executors.newFixedThreadPool(
                concurrencyLimit,
                Thread.ofPlatform().name(threadNamePrefix, 0).factory()
        );
        return new ManagedEmailExecutor(executorService, () -> shutdownGracefully(executorService));
    }

    public static ManagedEmailExecutor boundedVirtualThreads(int concurrencyLimit, String threadNamePrefix) {
        Semaphore semaphore = new Semaphore(concurrencyLimit);
        ExecutorService executorService = Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name(threadNamePrefix, 0).factory()
        );
        return new ManagedEmailExecutor(command -> {
            acquirePermit(semaphore);
            try {
                executorService.execute(() -> {
                    try {
                        command.run();
                    } finally {
                        semaphore.release();
                    }
                });
            } catch (RuntimeException exception) {
                semaphore.release();
                throw exception;
            }
        }, () -> shutdownGracefully(executorService));
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }

    @Override
    public void close() {
        closeAction.run();
    }

    private static void acquirePermit(Semaphore semaphore) {
        try {
            semaphore.acquire();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new EmailSendException(EmailCommonMessages.EMAIL_EXECUTOR_INTERRUPTED, exception);
        }
    }

    private static Executor requireExecutor(Executor executor) {
        if (executor == null) {
            throw new EmailSendException(EmailCommonMessages.EXECUTOR_MUST_NOT_BE_NULL);
        }
        return executor;
    }

    private static void shutdownGracefully(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException exception) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
