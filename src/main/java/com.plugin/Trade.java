package com.plugin;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Trade {

    private final ItemStack tradeItem;
    private final ItemStack neededItem;

    public Trade(ItemStack tradeItem, ItemStack neededItem, boolean setLocalizedName){
        //A single Trade with

        if(setLocalizedName){
            ItemMeta tradeMeta = tradeItem.getItemMeta();
            tradeMeta.setLocalizedName(tradeItem.toString());
            tradeItem.setItemMeta(tradeMeta);

            ItemMeta needMeta = neededItem.getItemMeta();
            needMeta.setLocalizedName(neededItem.toString());
            neededItem.setItemMeta(needMeta);
        }


        this.tradeItem = tradeItem;
        this.neededItem = neededItem;


    }

    public ItemStack getTradeItem(){
        return tradeItem;
    }
    public ItemStack getNeededItem(){
        return neededItem;
    }

}
