package net.pizzacrust.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a field initalizer.
 *
 * @since 1.0-SNAPSHOT
 * @author PizzaCrust
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldInitalizer {
    /**
     * The java source code of the inititalizer.
     * Example: new java.lang.String("test");
     * @return the source
     */
    String value();
}
