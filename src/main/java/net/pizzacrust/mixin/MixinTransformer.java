package net.pizzacrust.mixin;

import javassist.*;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private CtClass targetClass;
    /**
     * Represents the mixin class.
     */
    private CtClass mixinClass;

    /**
     * Constructs a new {@link MixinTransformer} object.
     * @param targetClass the target class of mixin
     * @param mixinClass the mixin class
     */
    public MixinTransformer(String targetClass, CtClass mixinClass) throws Exception {
        this.targetClass = ClassPool.getDefault().getCtClass(targetClass);
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
                FieldInitalizer fieldInitalizer = (FieldInitalizer) field.getAnnotation(FieldInitalizer.class);
                CtField ctField = new CtField(field.getType(), field.getName(), targetCtClass);
                targetCtClass.addField(ctField, CtField.Initializer.byExpr(fieldInitalizer.value()));
            }
        }
        System.out.println(targetClass.getName() + " -> Adding Mixin methods...");
        for (CtMethod method : mixinCtClass.getDeclaredMethods()) {
            if (!method.hasAnnotation(IgnoreMethod.class) || !method.hasAnnotation(MixinBridge.class)) {
                //CtMethod newMethod = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), targetCtClass);
                //newMethod.setBody(methodBody.value());
                CtMethod newMethod = CtNewMethod.copy(method, targetCtClass, null);
                targetClass.addMethod(newMethod);
            }
        }
        System.out.println(targetClass.getName() + " -> Modifying target hierarchy...");
        for (CtClass interfaceType : mixinCtClass.getInterfaces()) {
            boolean isAlreadyImplemented = false;
            for(CtClass targetInterface : targetCtClass.getInterfaces()) {
                if (targetInterface == interfaceType) {
                    isAlreadyImplemented = true;
                }
            }
            if (!isAlreadyImplemented) {
                targetCtClass.addInterface(interfaceType);
            }
        }
        System.out.println("Mixin -> Inserting " + targetClass.getName() + " into class loader...");
        targetCtClass.toClass();
    }
}
