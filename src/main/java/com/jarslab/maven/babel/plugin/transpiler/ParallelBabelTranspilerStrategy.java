package com.jarslab.maven.babel.plugin.transpiler;

import lombok.AllArgsConstructor;
import org.apache.maven.plugin.logging.Log;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * Uses a multiple BabelTranspilers in parallel, each of which performs transpilations
 * in sequence.
 */
@AllArgsConstructor
class ParallelBabelTranspilerStrategy implements BabelTranspilerStrategy {

    private  int threads;

    @Override
    public Stream<Transpilation> execute(Set<Transpilation> transpilations) {

        ConcurrentLinkedQueue<Transpilation> queue = new ConcurrentLinkedQueue<>(transpilations);

        Log log = transpilations.iterator().next().getContext().getLog();

        if (threads < 1) {
            log.warn(format("Invalid number of threads (%d). Setting number of threads to 1", threads));
            threads = 1;
        }

        // Use a maximum number of threads equal to the amount of available processors
        if(threads > Runtime.getRuntime().availableProcessors()) {
            log.warn(format("Number of threads more than available processors (%d), setting number of threads to %d",
                    threads, Runtime.getRuntime().availableProcessors()));
            this.threads = Runtime.getRuntime().availableProcessors();
        }

        // Each thread's task is to create babel transpiler and perform as much transpilations as possible
        Runnable task = () -> {
            BabelTranspiler transpiler = new BabelTranspiler();
            Transpilation transpilation;
            while((transpilation = queue.poll()) != null){
                if (log.isDebugEnabled()) {
                    String name = Thread.currentThread().getName();
                    log.debug(format("[%s] transpiling %s", name, transpilation.getSource()));
                }
                transpiler.execute(transpilation);
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        for(int i = 0; i < threads; i++) {
            executorService.submit(task);
        }

        // Wait for the tasks to finish
        try {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            transpilations.iterator().next().getContext().getLog().error(e.getMessage(), e);
        }

        // Return a parallel stream, allowing for writing the result in parallel.
        return transpilations.parallelStream();
    }

}
