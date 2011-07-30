/**
 * User: YBogomolov
 * Date: 29.07.11
 * Time: 16:13
 */

package com.github.doodlez.bukkit.globalquest.listeners;

import com.github.doodlez.bukkit.globalquest.GlobalQuestPlugin;
import com.github.doodlez.bukkit.globalquest.utilities.Diary;
import net.minecraft.server.CraftingManager;
import net.minecraft.server.CraftingRecipe;
import net.minecraft.server.ItemStack;
import net.minecraft.server.ShapedRecipes;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static com.github.doodlez.bukkit.globalquest.utilities.Diary.setPrivateValue;

public class SpecialServerListener extends ServerListener {
    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();

        if (plugin.getDescription().getName().equals("GlobalQuestPlugin")) {
            CraftingManager craftingManager = CraftingManager.getInstance();
            List<CraftingRecipe> craftList = craftingManager.b();

            List<CraftingRecipe> editedList = new ArrayList<CraftingRecipe>();

            for (CraftingRecipe recipe : craftList) {
                if (recipe instanceof ShapedRecipes) {
                    ShapedRecipes shapedRecipe = (ShapedRecipes)recipe;
                    ItemStack recipeResult = shapedRecipe.b();
                    String resultName = recipeResult.getItem().j();

                    if (GlobalQuestPlugin.blockedRecipes.contains(resultName)) {
                        System.out.print(resultName + " is blocked!");
                        GlobalQuestPlugin.backupRecipes.put(resultName, recipe);
                    }
                    else {
                        editedList.add(recipe);
                    }
                }
            }

            try {
                if (Diary.setPrivateValue(CraftingManager.class, craftingManager, "b", editedList))
                    System.out.print("All right, set edited list!");
                else
                    System.out.print("Nope.");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

}
