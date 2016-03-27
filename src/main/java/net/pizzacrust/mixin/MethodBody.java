package net.pizzacrust.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a method body.
 *
 * @since 1.0-SNAPSHOT
 * @author PizzaCrust
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodBody {
    /**
     * The method body.
     * @return method body
     */
    String value();
}
