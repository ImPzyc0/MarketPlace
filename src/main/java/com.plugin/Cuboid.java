package com.plugin;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Cuboid {

    private String world;
    private Vector minV, maxV;
    private double xPos1;
    private double zPos1;
    private double yPos1;
    private double xPos2;
    private double yPos2;
    private double zPos2;
    private Location min, max;

    public Cuboid(Location min, Location max){

        world = min.getWorld().getName();

        this.min = min;
        this.max = max;

        this.xPos1 = Math.min(min.getX(), max.getX());
        this.zPos1 = Math.min(min.getZ(), max.getZ());
        this.yPos1 = Math.min(min.getY(), max.getY());
        this.xPos2 = Math.max(min.getX(), max.getX());
        this.yPos2 = Math.max(min.getY(), max.getY());
        this.zPos2 = Math.max(min.getZ(), max.getZ());


        minV = new Vector(xPos1, yPos1, zPos1);
        maxV = new Vector(xPos2, yPos2, zPos2);
    }

    public boolean containsLocation(Location location){
        if(location.getWorld().getName().equalsIgnoreCase(world)){
            return location.toVector().isInAABB(minV, maxV);
        }

        return false;
    }
    public double getxPos1(){
        return xPos1;
     }
    public double getyPos1(){
        return yPos1;
    }
    public double getzPos1(){
        return zPos1;
    }

    public Location getMin(){
        return min;
    }
    public Location getMax(){
        return max;
    }

}
