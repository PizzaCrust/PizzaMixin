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
            if (method.hasAnnotation(MethodName.class)) {
                MethodName methodName = (MethodName) method.getAnnotation(MethodName.class);
                ctClass.getDeclaredMethod(methodName.value(), method.getParameterTypes());
            } else {
                ctClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
            }
        } catch (Exception e) {
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
                CtField ctField = new CtField(field, targetCtClass);
                if (doesFieldAlreadyExists(ctField, targetCtClass)) {
                    for (CtField field1 : targetCtClass.getDeclaredFields()) {
                        if (field1.getType() == field.getType() && field1.getName().equals(field.getName())) {
                            FieldInitalizer fieldInitalizer = (FieldInitalizer) field.getAnnotation(FieldInitalizer.class); // field init is now only used for overriding fields because of JASSIST-140
                            for (CtConstructor constructor : targetCtClass.getDeclaredConstructors()) {
                                constructor.insertAfter(field.getName() + " = " + fieldInitalizer.value() + ";");
                            }
                        }
                    }
                } else {
                    targetCtClass.addField(ctField);
                }
            }
        }
        System.out.println(targetClass.getName() + " -> Adding Mixin methods...");
        for (CtMethod method : mixinCtClass.getDeclaredMethods()) {
            if (method.hasAnnotation(Inject.class) && Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers()) && method.getReturnType() == CtClass.voidType) {
                if (doesMethodAlreadyExists(method, targetCtClass)) {
                    Inject annotation = (Inject) method.getAnnotation(Inject.class);
                    CtMethod methodInTargetClass;
                    if (method.hasAnnotation(MethodName.class)) {
                        MethodName methodName = (MethodName) method.getAnnotation(MethodName.class);
                        methodInTargetClass = targetCtClass.getDeclaredMethod(methodName.value(), method.getParameterTypes());
                    } else {
                       methodInTargetClass = targetCtClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    }
                    switch (annotation.value()) {
                        case AFTER:
                            methodInTargetClass.insertAfter(method.getDeclaringClass().getName() + "." + method.getName() + "($$);");
                            break;
                        case BEFORE:
                            methodInTargetClass.insertBefore(method.getDeclaringClass().getName() + "." + method.getName() + "($$);");
                            break;
                        case CUSTOM:
                            int line = annotation.line();
                            methodInTargetClass.insertAt(line, method.getDeclaringClass().getName() + "." + method.getName() + "($$);");
                            break;
                    }
                } else {
                    System.out.println("MixinTransformer -> Error -> Retrieved a invalid @Inject annotation on: " + method.getName());
                }
            } else {
                if (!method.hasAnnotation(IgnoreMethod.class) || !method.hasAnnotation(MixinBridge.class) || !method.hasAnnotation(Inject.class)) {
                    if (doesMethodAlreadyExists(method, targetCtClass)) {
                        CtMethod ctMethod;
                        if (method.hasAnnotation(MethodName.class)) {
                            MethodName methodName = (MethodName) method.getAnnotation(MethodName.class);
                            ctMethod = targetCtClass.getDeclaredMethod(methodName.value(), method.getParameterTypes());
                        } else {
                           ctMethod = targetCtClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                        }
                        targetCtClass.removeMethod(ctMethod);
                    }
                    //CtMethod newMethod = new CtMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), targetCtClass);
                    //newMethod.setBody(methodBody.value());
                    CtMethod newMethod;
                    if (method.hasAnnotation(MethodName.class)) {
                        MethodName methodName = (MethodName) method.getAnnotation(MethodName.class);
                        newMethod = CtNewMethod.copy(method, methodName.value(), targetCtClass, null);
                    } else {
                        newMethod = CtNewMethod.copy(method, targetCtClass, null);
                    }
                    targetClass.addMethod(newMethod);
                }
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
