package com.plugin.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

public class Utils {

    public static ItemStack giveHead(String headCode){

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 64);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();

            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", headCode));
            Field field;
            try {
                field = meta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(meta, profile);
            } catch(NoSuchFieldException | IllegalArgumentException | IllegalAccessException x) {
                x.printStackTrace();
            }


            skull.setItemMeta(meta);
            skull.getItemMeta().toString();
            return skull;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack givePlayerHead(String owner){

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 64);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        meta.setOwner(owner);


        skull.setItemMeta(meta);
        return skull;
    }

    public static Inventory fillWithItem(Inventory inv, ItemStack filler, ArrayList<Integer> list) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null && !list.contains(i)) {
                inv.setItem(i, filler);
            }
        }

        return inv;
    }

    public static Inventory fillWithItem(Inventory inv, ItemStack filler) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, filler);
            }
        }

        return inv;
    }
}
