//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package pro.verron.officestamper.utils;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R> extends Function<T, R> {
    R applyWithException(T t) throws Exception;

    default R apply(T t) {
        return (R)this.apply(t, RuntimeException::new);
    }

    default R apply(T t, BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        try {
            return (R)this.applyWithException(t);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw (RuntimeException)exceptionWrapper.apply(ex.getMessage(), ex);
        }
    }

    default ThrowingFunction<T, R> throwing(final BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        return new ThrowingFunction<T, R>() {
            public R applyWithException(T t) throws Exception {
                return (R)ThrowingFunction.this.applyWithException(t);
            }

            public R apply(T t) {
                return (R)this.apply(t, exceptionWrapper);
            }
        };
    }

    static <T, R> ThrowingFunction<T, R> of(ThrowingFunction<T, R> function) {
        return function;
    }

    static <T, R> ThrowingFunction<T, R> of(ThrowingFunction<T, R> function, BiFunction<String, Exception, RuntimeException> exceptionWrapper) {
        return function.throwing(exceptionWrapper);
    }
}
