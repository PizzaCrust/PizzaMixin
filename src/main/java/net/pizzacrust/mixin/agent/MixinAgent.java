package net.pizzacrust.mixin.agent;

import javassist.ClassPool;
import javassist.CtClass;
import net.pizzacrust.mixin.Mixin;
import net.pizzacrust.mixin.MixinTransformer;

import java.io.File;
import java.io.FileInputStream;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Allows for Mixins to be processed before the application starts.
 *
 * @since 1.0-SNAPSHOT
 * @author PizzaCrust
 */
public class MixinAgent
{
    /**
     * Retrieves all classes inside of a JAR.
     * @param jarName the jar
     * @return the classes
     * @throws Exception if something goes wrong
     */
    private static String[] getAllClasses(File jarName) throws Exception {
        JarInputStream input = new JarInputStream(new FileInputStream(jarName));
        JarEntry jarEntry;

        ArrayList<String> classNames = new ArrayList<String>();

        while(true) {
            jarEntry = input.getNextJarEntry();
            if (jarEntry == null) {
                break;
            }
            if (jarEntry.getName().endsWith(".class")) {
                String className = jarEntry.getName().substring(0, jarEntry.getName().lastIndexOf('.'));
                className = className.replace('/', '.');
                classNames.add(className);
            }
        }

        return classNames.toArray(new String[classNames.size()]);
    }

    /**
     * The method that Java calls to execute this agent.
     * @param agentArguments the arguments, that the executor passed on to Java
     * @param instrumentation the instrumentation object
     */
    public static void premain(String agentArguments, Instrumentation instrumentation) {
        System.out.println("MixinAgent -> Parsing arguments...");
        File file = new File(agentArguments);
        if (!file.exists()) {
            System.out.println("MixinAgent -> Mixin was given a invalid JAR.");
            return;
        }
        System.out.println("MixinAgent -> Detecting classes...");
        String[] classes;
        try {
            classes = getAllClasses(file);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return;
        }
        for (String theClass : classes) {
            System.out.println("Class -> " + theClass);
        }
        try {
            System.out.println("MixinAgent -> Detecting Mixins...");
            ArrayList<CtClass> mixins = new ArrayList<>();
            for (String theClass : classes) {
                if (ClassPool.getDefault().getCtClass(theClass).hasAnnotation(Mixin.class)) {
                    mixins.add(ClassPool.getDefault().getCtClass(theClass));
                }
            }
            System.out.println("MixinAgent -> Detected " + mixins.size() + " mixins.");
            System.out.println("MixinAgent -> Moving into transformation stage...");
            for (CtClass mixinClass : mixins) {
                Mixin mixin = (Mixin) mixinClass.getAnnotation(Mixin.class);
                MixinTransformer transformer = new MixinTransformer(mixin.value(), mixinClass);
                try {
                    transformer.transform();
                } catch (Exception e) {
                    System.out.println("MixinAgent -> Failed to load mixin class " + mixinClass.getName() + "!");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return;
        }
    }
}
