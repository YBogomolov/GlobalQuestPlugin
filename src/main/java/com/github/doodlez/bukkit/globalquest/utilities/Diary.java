package com.github.doodlez.bukkit.globalquest.utilities;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * User: YBogomolov
 * Date: 30.07.11
 * Time: 16:28
 */
public class Diary {
    public static int totalCount = 0;

    public int number;
    public String text;
    public ArrayList<String> unblockedRecipes;

    public Diary() {
        number = totalCount + 1;
        totalCount++;
        unblockedRecipes = new ArrayList<String>();
    }

    public String getName() {
        return "Diary #" + number;
    }


    /**
     * Sets private field to a new value. Use with caution!
     * @param instanceClass Class to which instance belongs.
     * @param instance Instance whose field needed to be edited.
     * @param fieldName Field name (self-explanatory, huh?).
     * @param newValue New field value.
     * @return True, if field was set successfully, and false otherwise.
     * @throws IllegalArgumentException Illegal argument exception
     * @throws SecurityException Security exception
     * @throws NoSuchFieldException No such field exception
     */
    public static boolean setPrivateValue(Class instanceClass, Object instance, String fieldName, Object newValue)
            throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        try {
            Field f = instanceClass.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(instance, newValue);
            return true;
        }
        catch(IllegalAccessException e) {
            System.out.print("Illegal access.");
            return false;
        }
        catch (IndexOutOfBoundsException e) {
            System.out.print("Index out of bounds.");
            return false;
        }
    }
}
