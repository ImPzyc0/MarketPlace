package com.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class MarketCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Command executor for all commands. Only one class because it is only one command, /market

        if (args.length != 0 && (sender instanceof Player)) {
            Player player = (Player) sender;
            //Creates a market if possible, checks everything before
            if (args[0].equalsIgnoreCase("create")) {
                if(MarketPlaceManager.marketPlaces.size() == MarketPlaceManager.maxMarketPlaces){
                    player.sendMessage(ChatColor.RED + "The max amount of MarketPlaces has been reached!");
                    if(player.isOp()){
                        player.sendMessage("You can change the max amount of MarketStands with /market settings maxmarketplaces <number>");
                    }
                    return false;
                }
                if (!MarketPlaceManager.PlayersInMarketPlaces.containsKey(player)) {
                    for(MarketPlace i : MarketPlaceManager.marketPlaces){
                        if(i.getCheckArea().containsLocation(player.getLocation())){
                            player.sendMessage(ChatColor.RED + "There is a MarketPlace nearby!");
                            return false;
                        }
                    }
                    if(player.getLocation().getBlockY()-1 > player.getLocation().getWorld().getHighestBlockYAt(player.getLocation())){
                        player.sendMessage(ChatColor.RED + "You need to be on the ground to build a MarketPlace!");
                        return false;
                    }
                    new MarketPlace(player.getLocation(), player.getUniqueId(), false);
                } else {
                    player.sendMessage(ChatColor.RED + "You can't make a new MarketPlace here!");
                }

                //Creates a market stand if possible
            } else if (args[0].equalsIgnoreCase("stand")) {
                //create a new MarketStand
                if (MarketPlaceManager.PlayersInMarketPlaces.containsKey(player)) {
                    if(MarketPlaceManager.PlayersInMarketPlaces.get(player).getMarketStands().size() > 144){
                        player.sendMessage(ChatColor.RED + "The max amount of MarketStands has been reached!");
                        return false;
                    }
                    for (MarketStand i : MarketPlaceManager.PlayersInMarketPlaces.get(player).getMarketStands()) {
                        if (i.getOwner().equals(player.getUniqueId()) && !player.isOp()) {
                            player.sendMessage(ChatColor.RED + "You already have a market stand!");
                            return false;
                        }
                    }
                    new MarketStand(player.getUniqueId(), MarketPlaceManager.PlayersInMarketPlaces.get(player), false);

                } else {
                    player.sendMessage(ChatColor.RED + "You need to be in a MarketPlace to use this command!");
                }

                //Debug commands for market places
            } else if (args[0].equalsIgnoreCase("manager") && player.isOp()) {
                if(args.length < 2){
                    player.sendMessage(ChatColor.RED + "Invalid arguments!");
                    return false;
                }
                if (args[1].equalsIgnoreCase("deletemarkets")) {
                    MarketPlaceManager.marketPlaces.clear();
                    Main.getDataConfig().getDataFile().set("marketplaces", null);
                    player.sendMessage(ChatColor.GREEN + "Market List cleared");
                }else if(args[1].equalsIgnoreCase("makeadmin")){
                    if(MarketPlaceManager.PlayersInMarketPlaces.containsKey(player)){
                        MarketPlaceManager.PlayersInMarketPlaces.get(player).setOwner(player.getUniqueId());
                        player.sendMessage("You are now Owner of this MarketPlace.");
                    }else{
                        player.sendMessage("You need to be in a MarketPlace to use this command.");
                    }
                }
                //Settings of MarketStands and MarketPlacesd
            }else if(args[0].equalsIgnoreCase("settings") && MarketPlaceManager.PlayersInMarketPlaces.containsKey(player)){
                if(MarketPlaceManager.PlayersInMarketPlaces.get(player).getOwner().equals(player.getUniqueId())){
                    //Settings ändern für den MarketPlace
                    if(args.length >= 3){
                        if(args[1].equalsIgnoreCase("passadmin")){
                            if(Bukkit.getPlayer(args[2])!= null){
                                MarketPlaceManager.PlayersInMarketPlaces.get(player).setOwner(Bukkit.getPlayer(args[2]).getUniqueId());
                                player.sendMessage(ChatColor.GREEN + "Made " + args[2] + " the Owner of the MarketPlace!");
                            }else{
                                player.sendMessage(ChatColor.RED + "Player couldn't be found!");
                            }
                        }if(args[1].equalsIgnoreCase("reload")){
                            if(args[2].equalsIgnoreCase("sign")){
                                if(MarketPlaceManager.PlayersInMarketPlaces.containsKey(player)){
                                    for(MarketStand j : MarketPlaceManager.PlayersInMarketPlaces.get(player).getMarketStands()){
                                        if(j.getArea().containsLocation(player.getLocation()) && j.getOwner().equals(player.getUniqueId())){
                                            j.setTradeSign();
                                            player.sendMessage(ChatColor.GREEN + "Tradesign reloaded successfully!");
                                            return false;
                                        }else if(j.getArea().containsLocation(player.getLocation()) && !j.getOwner().equals(player.getUniqueId())){
                                            player.sendMessage(ChatColor.RED + "You need to be the Owner of the MarketStand to use this command!");
                                            return false;
                                        }
                                    }
                                }else{
                                    player.sendMessage(ChatColor.RED + "You need to be in a MarketPlace to use this command!");
                                }
                            }else if(args[2].equalsIgnoreCase("stand") || args[2].equalsIgnoreCase("hologram")){
                                if(MarketPlaceManager.PlayersInMarketPlaces.containsKey(player)){
                                    for(MarketStand j : MarketPlaceManager.PlayersInMarketPlaces.get(player).getMarketStands()){
                                        if(j.getArea().containsLocation(player.getLocation()) && j.getOwner().equals(player.getUniqueId())){
                                            j.setArmorStand();
                                            player.sendMessage(ChatColor.GREEN + "Hologram reloaded successfully!");
                                            return false;
                                        }else if(j.getArea().containsLocation(player.getLocation()) && !j.getOwner().equals(player.getUniqueId())){
                                            player.sendMessage(ChatColor.RED + "You need to be the Owner of the MarketStand to use this command!");
                                            return false;
                                        }
                                    }
                                    player.sendMessage(ChatColor.RED + "You need to be in a MarketStand to use this command!");
                                }else{
                                    player.sendMessage(ChatColor.RED + "You need to be in a MarketPlace to use this command!");
                                }
                            }else{
                                player.sendMessage(ChatColor.RED + "Invalid Argument!");
                            }
                        } else{
                            player.sendMessage("Marketplace settings:");
                            player.sendMessage("-/market settings passadmin <player>");
                        }
                    }else{
                        player.sendMessage("Marketplace settings:");
                        player.sendMessage("-/market settings passadmin <player>");
                    }
                }else if(!MarketPlaceManager.PlayersInMarketPlaces.get(player).getOwner().equals(player.getUniqueId())){
                    //Settings ändern für den MarketPlace
                    if(args.length >= 3) {
                        if (args[1].equalsIgnoreCase("reload")) {
                            if (args[2].equalsIgnoreCase("sign")) {
                                if (MarketPlaceManager.PlayersInMarketPlaces.containsKey(player)) {
                                    for (MarketStand j : MarketPlaceManager.PlayersInMarketPlaces.get(player).getMarketStands()) {
                                        if (j.getArea().containsLocation(player.getLocation()) && j.getOwner().equals(player.getUniqueId())) {
                                            j.setTradeSign();
                                            player.sendMessage(ChatColor.GREEN + "Tradesign reloaded successfully!");
                                            return false;
                                        } else if (j.getArea().containsLocation(player.getLocation()) && !j.getOwner().equals(player.getUniqueId())) {
                                            player.sendMessage(ChatColor.RED + "You need to be the Owner of the MarketStand to use this command!");
                                            return false;
                                        }
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "You need to be in a MarketPlace to use this command!");
                                }
                            } else if (args[2].equalsIgnoreCase("stand") || args[2].equalsIgnoreCase("hologram")) {
                                if (MarketPlaceManager.PlayersInMarketPlaces.containsKey(player)) {
                                    for (MarketStand j : MarketPlaceManager.PlayersInMarketPlaces.get(player).getMarketStands()) {
                                        if (j.getArea().containsLocation(player.getLocation()) && j.getOwner().equals(player.getUniqueId())) {
                                            j.setArmorStand();
                                            player.sendMessage(ChatColor.GREEN + "Hologram reloaded successfully!");
                                            return false;
                                        } else if (j.getArea().containsLocation(player.getLocation()) && !j.getOwner().equals(player.getUniqueId())) {
                                            player.sendMessage(ChatColor.RED + "You need to be the Owner of the MarketStand to use this command!");
                                            return false;
                                        }
                                    }
                                    player.sendMessage(ChatColor.RED + "You need to be in a MarketStand to use this command!");
                                } else {
                                    player.sendMessage(ChatColor.RED + "You need to be in a MarketPlace to use this command!");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Invalid Argument!");
                            }
                        } else {
                            player.sendMessage("Marketstand settings:");
                            player.sendMessage("-/market settings reload sign");
                            player.sendMessage("-/market settings reload hologram");
                        }
                    }
                }else{
                    player.sendMessage(ChatColor.RED + "You need to be the Owner of the MarketPlace to access the settings!");
                    return false;
                }
            }else if(args[0].equalsIgnoreCase("settings") && !MarketPlaceManager.PlayersInMarketPlaces.containsKey(player)){
                if(player.isOp()){
                    //Settings ändern für alle MarketPlaces
                    if(args.length >= 3){
                        if(args[1].equalsIgnoreCase("maxmarketplaces")){
                            try{
                                MarketPlaceManager.maxMarketPlaces = Integer.parseInt(args[2]);
                                player.sendMessage(ChatColor.GREEN + "Max amount of MarketPlaces has been set to " + args[2]);
                                return false;
                            }catch(NumberFormatException x){
                                player.sendMessage(ChatColor.RED + "Invalid Argument! Please pass a Number!");
                                return false;
                            }
                        }else{
                            player.sendMessage("Marketplace plugin settings:");
                            player.sendMessage("-/market settings maxmarketplaces <number>");
                        }
                    }else{
                        player.sendMessage("Marketplace settings:");
                        player.sendMessage("-/market settings maxmarketplaces <number>");
                    }

                }else{
                    player.sendMessage(ChatColor.GREEN + "You need to be Operator to access the MarketPlace settings!");
                    return false;
                }
            }

            else {
                player.sendMessage("Unknown Command! Market commands are:");
                player.sendMessage("/market stand - create a Market stand inside a Marketplace");
                player.sendMessage("/market create - create a Marketplace");
                player.sendMessage("/market settings  - Change settings of the MarketPlace you are in or change the settings for all MarketPlaces");
                player.sendMessage("/market manager - debug commands");
            }
        }


        return false;
    }
}
