package com.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarketTabCompleteCommand implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        //Tabcomplete for the commands

        //List<String> TabCompleteList = new ArrayList<>();
        if(args.length == 1){
            //TabCompleteList.add("create");
            //TabCompleteList.add("stand");
            //TabCompleteList.add("settings");
            //TabCompleteList.add("manager");

            return StringUtil.copyPartialMatches(args[0], Arrays.asList(new String[]{"create", "stand", "settings", "manager"}), new ArrayList<>());
        }else if(args.length == 2 && args[0].equalsIgnoreCase("settings")){
            return StringUtil.copyPartialMatches(args[1], Arrays.asList(new String[]{"passadmin", "maxmarketplaces", "reload"}), new ArrayList<>());

            //TabCompleteList.add("passadmin");
            //TabCompleteList.add("maxmarketplaces");
        }else if(args.length == 2 && args[0].equalsIgnoreCase("manager")){
            //TabCompleteList.add("makeadmin");
            //TabCompleteList.add("deletemarkets");

            return StringUtil.copyPartialMatches(args[1], Arrays.asList(new String[]{"makeadmin", "deletemarkets"}), new ArrayList<>());
        }else if(args.length == 3 && args[0].equalsIgnoreCase("settings") && args[1].equalsIgnoreCase("reload")){
            return StringUtil.copyPartialMatches(args[1], Arrays.asList(new String[]{"sign", "hologram"}), new ArrayList<>());
        }

        return null;
    }
}
