package net.pizzacrust.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Injects to the target class. Method must be public, static, and void. And also, be the same method name and parameters as in the target class.
 *
 * @since 1.0-SNAPSHOT
 * @author PizzaCrust
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

    /**
     * The execution type.
     * @return execution type
     */
    Execution value();

    /**
     * Only applicable when Execution is CUSTOM.
     * The line of the injected code.
     * @return the line
     */
    int line() default 0;

    /**
     * Represents execution types.
     */
    enum Execution {
        /**
         * Before the original code.
         */
        BEFORE,
        /**
         * After the original code.
         */
        AFTER,
        /**
         * Allows to customize which line you want the injection to be.
         */
        CUSTOM,
    }
}
