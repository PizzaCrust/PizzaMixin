package net.pizzacrust.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells the transformer to ignore the method inside of a Mixin.
 *
 * @since 1.0-SNAPSHOT
 * @author PizzaCrust
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreMethod {
}
