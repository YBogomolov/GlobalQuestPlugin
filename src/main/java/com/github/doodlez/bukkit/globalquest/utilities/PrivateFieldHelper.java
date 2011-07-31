package com.github.doodlez.bukkit.globalquest.utilities;

import java.lang.reflect.Field;

public class PrivateFieldHelper {
    /**
     * Sets private field to a new value. Use with caution!
     *
     * @param instanceClass Class to which instance belongs.
     * @param instance      Instance whose field needed to be edited.
     * @param fieldName     Field name (self-explanatory, huh?).
     * @param newValue      New field value.
     * @return True, if field was set successfully, and false otherwise.
     * @throws IllegalArgumentException Illegal argument exception
     * @throws SecurityException        Security exception
     * @throws NoSuchFieldException     No such field exception
     */
    public static boolean setPrivateValue(Class instanceClass, Object instance, String fieldName, Object newValue)
            throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        try {
            Field f = instanceClass.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(instance, newValue);
            return true;
        } catch (IllegalAccessException e) {
            System.out.print("Illegal access.");
            return false;
        } catch (IndexOutOfBoundsException e) {
            System.out.print("Index out of bounds.");
            return false;
        }
    }

    /**
     * Tries to set book's maxStackSize to specified count.
     * @param count Max stack size.
     * @return True, if field was set successfully, and false otherwise.
     */
    public static boolean setBookStack(int count) {
        try {
            Field maxStackSize = net.minecraft.server.Item.class.getDeclaredField("maxStackSize");
            maxStackSize.setAccessible(true);
            maxStackSize.setInt(net.minecraft.server.Item.BOOK, count);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}