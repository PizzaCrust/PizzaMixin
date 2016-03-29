package net.pizzacrust.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bridges from the target class to the Mixin class. It can be a {@link java.lang.reflect.Field} or {@link java.lang.reflect.Method}.
 *
 * @author PizzaCrust
 * @since 1.0-SNAPSHOT
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MixinBridge {
}
