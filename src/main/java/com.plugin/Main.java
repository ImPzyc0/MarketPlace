package com.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class Main extends JavaPlugin {

    //Main class

    public static Main main;
    //Only instance of the Config class
    private static Config config;

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getPluginManager().registerEvents(new MarketListener(), this);
        

        getCommand("market").setExecutor(new MarketCommand());
        getCommand("market").setTabCompleter(new MarketTabCompleteCommand());
        main = this;
        try {
            config = new Config();
        } catch (IOException e) {
            System.out.println("Config will net lul");
        }
        new MarketPlaceManager();
        MarketPlaceManager.loadData();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        MarketPlaceManager.setConfigData();

    }

    public static Config getDataConfig(){
        return config;
    }

}
