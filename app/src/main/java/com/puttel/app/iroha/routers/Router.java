package com.puttel.app.iroha.routers;

import android.os.Build;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
/**
 * Router, which uses pattern matching to simplify processing of complex structures.
 *
 * It is similar to how web servers and their middlewares are defined:
 *
 * @param <T> extract type {@link R} from type {@link T}
 * @param <R> type, which is used in Router.handle
 * @implNote for every one item {@link T} processes exactly one item {@link R}
 *
 * <ol>
 * <li>Define lambda, which extracts type {@link R} from type {@link T}</li>
 * <li>Creatr Router</li>
 * <li>Define set of middlewares - functions, which accept {@link T} and return {@link T} (possibly
 * different)</li>
 * <li>Define set of handlers - functions, which assign function-handlers to process type {@link
 * R}</li>
 * <li>Execute {@code Router.process(...)} on type {@link T}</li>
 * </ol>
 */

public class Router<T, R> {

    private Consumer<T> defaultHandler = c -> {
    };
    private Map<R, Consumer<T>> handlers = new HashMap<>();
    private Function<T, T> middleware = c -> c;

    private final Function<T, R> getType;

    public Router(Function<T, R> getType) {
        this.getType = getType;
    }


    private void processType(final R type, final T el) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            handlers.getOrDefault(type, defaultHandler).accept(el);
        }
    }

    public Router use(Function<T, T> m) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.middleware = this.middleware.andThen(m);
        }
        return this;
    }

    public Router handleDefault(Consumer<T> c) {
        this.defaultHandler = c;
        return this;
    }

    public Router handle(R type, Consumer<T> h) {
        handlers.put(type, h);
        return this;
    }

    public void process(final T el) {
         T t = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            t = middleware.apply(el);
        }
         R type=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            type = getType.apply(t);
        }
        processType(type, t);
    }
}
