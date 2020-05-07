package com.neo.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;
@Component
public class ExecutorServiceUtil {
    private final static ThreadFactory NAMED_THREAD_FACTORY = (Runnable r) -> new Thread(r, "IndexStatisticsService-" + r.hashCode());
    //    private final static ExecutorService EXECUTOR = new ThreadPoolExecutor(20, 20,
//            120, TimeUnit.SECONDS,
//            new ArrayBlockingQueue(100), NAMED_THREAD_FACTORY);
    public final static ExecutorService EXECUTOR = new ThreadPoolExecutor(20, 20,
            120, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), NAMED_THREAD_FACTORY);
}
