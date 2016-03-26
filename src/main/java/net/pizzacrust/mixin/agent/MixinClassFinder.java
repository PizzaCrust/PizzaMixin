package net.pizzacrust.mixin.agent;

import net.pizzacrust.mixin.Mixin;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;

/**
 * Finds all classes loaded in the classpath annotated with {@link net.pizzacrust.mixin.Mixin}.
 *
 * @since 1.0-SNAPSHOT
 * @author PizzaCrust
 */
public class MixinClassFinder {
    /**
     * The instrumentation object that allows us to instrument with the class loader.
     */
    private Instrumentation agent;

    /**
     * Constructs a new {@link MixinClassFinder} object.
     * @param agent the instrumentation object
     */
    public MixinClassFinder(Instrumentation agent) {
        this.agent = agent;
    }

    /**
     * Finds all classes that are annotated with {@link Mixin}.
     * @return the classes
     */
    public Class<?>[] find() {
        ArrayList<Class> classes = new ArrayList<Class>();
        for (Class loadedClass : agent.getAllLoadedClasses()) {
            if (loadedClass.isAnnotationPresent(Mixin.class)) {
                classes.add(loadedClass);
            }
        }
        return classes.toArray(new Class<?>[classes.size()]);
    }
}
