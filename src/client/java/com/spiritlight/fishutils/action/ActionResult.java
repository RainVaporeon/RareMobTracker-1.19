package com.spiritlight.fishutils.action;

import com.spiritlight.fishutils.collections.Pair;
import com.spiritlight.fishutils.misc.ThrowingRunnable;
import com.spiritlight.fishutils.misc.ThrowingSupplier;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/*
* INTERNAL CHANGELOG:
* 1.1.1: All references to returnValue is now retrieved with the getter.

 */
/**
 * An object representing the execution status of an action. This is bundled
 * with a {@link Result} denoting the execution result, and can hold a return value
 * and a throwable if an exception had occurred.
 * <p>
 * The implementation also supports multiple chaining statements to provide ease of execution,
 * such as {@link ActionResult#expect(Class, Consumer)} to handle specific exceptions,
 * throwing exceptions with {@link ActionResult#throwIfPresent()} and such.
 * @param <T> The return type
 * @apiNote This object is not meant to be serialized, and only recommended to be used as
 * return values for simplifying handling the result of executing something.
 */
public class ActionResult<T> {

    private static final ActionResult<?> DEFAULT_SUCCESS = new ActionResult<>(Result.SUCCESS, null);

    private final Result result;
    private final T returnValue;
    private final Throwable throwable;
    private boolean exceptionHandled;

    /**
     * Creates a new ActionResult instance with the provided parameters
     * @param result A non-null value representing the result state
     * @param returnValue The return value of this object
     * @param throwable The throwable if any is present
     */
    public ActionResult(Result result, T returnValue, Throwable throwable) {
        Objects.requireNonNull(result);
        this.result = result;
        this.returnValue = returnValue;
        this.throwable = throwable;
        this.exceptionHandled = throwable == null;
    }

    /**
     * Creates a new ActionResult holding the result and a return value, the throwable is null in this case
     * @param result The result
     * @param returnValue The return value
     */
    public ActionResult(Result result, T returnValue) {
        this(result, returnValue, null);
    }

    /**
     * Creates a new ActionResult holding the result and a throwable, the return value is null in this case
     * @param result The result
     * @param throwable The return value
     */
    public ActionResult(Result result, Throwable throwable) {
        this(result, null, throwable);
    }

    // 1.1: Returns itself instead
    /**
     * Accepts this result's returned value
     * @param consumer the consumer to handle the value
     * @return the object itself, or fail if any exceptions occurred
     * @apiNote if execution fails, the returned ActionResult still will hold this object's
     * return value.
     */
    public ActionResult<T> consume(Consumer<T> consumer) {
        try {
            consumer.accept(this.getReturnValue());
            return this;
        } catch (Exception e) {
            return new ActionResult<>(Result.ERROR, this.getReturnValue(), e);
        }
    }

    /**
     * Accepts this result and maps it to another value
     * @param function The mapping function
     * @return The action result for the returned value
     * @param <R> The return type
     */
    public <R> ActionResult<R> map(Function<T, R> function) {
        try {
            return new ActionResult<>(Result.SUCCESS, function.apply(this.getReturnValue()));
        } catch (Throwable t) {
            return new ActionResult<>(Result.ERROR, t);
        }
    }

    /**
     * Handles the exception, if any is present.
     * @param type The type of exception to expect
     * @param action the handler for the exception
     * @return the same ActionResult for chaining purposes
     * @param <X> the type of exception
     * @apiNote if one of the exceptions were handled successfully,
     * then this is considered a "handled result", and so calling
     * {@link ActionResult#getReturnValue()} will not throw the exception.
     */
    public <X extends Throwable> ActionResult<T> expect(Class<X> type, Consumer<X> action) {
        if(this.throwable == null) return this;
        if(type.isAssignableFrom(this.throwable.getClass())) {
            //noinspection unchecked
            action.accept((X) this.throwable);
            this.exceptionHandled = true;
        }
        return this;
    }

    /**
     * Handles the exception and produces a new value
     * @param type The type of exception to expect
     * @param handler The handler to execute, if this type is expected
     * @return the same ActionResult for chaining purposes, or a new one specified by the handler
     * @param <X> The exception type
     */
    public <X extends Throwable> ActionResult<T> expect(Class<X> type, Function<X, T> handler) {
        if(this.throwable == null) return this;
        if(type.isAssignableFrom(this.throwable.getClass())) {
            return ActionResult.success(handler.apply((X) this.throwable));
        }
        return this;
    }

    /**
     * Acquires the return value
     * @return the return value
     * @apiNote this method will throw an exception if
     * one is present and is not handled.
     */
    public T getReturnValue() {
        if(!this.exceptionHandled && this.throwable != null) {
            this.throwUnchecked();
        }
        return returnValue;
    }

    /**
     * Gets the throwable, if any is present
     * @return the throwable
     * @apiNote if this method is called, it's assumed that
     * the exception is handled, and therefore any future calls of
     * {@link ActionResult#getReturnValue()} will not throw an exception.
     */
    public Throwable getThrowable() {
        this.exceptionHandled = true;
        return throwable;
    }

