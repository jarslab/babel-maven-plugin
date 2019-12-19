package com.jarslab.maven.babel.plugin.transpiler;

import org.apache.maven.plugin.logging.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * Uses a multiple BabelTranspilers in parallel, each of which performs transpilations
 * in sequence.
 */
class ParallelBabelTranspilerStrategy implements BabelTranspilerStrategy {

    private  int threads;

    ParallelBabelTranspilerStrategy(int threads) {
        this.threads = threads;
    }

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
            log.warn(format("Configured number of threads (%d) exceeds the number of available processors (%d), setting number of threads to %2$d",
                    threads, Runtime.getRuntime().availableProcessors()));
            this.threads = Runtime.getRuntime().availableProcessors();
        }

        // Each thread's task is to create babel transpiler and perform as much transpilations as possible
        Supplier<Collection<Transpilation>> task = () -> {
            BabelTranspiler transpiler = new BabelTranspiler();
            Transpilation transpilation;
            Set<Transpilation> transpiled = new HashSet<>();
            while ((transpilation = queue.poll()) != null) {
                if (log.isDebugEnabled()) {
                    String name = Thread.currentThread().getName();
                    log.debug(format("[%s] transpiling %s", name, transpilation.getSource()));
                }
                transpiled.add(transpiler.execute(transpilation));
            }
            return transpiled;
        };

        Collection<CompletableFuture<Collection<Transpilation>>> futures = new HashSet<>();

        for(int i = 0; i < threads; i++) {
            futures.add(CompletableFuture.supplyAsync(task));
        }

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream);
    }

}
