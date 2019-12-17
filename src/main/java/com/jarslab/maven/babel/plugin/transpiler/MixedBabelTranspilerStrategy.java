package com.jarslab.maven.babel.plugin.transpiler;

import org.apache.maven.plugin.logging.Log;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * Uses a multiple BabelTranspilers in parallel, which each performs transpilations
 * in sequence.
 */
class MixedBabelTranspilerStrategy implements BabelTranspilerStrategy {

    @Override
    public Stream<Transpilation> execute(Set<Transpilation> transpilations) {

        ConcurrentLinkedQueue<Transpilation> queue = new ConcurrentLinkedQueue<>(transpilations);

        // Each thread's task is to create babeltranspiler and perform as much transpilations as possible
        Runnable task = () -> {
            BabelTranspiler transpiler = new BabelTranspiler();
            Transpilation transpilation;
            while((transpilation = queue.poll()) != null){
                Log log =transpilation.getContext().getLog();
                if (log.isDebugEnabled()) {
                    String name = Thread.currentThread().getName();
                    log.debug(format("[%s] transpiling %s", name, transpilation.getSource()));
                }
                transpiler.execute(transpilation);
            }
        };

        // Devide the work over as much threads as there are processorc
        int threads = Runtime.getRuntime().availableProcessors() / 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        for(int i = 0; i < threads; i++) {
            executorService.submit(task);
        }

        // Wait for the tasks to finish
        try {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            transpilations.iterator().next().getContext().getLog().error(e.getMessage(), e);
        }

        // Return a parallel stream, allowing for writing the result in parallel.
        return transpilations.parallelStream();
    }

}
