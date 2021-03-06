package net.pizzacrust.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a class for as a Mixin.
 * Must have a constructor w/ a {@link Object} as the ONLY parameter.
 *
 * @author PizzaCrust
 * @since 1.0-SNAPSHOT
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mixin {
    /**
     * The target class of the Mixin.
     *
     * @return the target class
     */
    String value();
}
