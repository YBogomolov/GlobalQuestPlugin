package com.github.doodlez.bukkit.globalquest.utilities;

import java.util.ArrayList;

/**
 * User: YBogomolov
 * Date: 30.07.11
 * Time: 16:28
 */
public class Diary {
    public static int totalCount = 0;

    public int number;
    public ArrayList<String> text;
    public ArrayList<String> unblockedRecipes;

    public Diary() {
        number = totalCount + 1;
        totalCount++;
        text = new ArrayList<String>();
        unblockedRecipes = new ArrayList<String>();
    }

    public String getName() {
        return "дневник №" + number;
    }
}
