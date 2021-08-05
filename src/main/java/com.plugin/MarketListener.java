package com.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;


public class MarketListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        //Sends titles to Players who enter/leave MarketPlaces and manages the ArrayLists and HashMaps of MarketPlaceManager
        boolean isInArea = false;
        for(MarketPlace p : MarketPlaceManager.marketPlaces){
            if(p.getCheckArea().containsLocation(e.getPlayer().getLocation())){
                isInArea = true;
                break;
            }
        }

        if(!isInArea){return;}

        boolean playerIsInMarketPlaceNow = false;
        MarketPlace MarketPlaceThePlayerIsIn = null;
        boolean playerIsInMarketPlace = false;
        MarketPlace MarketPlaceThePlayerIsInNow = null;

        if(MarketPlaceManager.PlayersInMarketPlaces.containsKey(e.getPlayer())){
            playerIsInMarketPlace = true;
            MarketPlaceThePlayerIsIn = MarketPlaceManager.PlayersInMarketPlaces.get(e.getPlayer());
        }


        for(MarketPlace i : MarketPlaceManager.marketPlaces){
            for(MarketStand j : i.getMarketStands()){
                if(j.getArea().containsLocation(e.getTo().getBlock().getLocation()) && !j.getOwner().equals(e.getPlayer().getUniqueId())){
                    e.getPlayer().teleport(j.getPortLocation());
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    playerIsInMarketPlaceNow = true;
                    MarketPlaceThePlayerIsInNow = i;
                }else if((j.getArea().containsLocation(e.getTo().getBlock().getLocation().getBlock().getLocation()) && j.getOwner().equals(e.getPlayer().getUniqueId())) ){
                    playerIsInMarketPlaceNow = true;
                    MarketPlaceThePlayerIsInNow = i;
                }
            }
            for(MarketPathWay y : i.getMarketPathways()){
                if(y.getArea().containsLocation(e.getTo().getBlock().getLocation())){
                    playerIsInMarketPlaceNow = true;
                    MarketPlaceThePlayerIsInNow = i;
                }
            }
            if(i.getMarketMiddle().getArea().containsLocation(e.getTo().getBlock().getLocation())){
                playerIsInMarketPlaceNow = true;
                MarketPlaceThePlayerIsInNow = i;
            }

        }

        if(!playerIsInMarketPlace && playerIsInMarketPlaceNow){
            MarketPlaceManager.PlayersInMarketPlaces.put(e.getPlayer(), MarketPlaceThePlayerIsInNow);
            e.getPlayer().sendTitle(ChatColor.GREEN + "You entered a", ChatColor.GREEN + "MarketPlace", 10, 50, 10);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE,0.2f , 1);
        } else if(playerIsInMarketPlace && !playerIsInMarketPlaceNow){
            MarketPlaceManager.PlayersInMarketPlaces.remove(e.getPlayer());
            e.getPlayer().sendTitle("", ChatColor.AQUA + "You left the MarketPlace!", 5, 30, 5);
        } else if(playerIsInMarketPlace && MarketPlaceThePlayerIsIn != MarketPlaceThePlayerIsInNow){
            MarketPlaceManager.PlayersInMarketPlaces.replace(e.getPlayer(), MarketPlaceThePlayerIsInNow);
            e.getPlayer().sendTitle(ChatColor.DARK_GREEN + "You entered a new ", ChatColor.DARK_GREEN + "MarketPlace", 10, 50, 10);
        }


    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e){
        //Cancells the event if the Player is in a MarketPlace
        if((e.getEntity().getType() == EntityType.PLAYER && MarketPlaceManager.PlayersInMarketPlaces.containsKey(e.getEntity()) || ((e.getDamager().getType() == EntityType.PLAYER && MarketPlaceManager.PlayersInMarketPlaces.containsKey(e.getDamager()) && e.getEntity().getType() == EntityType.PLAYER)))){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBlockDestroy(BlockBreakEvent e){
        //Makes that only Owners can break blocks in MarketPlaces/MarketStands
        boolean isInArea = false;
        for(MarketPlace p : MarketPlaceManager.marketPlaces){
            if(p.getCheckArea().containsLocation(e.getPlayer().getLocation())){
                isInArea = true;
                break;
            }
        }

        if(!isInArea){return;}

        if(e.getBlock().getType().equals(Material.OAK_WALL_SIGN)){
            Sign idk = (Sign) e.getBlock().getState();
            if(idk.getLine(1).equalsIgnoreCase(ChatColor.GREEN +"Trade!")){
                e.setCancelled(true);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                return;
            }
        }

        for(MarketPlace i : MarketPlaceManager.marketPlaces){
            for(MarketStand j : i.getMarketStands()){
                if(j.getArea().containsLocation(e.getBlock().getLocation()) && !j.getOwner().equals(e.getPlayer().getUniqueId())) {
                    e.setCancelled(true);
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                }else if(j.getArea().containsLocation(e.getBlock().getLocation()) && j.getOwner().equals(e.getPlayer().getUniqueId()) && e.getBlock().getType().toString().contains("WOOL")){
                    e.getBlock().setType(Material.AIR);
                    e.setCancelled(true);
                }
            }
            for(MarketPathWay y : i.getMarketPathways()){
                if(y.getArea().containsLocation(e.getBlock().getLocation()) && !e.getPlayer().getUniqueId().equals(i.getOwner())){
                    e.setCancelled(true);
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                }else if(y.getArea().containsLocation(e.getBlock().getLocation()) && e.getPlayer().getUniqueId().equals(i.getOwner()) && e.getBlock().getType().equals(Material.COBBLESTONE)){
                    e.getBlock().setType(Material.AIR);
                    e.setCancelled(true);
                }
            }
            if(i.getMarketMiddle().getArea().containsLocation(e.getBlock().getLocation()) &&  !i.getOwner().equals(e.getPlayer().getUniqueId())){
                e.setCancelled(true);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            }else if(i.getMarketMiddle().getArea().containsLocation(e.getBlock().getLocation()) &&  i.getOwner().equals(e.getPlayer().getUniqueId()) &&( e.getBlock().getType().equals(Material.STONE_BRICKS) || e.getBlock().getType().equals(Material.SEA_LANTERN))){
                e.getBlock().setType(Material.AIR);
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onPlayerBlockPlace(BlockPlaceEvent e){
        //Makes that only Owners can place blocks in MarketPlaces/MarketStands

        boolean isInArea = false;
        for(MarketPlace p : MarketPlaceManager.marketPlaces){
            if(p.getCheckArea().containsLocation(e.getPlayer().getLocation())){
                isInArea = true;
                break;
            }
        }

        if(!isInArea){return;}

        for(MarketPlace place : MarketPlaceManager.marketPlaces){
            if(place.getCheckArea().containsLocation(e.getPlayer().getLocation())){

                for(MarketPlace i : MarketPlaceManager.marketPlaces){
                    for(MarketStand j : i.getMarketStands()){
                        if(j.getArea().containsLocation(e.getBlock().getLocation()) && !j.getOwner().equals(e.getPlayer().getUniqueId())){
                            e.setCancelled(true);
                            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        }
                    }
                    for(MarketPathWay y : i.getMarketPathways()){
                        if(y.getArea().containsLocation(e.getBlock().getLocation()) && !e.getPlayer().getUniqueId().equals(i.getOwner())){
                            e.setCancelled(true);
                            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        }
                    }
                    if(i.getMarketMiddle().getArea().containsLocation(e.getBlock().getLocation()) &&  !i.getOwner().equals(e.getPlayer().getUniqueId())){
                        e.setCancelled(true);
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    }
                }

                break;
            }
        }
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent e){
        //Opens trade inventorys when someone clicks on a trade sign
        boolean isInArea = false;
        for(MarketPlace p : MarketPlaceManager.marketPlaces){
            if(p.getCheckArea().containsLocation(e.getPlayer().getLocation())){
                isInArea = true;
                break;
            }
        }

        if(!isInArea){return;}

        for(MarketPlace place : MarketPlaceManager.marketPlaces) {
            if(place.getCheckArea().containsLocation(e.getPlayer().getLocation())) {
                if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.OAK_WALL_SIGN)) {
                        Sign idk = (Sign) e.getClickedBlock().getState();
                        if (idk.getLine(1).equalsIgnoreCase(ChatColor.GREEN + "Trade!")) {
                            //Funktion aufrufen die das TradeInventar öffnet
                            if (e.getClickedBlock().getBlockData().getAsString().equals("minecraft:oak_wall_sign[facing=north,waterlogged=false]")) {
                                if (!MarketPlaceManager.PlayersInMarketPlaces.containsKey(e.getPlayer())) return;
                                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                                for (MarketStand i : MarketPlaceManager.PlayersInMarketPlaces.get(e.getPlayer()).getMarketStands()) {
                                    if (i.getOwner().equals(e.getPlayer().getUniqueId()) && i.getMarketStandLocation().equals(new Location(e.getClickedBlock().getLocation().getWorld(),
                                            e.getClickedBlock().getLocation().getX(),
                                            e.getClickedBlock().getLocation().getY() - 1,
                                            e.getClickedBlock().getLocation().getZ() + 3))) {
                                        i.openTradeInventoryOwner();
                                        if (MarketPlaceManager.playersInTradeInventorys.containsKey(e.getPlayer())) {
                                            MarketPlaceManager.playersInTradeInventorys.remove(e.getPlayer());
                                        }
                                        MarketPlaceManager.playersInTradeInventorys.put(e.getPlayer(), i);
                                    } else if (!i.getOwner().equals(e.getPlayer().getUniqueId()) && i.getMarketStandLocation().equals(new Location(e.getClickedBlock().getLocation().getWorld(),
                                            e.getClickedBlock().getLocation().getX(),
                                            e.getClickedBlock().getLocation().getY() - 1,
                                            e.getClickedBlock().getLocation().getZ() + 3)) && i.getTrades().size() != 0) {
                                        if(i.getActive()){
                                            i.openTradeInventory(e.getPlayer(), 1);
                                            if (MarketPlaceManager.playersInTradeInventorys.containsKey(e.getPlayer())) {
                                                MarketPlaceManager.playersInTradeInventorys.remove(e.getPlayer());
                                            }
                                            MarketPlaceManager.playersInTradeInventorys.put(e.getPlayer(), i);
                                        }else{
                                            e.getPlayer().sendMessage(ChatColor.RED + "This Stand is deactivated!");
                                        }
                                    } else if (!i.getOwner().equals(e.getPlayer().getUniqueId()) && i.getMarketStandLocation().equals(new Location(e.getClickedBlock().getLocation().getWorld(),
                                            e.getClickedBlock().getLocation().getX(),
                                            e.getClickedBlock().getLocation().getY() - 1,
                                            e.getClickedBlock().getLocation().getZ() + 3)) && i.getTrades().size() == 0) {
                                        e.getPlayer().sendMessage(ChatColor.RED + "This Stand does not have any trades!");
                                    }
                                }

                            } else if (e.getClickedBlock().getBlockData().getAsString().equals("minecraft:oak_wall_sign[facing=south,waterlogged=false]")) {
                                if (!MarketPlaceManager.PlayersInMarketPlaces.containsKey(e.getPlayer())) return;
                                for (MarketStand i : MarketPlaceManager.PlayersInMarketPlaces.get(e.getPlayer()).getMarketStands()) {
                                    if (i.getOwner().equals(e.getPlayer().getUniqueId()) && i.getMarketStandLocation().equals(new Location(e.getClickedBlock().getLocation().getWorld(),
                                            e.getClickedBlock().getLocation().getX(),
                                            e.getClickedBlock().getLocation().getY() - 1,
                                            e.getClickedBlock().getLocation().getZ() - 3))) {
                                        i.openTradeInventoryOwner();
                                        if (MarketPlaceManager.playersInTradeInventorys.containsKey(e.getPlayer())) {
                                            MarketPlaceManager.playersInTradeInventorys.remove(e.getPlayer());
                                        }
                                        MarketPlaceManager.playersInTradeInventorys.put(e.getPlayer(), i);
                                    } else if (!i.getOwner().equals(e.getPlayer().getUniqueId()) && i.getMarketStandLocation().equals(new Location(e.getClickedBlock().getLocation().getWorld(),
                                            e.getClickedBlock().getLocation().getX(),
                                            e.getClickedBlock().getLocation().getY() - 1,
                                            e.getClickedBlock().getLocation().getZ() - 3)) && i.getTrades().size() != 0) {
                                        if(i.getActive()){
                                            i.openTradeInventory(e.getPlayer(), 1);
                                            if (MarketPlaceManager.playersInTradeInventorys.containsKey(e.getPlayer())) {
                                                MarketPlaceManager.playersInTradeInventorys.remove(e.getPlayer());
                                            }
                                            MarketPlaceManager.playersInTradeInventorys.put(e.getPlayer(), i);
                                        }else{
                                            e.getPlayer().sendMessage(ChatColor.RED + "This Stand is deactivated!");
                                        }
                                    } else if (!i.getOwner().equals(e.getPlayer().getUniqueId()) && i.getMarketStandLocation().equals(new Location(e.getClickedBlock().getLocation().getWorld(),
                                            e.getClickedBlock().getLocation().getX(),
                                            e.getClickedBlock().getLocation().getY() - 1,
                                            e.getClickedBlock().getLocation().getZ() - 3)) && i.getTrades().size() == 0) {
                                        e.getPlayer().sendMessage(ChatColor.RED + "This Stand does not have any trades!");
                                    }
                                }
                            } else if (e.getClickedBlock().getBlockData().getAsString().equals("minecraft:oak_wall_sign[facing=east,waterlogged=false]")) {
                                if (!MarketPlaceManager.PlayersInMarketPlaces.containsKey(e.getPlayer())) return;
                                for (MarketStand i : MarketPlaceManager.PlayersInMarketPlaces.get(e.getPlayer()).getMarketStands()) {
                                    if (i.getOwner().equals(e.getPlayer().getUniqueId()) && i.getMarketStandLocation().equals(new Location(e.getClickedBlock().getLocation().getWorld(),
                                            e.getClickedBlock().getLocation().getX() - 3,
                                            e.getClickedBlock().getLocation().getY() - 1,
                                            e.getClickedBlock().getLocation().getZ()))) {
                                        i.openTradeInventoryOwner();
                                        if (MarketPlaceManager.playersInTradeInventorys.containsKey(e.getPlayer())) {
                                            MarketPlaceManager.playersInTradeInventorys.remove(e.getPlayer());
                                        }
                                        MarketPlaceManager.playersInTradeInventorys.put(e.getPlayer(), i);
                                    } else if (!i.getOwner().equals(e.getPlayer().getUniqueId()) && i.getMarketStandLocation().equals(new Location(e.getClickedBlock().getLocation().getWorld(),
                                            e.getClickedBlock().getLocation().getX() - 3,
                                            e.getClickedBlock().getLocation().getY() - 1,
                                            e.getClickedBlock().getLocation().getZ())) && i.getTrades().size() != 0) {
                                        if(i.getActive()){
                                            i.openTradeInventory(e.getPlayer(), 1);
                                            if (MarketPlaceManager.playersInTradeInventorys.containsKey(e.getPlayer())) {
                                                MarketPlaceManager.playersInTradeInventorys.remove(e.getPlayer());
                                            }
                                            MarketPlaceManager.playersInTradeInventorys.put(e.getPlayer(), i);
                                        }else{
                                            e.getPlayer().sendMessage(ChatColor.RED + "This Stand is deactivated!");
                                        }
                                    } else if (!i.getOwner().equals(e.getPlayer().getUniqueId()) && i.getMarketStandLocation().equals(new Location(e.getClickedBlock().getLocation().getWorld(),
                                            e.getClickedBlock().getLocation().getX() - 3,
                                            e.getClickedBlock().getLocation().getY() - 1,
                                            e.getClickedBlock().getLocation().getZ())) && i.getTrades().size() == 0) {
                                        e.getPlayer().sendMessage(ChatColor.RED + "This Stand does not have any trades!");
                                    }
                                }
                            } else if (e.getClickedBlock().getBlockData().getAsString().equals("minecraft:oak_wall_sign[facing=west,waterlogged=false]")) {
                                if (!MarketPlaceManager.PlayersInMarketPlaces.containsKey(e.getPlayer())) return;
                                for (MarketStand i : MarketPlaceManager.PlayersInMarketPlaces.get(e.getPlayer()).getMarketStands()) {
                                    if (i.getOwner().equals(e.getPlayer().getUniqueId()) && i.getMarketStandLocation().equals(new Location(e.getClickedBlock().getLocation().getWorld(),
                                            e.getClickedBlock().getLocation().getX() + 3,
                                            e.getClickedBlock().getLocation().getY() - 1,
                                            e.getClickedBlock().getLocation().getZ()))) {
                                        i.openTradeInventoryOwner();
                                        if (MarketPlaceManager.playersInTradeInventorys.containsKey(e.getPlayer())) {
                                            MarketPlaceManager.playersInTradeInventorys.remove(e.getPlayer());
                                        }
                                        MarketPlaceManager.playersInTradeInventorys.put(e.getPlayer(), i);
                                    } else if (!i.getOwner().equals(e.getPlayer().getUniqueId()) && i.getMarketStandLocation().equals(new Location(e.getClickedBlock().getLocation().getWorld(),
                                            e.getClickedBlock().getLocation().getX() + 3,
                                            e.getClickedBlock().getLocation().getY() - 1,
                                            e.getClickedBlock().getLocation().getZ())) && i.getTrades().size() != 0) {
                                        if (i.getActive()) {
                                            i.openTradeInventory(e.getPlayer(), 1);
                                            if (MarketPlaceManager.playersInTradeInventorys.containsKey(e.getPlayer())) {
                                                MarketPlaceManager.playersInTradeInventorys.remove(e.getPlayer());
                                            }
                                            MarketPlaceManager.playersInTradeInventorys.put(e.getPlayer(), i);
                                        } else {
                                            e.getPlayer().sendMessage(ChatColor.RED + "This Stand is deactivated!");
                                        }
                                    } else if (!i.getOwner().equals(e.getPlayer().getUniqueId()) && i.getMarketStandLocation().equals(new Location(e.getClickedBlock().getLocation().getWorld(),
                                            e.getClickedBlock().getLocation().getX() + 3,
                                            e.getClickedBlock().getLocation().getY() - 1,
                                            e.getClickedBlock().getLocation().getZ())) && i.getTrades().size() == 0) {
                                        e.getPlayer().sendMessage(ChatColor.RED + "This Stand does not have any trades!");
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
        }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        //Redirects the event to the MarketStand
        if(MarketPlaceManager.playersInTradeInventorys.containsKey(e.getWhoClicked()) && e.getCurrentItem() != null){
            MarketPlaceManager.playersInTradeInventorys.get(e.getWhoClicked()).InventoryClick(e);
        }
    }

    @EventHandler
    public void onSignBreak(BlockPhysicsEvent e){
        //Makes the Trade sign unbreakable
        for(MarketPlace place : MarketPlaceManager.marketPlaces) {
            if(place.getCheckArea().containsLocation(e.getBlock().getLocation())) {
                if (e.getChangedType().equals(Material.AIR) && e.getSourceBlock().getType().equals(Material.AIR)) {
                    for (MarketPlace i : MarketPlaceManager.marketPlaces) {
                        for (MarketStand j : i.getMarketStands()) {
                            if (j.getArea().containsLocation(e.getSourceBlock().getLocation())) {
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent e){
        //Deactivates tnt in check areas
        for(MarketPlace k : MarketPlaceManager.marketPlaces){
            if(k.getCheckArea().containsLocation(e.getEntity().getLocation())){
                e.getEntity().getLocation().getWorld().playSound(e.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        //Makes that Items dont disappear
        if(e.getView().getTitle().contains("Trade page")){
            if(e.getInventory().getItem(13) != null){
                e.getPlayer().getInventory().addItem(e.getInventory().getItem(13));
            }
        }
        if(e.getView().getTitle().equalsIgnoreCase(ChatColor.GREEN+ "add trades")){
            if(e.getInventory().getItem(2) != null){
                e.getPlayer().getInventory().addItem(e.getInventory().getItem(2));
            }
        }
        if(e.getView().getTitle().equalsIgnoreCase(ChatColor.GREEN + "add trades")){
            if(e.getInventory().getItem(6) != null){
                e.getPlayer().getInventory().addItem(e.getInventory().getItem(6));
            }
        }
    }

}