    /**
     * Handles the return value if this action was successful
     *
     * @param consumer the handler if the result is {@link Result#SUCCESS}
     * @return itself for chaining purposes
     */
    public ActionResult<T> onSuccess(Consumer<T> consumer) {
        if(result == Result.SUCCESS) {
            consumer.accept(this.getReturnValue());
        }
        return this;
    }

    /**
     * Utility method to handle any throwable if the result is not {@link Result#SUCCESS}.
     * @param consumer a consumer that takes a Result and a Throwable for handling
     * @return itself for chaining purposes
     * @see ActionResult#expect(Class, Consumer)
     */
    public ActionResult<T> onFail(BiConsumer<Result, Throwable> consumer) {
        if(result != Result.SUCCESS && this.throwable != null) {
            consumer.accept(result, this.throwable);
            this.exceptionHandled = true;
        }
        return this;
    }

    public ActionResult<T> onFail(Consumer<Throwable> consumer) {
        if(result != Result.SUCCESS && this.throwable != null) {
            consumer.accept(this.throwable);
            this.exceptionHandled = true;
        }
        return this;
    }

    /**
     * Ignores this exception and moves on
     * @return the object itself
     */
    public ActionResult<T> ignoreFail() {
        this.exceptionHandled = true;
        return this;
    }

    /**
     * Throws the exception if the provided type is caught
     * @param type The type of exception
     * @return itself for chaining purposes, if no such exception exist
     * @param <X> The exception type
     * @throws X The exception
     */
    public <X extends Throwable> ActionResult<T> throwIf(Class<X> type) throws X {
        if(this.throwable == null) return this;
        if(Objects.requireNonNull(type).isAssignableFrom(this.throwable.getClass())) throw (X) this.throwable;
        return this;
    }

    /**
     * Throws the exception if any is present
     * @return itself for chaining purposes, if no such exception exist
     * @throws Throwable if one is present, regardless whether it has been handled.
     */
    public ActionResult<T> throwIfPresent() throws Throwable {
        if(this.throwable != null) throw throwable;
        return this;
    }

    /**
     * Checks whether the result is {@link Result#SUCCESS}
     * @return
     */
    public boolean isSuccessful() {
        return this.result == Result.SUCCESS;
    }

    /**
     * Checks whether the result is not {@link Result#SUCCESS}
     * @return
     */
    public boolean failed() {
        return !isSuccessful();
    }

    // as of version 1.1, this returns a shared object instead
    /**
     * Convenience method to create an ActionResult that holds {@code null}
     * as the return type.
     */
    public static <T> ActionResult<T> success() {
        return (ActionResult<T>) DEFAULT_SUCCESS;
    }

    /**
     * Convenience method to create an ActionResult that the provided element
     * as the return type.
     */
    public static <T> ActionResult<T> success(T element) {
        return new ActionResult<>(Result.SUCCESS, element, null);
    }

    /**
     * Convenience method to create an ActionResult with the current
     * stacktrace as the exception.
     */
    public static <T> ActionResult<T> fail() {
        return new ActionResult<>(Result.FAIL, getDefaultException());
    }

    // note: If throwable is not null it used to return FAIL here, if something breaks,
    // this should be the first thing to check out.

    // right as of version 1.1, null-supplied fail no longer fetches
    // the exception, if needed, use ActionResult#fail().
    /**
     * Convenience method to create an ActionResult with the provided
     * throwable as the exception.
     * @param throwable The throwable, can be null
     * @return An ActionResult holding {@link Result#FAIL}, or {@link Result#ERROR}
     * if throwable is not null
     */
    public static <T> ActionResult<T> fail(Throwable throwable) {
        if(throwable == null) return new ActionResult<>(Result.FAIL, null, null);
        return new ActionResult<>(Result.ERROR, throwable);
    }

    public static <T> ActionResult<T> tryAction(ThrowingSupplier<T> action) {
        try {
            return new ActionResult<>(Result.SUCCESS, action.get());
        } catch (Throwable e) {
            return new ActionResult<>(Result.ERROR, e);
        }
    }

    public static <T> ActionResult<T> tryAction(ResultRunnable<T> runnable) {
        try {
            Pair<Result, T> pair = runnable.run();
            return new ActionResult<>(pair.getKey(), pair.getValue());
        } catch (Throwable e) {
            return ActionResult.fail(e);
        }
    }

    public static ActionResult<Void> tryAction(ThrowingRunnable runnable) {
        try {
            runnable.run();
            return ActionResult.success();
        } catch (Throwable e) {
            return ActionResult.fail(e);
        }
    }

    public Result getResult() {
        return result;
    }

    private static Throwable getDefaultException() {
        return new RuntimeException("No information provided").fillInStackTrace();
    }

    public ActionResult<T> throwUnchecked() {
        if(this.throwable == null) return this;
        if(this.throwable instanceof Error e) throw e;
        if(this.throwable instanceof RuntimeException rte) throw rte;
        throw new RuntimeException(this.throwable);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionResult<?> that = (ActionResult<?>) o;
        return exceptionHandled == that.exceptionHandled && result == that.result && Objects.equals(returnValue, that.returnValue) && Objects.equals(throwable, that.throwable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, returnValue, throwable, exceptionHandled);
    }
}
