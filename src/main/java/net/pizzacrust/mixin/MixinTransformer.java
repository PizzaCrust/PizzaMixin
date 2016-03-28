package net.pizzacrust.mixin;

import javassist.*;

/**
 * Transforms target class of a specified Mixin.
 *
 * @author PizzaCrust
 *         TODO Work on finding method bridges.
 * @since 1.0-SNAPSHOT
 */
public class MixinTransformer {
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
     *
     * @param targetClass the target class of mixin
     * @param mixinClass  the mixin class
     */
    public MixinTransformer(String targetClass, CtClass mixinClass) throws Exception {
        this.targetClass = ClassPool.getDefault().getCtClass(targetClass);
        this.mixinClass = mixinClass;
    }


    /**
     * Retrieves if the method specified already exists in the target class.
     *
     * @param method  the method
     * @param ctClass the class
     * @return the bool
     */
    private boolean doesMethodAlreadyExists(CtMethod method, CtClass ctClass) {
        try {
            ctClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Retrieves if the field specified already exists in the target class.
     *
     * @param field   the field
     * @param ctClass the class
     * @return the bool
     */
    private boolean doesFieldAlreadyExists(CtField field, CtClass ctClass) throws Exception {
        for (CtField field1 : ctClass.getDeclaredFields()) {
            if (field1.getType() == field.getType() && field1.getName().equals(field.getName())) {
                return true;
            }
        }
        return false;
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
                if (doesFieldAlreadyExists(ctField, targetCtClass)) {
                    for (CtConstructor constructor : targetCtClass.getDeclaredConstructors()) {
                        constructor.insertAfter(ctField.getName() + " = " + fieldInitalizer.value() + ";");
                    }
                } else {
                    targetCtClass.addField(ctField, CtField.Initializer.byExpr(fieldInitalizer.value()));
                }
            }
        }
        System.out.println(targetClass.getName() + " -> Adding Mixin methods...");
        for (CtMethod method : mixinCtClass.getDeclaredMethods()) {
            if (!method.hasAnnotation(IgnoreMethod.class) || !method.hasAnnotation(MixinBridge.class)) {
                if (doesMethodAlreadyExists(method, targetCtClass)) {
                    CtMethod ctMethod = targetCtClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    targetCtClass.removeMethod(ctMethod);
                }
                //CtMethod newMethod = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), targetCtClass);
                //newMethod.setBody(methodBody.value());
                CtMethod newMethod = CtNewMethod.copy(method, targetCtClass, null);
                targetClass.addMethod(newMethod);
            }
        }
        System.out.println(targetClass.getName() + " -> Modifying target hierarchy...");
        for (CtClass interfaceType : mixinCtClass.getInterfaces()) {
            boolean isAlreadyImplemented = false;
            for (CtClass targetInterface : targetCtClass.getInterfaces()) {
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
