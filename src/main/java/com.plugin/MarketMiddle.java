package com.plugin;

import org.bukkit.Location;
import org.bukkit.Material;

public class MarketMiddle {

    private final Cuboid area;
    private final MarketPlace belongingMarketPlace;

    public MarketMiddle(MarketPlace belongingMarketPlace, boolean configLoad){
        //Sets the Blocks and makes the Cuboid of the MarketPlace, aka MarketMiddle
        //nimmt die Middle von dem MarketPlace als die Mitte zum berechnen des Cuboids
        this.belongingMarketPlace = belongingMarketPlace;
        this.area = newMarketMiddleArea();

        //setzt die Blöcke
        if(!configLoad){
            setAreaBlocks();
        }
    }

    public Cuboid newMarketMiddleArea(){
        return new Cuboid(new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX()-6, belongingMarketPlace.getMiddle().getY(), belongingMarketPlace.getMiddle().getZ()-6),
                new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX()+6, belongingMarketPlace.getMiddle().getY()+5, belongingMarketPlace.getMiddle().getZ()+6));
    }
    public void setAreaBlocks(){
        int zPosition = 0;
        for(int i = 0; i<(13*13);i++){
            if(i/13d %1 == 0 && i != 0){
                zPosition = zPosition+1;
            }
            new Location(belongingMarketPlace.getMiddle().getWorld(),
                    area.getxPos1()+(i%13),
                    area.getyPos1()+0,
                    area.getzPos1()+zPosition).getBlock().setType(Material.STONE_BRICKS);
        }

        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX(), belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX(), belongingMarketPlace.getMiddle().getY()+2, belongingMarketPlace.getMiddle().getZ()).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX(), belongingMarketPlace.getMiddle().getY()+3, belongingMarketPlace.getMiddle().getZ()).getBlock().setType(Material.SEA_LANTERN);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX(), belongingMarketPlace.getMiddle().getY()+4, belongingMarketPlace.getMiddle().getZ()).getBlock().setType(Material.WATER);

        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX()+2, belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX()-2, belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX(), belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()-2).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX(), belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()+2).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX()+2, belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()+1).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX()+2, belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()-1).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX()-2, belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()-1).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX()-2, belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()+1).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX()-1, belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()+2).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX()+1, belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()+2).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX()-1, belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()-2).getBlock().setType(Material.STONE_BRICKS);
        new Location(belongingMarketPlace.getMiddle().getWorld(), belongingMarketPlace.getMiddle().getX()+1, belongingMarketPlace.getMiddle().getY()+1, belongingMarketPlace.getMiddle().getZ()-2).getBlock().setType(Material.STONE_BRICKS);

    }
    public Cuboid getArea(){
        return area;
    }

}
