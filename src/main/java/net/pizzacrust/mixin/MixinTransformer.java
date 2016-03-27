package net.pizzacrust.mixin;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Transforms target class of a specified Mixin.
 *
 * @since 1.0-SNAPSHOT
 * @author PizzaCrust
 * TODO Work on finding method bridges.
 */
public class MixinTransformer
{
    /**
     * Represents the target class of the Mixin.
     */
    private Class<?> targetClass;
    /**
     * Represents the mixin class.
     */
    private Class<?> mixinClass;

    /**
     * Constructs a new {@link MixinTransformer} object.
     * @param targetClass the target class of mixin
     * @param mixinClass the mixin class
     */
    public MixinTransformer(Class<?> targetClass, Class<?> mixinClass) {
        this.targetClass = targetClass;
        this.mixinClass = mixinClass;
    }


    /**
     * Transforms the target class accordingly with the mixin class.
     */
    public void transform() throws Exception {
        System.out.println("Mixin -> Transforming " + targetClass.getName() + " with " + mixinClass.getName() + "...");
        System.out.println(targetClass.getName() + " -> Adding Mixin fields...");
        CtClass targetCtClass = ClassPool.getDefault().getCtClass(targetClass.getName());
        CtClass mixinCtClass = ClassPool.getDefault().getCtClass(mixinClass.getName());
        for (CtField field : mixinCtClass.getDeclaredFields()) {
            if (!field.hasAnnotation(MixinBridge.class)) {
                targetCtClass.addField(field);
            }
        }
    }
}
