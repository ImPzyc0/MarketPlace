package com.plugin;

import org.bukkit.Location;
import org.bukkit.Material;

public class MarketPathWay {

    private final Cuboid area;
    private final MarketPlace belongingMarketPlace;


    public MarketPathWay(MarketPlace belongingMarketPlace, boolean configLoad){
        //Makes the Blocks and Area of the Pathways
        this.belongingMarketPlace = belongingMarketPlace;
        this.area = newPathWayArea();
        belongingMarketPlace.addMarketPathway(this);

        //Making the pathway(neu machen)
        if(!configLoad){
            setAreaBlocks();
        }


    }

    public Cuboid newPathWayArea(){
        //berechnet den neuen pathway

        switch (belongingMarketPlace.getMarketStands().size()%8) {
            case 1:
                return new Cuboid(new Location(belongingMarketPlace.getMiddle().getWorld(),
                        belongingMarketPlace.getMiddle().getX() + (1 + (belongingMarketPlace.getMarketStands().size() / 8 + 1) * 5),
                        belongingMarketPlace.getMiddle().getY() + 0,
                        belongingMarketPlace.getMiddle().getZ() + 1),
                        new Location(belongingMarketPlace.getMiddle().getWorld(),
                                belongingMarketPlace.getMiddle().getX() + (2 + (belongingMarketPlace.getMarketStands().size() / 8 + 1) * 5) + 4,
                                belongingMarketPlace.getMiddle().getY() + 5,
                                belongingMarketPlace.getMiddle().getZ() - 1)
                );
            case 3:
                return new Cuboid(new Location(belongingMarketPlace.getMiddle().getWorld(),
                        belongingMarketPlace.getMiddle().getX() + 1,
                        belongingMarketPlace.getMiddle().getY() + 0,
                        belongingMarketPlace.getMiddle().getZ() + (-1 + (belongingMarketPlace.getMarketStands().size() / 8 + 1) * -5d)),
                        new Location(belongingMarketPlace.getMiddle().getWorld(),
                                belongingMarketPlace.getMiddle().getX() - 1,
                                belongingMarketPlace.getMiddle().getY() + 5,
                                belongingMarketPlace.getMiddle().getZ() + (-2 + (belongingMarketPlace.getMarketStands().size() / 8 + 1) * -5) - 4)
                );
            case 5:
                return new Cuboid(new Location(belongingMarketPlace.getMiddle().getWorld(),
                        belongingMarketPlace.getMiddle().getX() + (-1 + (belongingMarketPlace.getMarketStands().size() / 8 + 1) * -5),
                        belongingMarketPlace.getMiddle().getY() + 0,
                        belongingMarketPlace.getMiddle().getZ() + 1),
                        new Location(belongingMarketPlace.getMiddle().getWorld(),
                                belongingMarketPlace.getMiddle().getX() + (-2 + (belongingMarketPlace.getMarketStands().size() / 8 + 1) * -5) - 4,
                                belongingMarketPlace.getMiddle().getY() + 5,
                                belongingMarketPlace.getMiddle().getZ() - 1)
                );
            case 7:
                return new Cuboid(new Location(belongingMarketPlace.getMiddle().getWorld(),
                        belongingMarketPlace.getMiddle().getX() + 1,
                        belongingMarketPlace.getMiddle().getY() + 0,
                        belongingMarketPlace.getMiddle().getZ() + (1 + (belongingMarketPlace.getMarketStands().size() / 8 + 1) * 5d)),
                        new Location(belongingMarketPlace.getMiddle().getWorld(),
                                belongingMarketPlace.getMiddle().getX() - 1,
                                belongingMarketPlace.getMiddle().getY() + 5,
                                belongingMarketPlace.getMiddle().getZ() + (+2 + (belongingMarketPlace.getMarketStands().size() / 8 + 1) * 5d) + 4)
                );
            default:
                break;
        }
        return null;
    }
    public void setAreaBlocks(){
        switch(belongingMarketPlace.getMarketStands().size()%8){
            case 1:
                for(int i = 1; i<6; i++){
                    for(int j = 0;j<3; j++){
                        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+i, area.getyPos1()+0, area.getzPos1()+j).getBlock().setType(Material.COBBLESTONE);
                    }
                }
                break;
            case 3:
                for(int i = 0; i<5; i++){
                    for(int j = 0;j<3; j++){
                        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+j, area.getyPos1()+0, area.getzPos1()+i).getBlock().setType(Material.COBBLESTONE);
                    }
                }
                break;
            case 5:
                for(int i = 0; i<5; i++){
                    for(int j = 0;j<3; j++){
                        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+i, area.getyPos1()+0, area.getzPos1()+j).getBlock().setType(Material.COBBLESTONE);
                    }
                }
                break;
            case 7:
                for(int i = 1; i<6; i++){
                    for(int j = 0;j<3; j++){
                        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+j, area.getyPos1()+0, area.getzPos1()+i).getBlock().setType(Material.COBBLESTONE);
                    }
                }
                break;
        }
    }

    public Cuboid getArea(){ return area; }

}
