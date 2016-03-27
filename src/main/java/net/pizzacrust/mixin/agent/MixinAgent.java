package net.pizzacrust.mixin.agent;

import net.pizzacrust.mixin.Mixin;
import net.pizzacrust.mixin.MixinTransformer;

import java.lang.instrument.Instrumentation;

/**
 * Allows for Mixins to be processed before the application starts.
 *
 * @since 1.0-SNAPSHOT
 * @author PizzaCrust
 */
public class MixinAgent
{
    /**
     * The method that Java calls to execute this agent.
     * @param agentArguments the arguments, that the executor passed on to Java
     * @param instrumentation the instrumentation object
     */
    public static void premain(String agentArguments, Instrumentation instrumentation) {
        System.out.println("MixinAgent -> Detecting Mixins...");
        MixinClassFinder finder = new MixinClassFinder(instrumentation);
        Class<?>[] mixins = finder.find();
        System.out.println("MixinAgent -> Moving into transformation stage...");
        for (Class mixinClass : mixins) {
            Mixin annotation = (Mixin) mixinClass.getAnnotation(Mixin.class);
            MixinTransformer transformer = new MixinTransformer(annotation.value(), mixinClass);
            try {
                transformer.transform();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
