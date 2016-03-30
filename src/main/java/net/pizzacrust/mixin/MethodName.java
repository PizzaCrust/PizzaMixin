package net.pizzacrust.mixin;

/**
 * Indicates to the transformer that the method the transformer looking at is defined as the annotated name.
 *
 * @since 1.0-SNAPSHOT
 * @author PizzaCrust
 */
public @interface MethodName {
    /**
     * The method name.
     * @return the method name
     */
    String value();
}
