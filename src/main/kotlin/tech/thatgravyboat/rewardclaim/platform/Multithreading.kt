package tech.thatgravyboat.rewardclaim.platform

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

object Multithreading {

    private val counter = AtomicInteger(0)

    private val scheduledPool: ScheduledExecutorService = Executors.newScheduledThreadPool(10) { target: Runnable? ->
        Thread(target, "Essential Thread " + counter.incrementAndGet())
    }

    private val POOL = ThreadPoolExecutor(
        10, 30,
        0L, TimeUnit.SECONDS,
        LinkedBlockingQueue()
    ) { target: Runnable? -> Thread(target, String.format("Essential %s", counter.incrementAndGet())) }

    @JvmStatic
    fun runAsync(runnable: () -> Unit) {
        POOL.execute(runnable)
    }

    fun schedule(r: Runnable, delay: Long, unit: TimeUnit): ScheduledFuture<*> {
        return scheduledPool.schedule(r, delay, unit)
    }
}