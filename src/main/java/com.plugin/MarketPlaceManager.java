package com.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MarketPlaceManager {
    //Manages and saves all data used for the Plugin, one instance

    public static HashMap<Player, MarketPlace> PlayersInMarketPlaces;
    public static ArrayList<MarketPlace> marketPlaces;
    public static HashMap<Player, MarketStand> playersInTradeInventorys;
    public static Integer maxMarketPlaces;

    public MarketPlaceManager(){
        PlayersInMarketPlaces = new HashMap<>();
        marketPlaces = new ArrayList<>();
        playersInTradeInventorys = new HashMap<>();
        maxMarketPlaces = 1000000000;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.main, new Runnable() {
            @Override
            public void run() {
                MarketPlaceManager.setConfigData();
                //System.out.println("saved data");
            }
        }, 40, 60);

    }

    public static boolean newPathWayNeeded(MarketPlace marketPlace){
        //berechnet mit den 2 Listen ob man einen neuen Pathway braucht
        if(marketPlace.getMarketStands().size() % 2 == 1){
            return true;
        }
        return false;
    }

    public static void setConfigData(){
        Main.getDataConfig().getDataFile().set("maxmarketplaces", maxMarketPlaces);
        Main.getDataConfig().getDataFile().createSection("marketplaces");
        for(int j = 0; j< marketPlaces.size(); j++){
            MarketPlace h = marketPlaces.get(j);
            //Sachen des MarketPlaces speichern
            Main.getDataConfig().getDataFile().set("marketplaces."+ j +".marketmiddle.min", h.getMarketMiddle().getArea().getMin());
            Main.getDataConfig().getDataFile().set("marketplaces."+ j +".marketmiddle.max", h.getMarketMiddle().getArea().getMax());
            //Cuboid MarketMiddle
            Main.getDataConfig().getDataFile().set("marketplaces."+ j +".owner", h.getOwner().toString());
            //Owner
            Main.getDataConfig().getDataFile().set("marketplaces."+ j +".middle", h.getMiddle());
            //Middle Location
            Main.getDataConfig().getDataFile().set("marketplaces."+ j +".checkarea.max", h.getCheckArea().getMax());
            Main.getDataConfig().getDataFile().set("marketplaces."+ j +".checkarea.min", h.getCheckArea().getMin());
            //CheckArea
            Main.getDataConfig().getDataFile().createSection("marketplaces."+ j +".marketstand");

            for(int i = 0; i< h.getMarketStands().size(); i++){
                MarketStand l = h.getMarketStands().get(i);
                //Owner
                Main.getDataConfig().getDataFile().set("marketplaces."+ j +".marketstand." + i+ ".owner", l.getOwner().toString());
                //Area cuboid
                Main.getDataConfig().getDataFile().set("marketplaces."+ j +".marketstand." + i + ".area.min", l.getArea().getMin());
                Main.getDataConfig().getDataFile().set("marketplaces."+ j +".marketstand." + i + ".area.max", l.getArea().getMax());
                //Location middle
                Main.getDataConfig().getDataFile().set("marketplaces."+ j +".marketstand." + i + ".middle", l.getMarketStandLocation());
                //portLocation
                Main.getDataConfig().getDataFile().set("marketplaces."+ j +".marketstand." + i + ".portlocation", l.getPortLocation());
                //trade Inventory
                Main.getDataConfig().getDataFile().createSection("marketplaces."+ j +".marketstand." + i+ ".tradeinventory");
                for(int t = 0; t<l.getTradeInventory().length; t++){
                    if(l.getTradeInventory()[t] != null){
                        Main.getDataConfig().getDataFile().set("marketplaces."+ j +".marketstand." + i+ ".tradeinventory."+ t, l.getTradeInventory()[t]);
                    }
                }
                //active
                Main.getDataConfig().getDataFile().set("marketplaces."+ j +".marketstand." + i + ".active", l.getActive());
                //standPosition
                Main.getDataConfig().getDataFile().set("marketplaces."+ j +".marketstand." + i + ".standposition", l.getStandPosition());

                Main.getDataConfig().getDataFile().createSection("marketplaces."+ j +".marketstand." + i+ ".trades");
                for(int o = 0; o<l.getTrades().size(); o++){
                    Trade q = l.getTrades().get(o);
                    //ItemStack 1 & 2 speichern

                    Main.getDataConfig().getDataFile().set("marketplaces."+ j +".marketstand." + i + ".trades." + o + ".needed", q.getNeededItem());
                    Main.getDataConfig().getDataFile().set("marketplaces."+ j +".marketstand." + i + ".trades." + o + ".traded", q.getTradeItem());
                }
            }
        }
        try {
            Main.getDataConfig().getDataFile().save(Main.getDataConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadData(){
        if(Main.getDataConfig().getDataFile().getConfigurationSection("marketplaces.") != null) {
            for (int i = 0; i < Main.getDataConfig().getDataFile().getConfigurationSection("marketplaces.").getKeys(false).size(); i++) {
                Location middle = Main.getDataConfig().getDataFile().getLocation("marketplaces." + i + ".middle");
                UUID placeOwner = UUID.fromString(Main.getDataConfig().getDataFile().getString("marketplaces." + i + ".owner"));
                MarketPlace place = new MarketPlace(middle, placeOwner, true);
                if(Main.getDataConfig().getDataFile().getConfigurationSection("marketplaces." + i + ".marketstand.") != null) {
                    for (int j = 0; j < Main.getDataConfig().getDataFile().getConfigurationSection("marketplaces." + i + ".marketstand.").getKeys(false).size(); j++) {
                        UUID standOwner = UUID.fromString(Main.getDataConfig().getDataFile().getString("marketplaces." + i + ".marketstand." + j + ".owner"));
                        MarketStand stand = new MarketStand(standOwner, place, true);
                        if (Main.getDataConfig().getDataFile().getConfigurationSection("marketplaces." + i + ".marketstand." + j + ".trades.") != null) {
                            for (int k = 0; k < Main.getDataConfig().getDataFile().getConfigurationSection("marketplaces." + i + ".marketstand." + j + ".trades.").getKeys(false).size(); k++) {
                                ItemStack needed = Main.getDataConfig().getDataFile().getItemStack("marketplaces." + i + ".marketstand." + j + ".trades." + k + ".needed");
                                ItemStack traded = Main.getDataConfig().getDataFile().getItemStack("marketplaces." + i + ".marketstand." + j + ".trades." + k + ".traded");
                                stand.addTrade(traded, needed, false);
                            }
                        }

                        if (Main.getDataConfig().getDataFile().getConfigurationSection("marketplaces." + i + ".marketstand." + j + ".tradeinventory.") != null) {
                            for (int t = 0; t < Main.getDataConfig().getDataFile().getConfigurationSection("marketplaces." + i + ".marketstand." + j + ".tradeinventory.").getKeys(false).size(); t++) {
                                ItemStack stack = Main.getDataConfig().getDataFile().getItemStack("marketplaces." + i + ".marketstand." + j + ".tradeinventory." + t);
                                if(stack != null){
                                    stand.addItemToTradeInventory(stack);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
