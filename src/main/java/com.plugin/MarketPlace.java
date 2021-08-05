package com.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.UUID;

public class MarketPlace {
//Controls and saves all data of a MarketPlace

    private UUID Owner;
    private final Location middle;
    private final ArrayList<MarketStand> MarketStands;
    private final ArrayList<MarketPathWay> MarketPathways;
    private final MarketMiddle marketMiddle;
    private final Cuboid checkArea;

    public MarketPlace(Location middle, UUID Owner, boolean configLoad){
        if(!configLoad){
            Bukkit.getPlayer(Owner).teleport(new Location(middle.getWorld(), middle.getX()-3, middle.getY(), middle.getZ()));
        }
        if(!configLoad){
            this.middle = new Location(middle.getWorld(), middle.getBlockX(), middle.getBlockY()-1, middle.getBlockZ());
        }else{
            this.middle = middle;
        }

        this.Owner = Owner;
        this.MarketStands = new ArrayList<>();
        this.MarketPathways = new ArrayList<>();
        this.checkArea = new Cuboid(new Location(middle.getWorld(), middle.getX()+100, middle.getY()+10, middle.getZ()+100), new Location(middle.getWorld(), middle.getX()-100, middle.getY()-10, middle.getZ()-100));

        this.marketMiddle = new MarketMiddle(this, configLoad);
        if(!configLoad){
            Bukkit.getPlayer(Owner).sendMessage(ChatColor.GREEN + "You built a new MarketPlace!");
            Bukkit.getPlayer(Owner).playSound(Bukkit.getPlayer(Owner).getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1, 1);

            MarketPlaceManager.PlayersInMarketPlaces.put(Bukkit.getPlayer(Owner),this);
            Bukkit.getPlayer(Owner).sendTitle(ChatColor.GREEN + "You entered a", ChatColor.GREEN + "MarketPlace", 10, 50, 10);

        }
        MarketPlaceManager.marketPlaces.add(this);



    }

    //getters
    public ArrayList<MarketStand> getMarketStands(){
        return MarketStands;
    }
    public ArrayList<MarketPathWay> getMarketPathways(){
        return MarketPathways;
    }
    public MarketMiddle getMarketMiddle(){
        return marketMiddle;
    }
    public Location getMiddle(){
        return middle;
    }
    public UUID getOwner(){
        return Owner;
    }
    public Cuboid getCheckArea(){
        return checkArea;
    }

    //setters/adders
    public void addMarketPathway(MarketPathWay p){
        MarketPathways.add(p);
    }
    public void addMarketStand(MarketStand p){
        MarketStands.add(p);
    }
    public void setOwner(UUID p){
        this.Owner = p;
    }
    public void removeMarketStand(MarketStand stand){
        MarketStands.remove(stand);
    }

}
