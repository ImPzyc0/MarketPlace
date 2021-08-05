package com.plugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    private File datafile;
    private YamlConfiguration changeDataFile;

    public Config() throws IOException {
        datafile = new File(Bukkit.getServer().getPluginManager().getPlugin("MarketPlaces").getDataFolder(), "data.yml");
        if(datafile.exists()){
            datafile.createNewFile();
        }

        changeDataFile = YamlConfiguration.loadConfiguration(datafile);
        changeDataFile.save(datafile);
    }

    public YamlConfiguration getDataFile(){
        return changeDataFile;
    }
    public File getFile(){
        return datafile;
    }

}
