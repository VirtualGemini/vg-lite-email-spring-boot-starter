package io.github.virtualgemini.vgliteemail.support.util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ManagedEmailExecutorTest {

    @Test
    void virtualTasksHonorConcurrencyLimitAndReleasePermits() throws Exception {
        ManagedEmailExecutor executor = ManagedEmailExecutor.boundedVirtualThreads(2, "test-email-");
        CountDownLatch started = new CountDownLatch(2);
        CountDownLatch release = new CountDownLatch(1);
        AtomicInteger active = new AtomicInteger();
        AtomicInteger maximum = new AtomicInteger();
        Runnable task = () -> {
            int current = active.incrementAndGet();
            maximum.accumulateAndGet(current, Math::max);
            started.countDown();
            try {
                assertThat(Thread.currentThread().isVirtual()).isTrue();
                assertThat(Thread.currentThread().getName()).startsWith("test-email-");
                assertThat(release.await(5, TimeUnit.SECONDS)).isTrue();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new AssertionError(exception);
            } finally {
                active.decrementAndGet();
            }
        };
        try {
            CompletableFuture<Void> first = CompletableFuture.runAsync(task, executor);
            CompletableFuture<Void> second = CompletableFuture.runAsync(task, executor);
            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
            CompletableFuture<CompletableFuture<Void>> third = CompletableFuture.supplyAsync(
                    () -> CompletableFuture.runAsync(task, executor));
            release.countDown();
            CompletableFuture.allOf(first, second, third.get(5, TimeUnit.SECONDS)).get(5, TimeUnit.SECONDS);
            assertThat(maximum.get()).isEqualTo(2);
        } finally {
            release.countDown();
            executor.close();
        }
        assertThatThrownBy(() -> executor.execute(() -> {})).isInstanceOf(RejectedExecutionException.class);
    }

    @Test
    void platformPoolUsesNamedPlatformThreadsAndCloses() throws Exception {
        ManagedEmailExecutor executor = ManagedEmailExecutor.fixedThreadPool(1, "platform-email-");
        try {
            Thread worker = CompletableFuture.supplyAsync(Thread::currentThread, executor).get(5, TimeUnit.SECONDS);
            assertThat(worker.isVirtual()).isFalse();
            assertThat(worker.getName()).startsWith("platform-email-");
        } finally {
            executor.close();
        }
        assertThatThrownBy(() -> executor.execute(() -> {})).isInstanceOf(RejectedExecutionException.class);
    }
}
