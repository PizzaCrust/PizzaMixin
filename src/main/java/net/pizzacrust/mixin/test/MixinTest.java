package net.pizzacrust.mixin.test;

import net.pizzacrust.mixin.IgnoreMethod;
import net.pizzacrust.mixin.Mixin;

import java.lang.reflect.Field;

/**
 * A test of the Mixin framework.
 *
 * @since 1.0-SNAPSHOT
 * @author PizzaCrust
 */
@Mixin("net.pizzacrust.mixin.test.MixinTest.Victim")
public class MixinTest
{
    public String test = "meow";

    @IgnoreMethod
    public static void main(String[] args) throws Exception {
        for (Field field : MixinTest.Victim.class.getDeclaredFields()) {
            System.out.println(field + " = " + field.get(new MixinTest.Victim()));
        }
    }

    public static class Victim {
        public String name = "plane";
    }
}
