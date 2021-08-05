package com.plugin;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.*;

public class MarketStand {

    //A MarketStand with Trades and a Inventory

    private final UUID Owner;
    private final Cuboid area;
    private final MarketPlace belongingMarketPlace;
    private final Location marketStandLocation;
    private Location portLocation;
    private final ArrayList<Trade> trades;
    private Inventory tradeInventory;
    private boolean active;
    private final int standPosition;
    private ArmorStand armorStand;

    public MarketStand(UUID Owner, MarketPlace belongingMarketPlace, boolean configLoad){
        belongingMarketPlace.addMarketStand(this);
        this.Owner = Owner;
        this.belongingMarketPlace = belongingMarketPlace;
        this.trades = new ArrayList<>();

        //Berechnet die Location des neuen MarketStands
        this.marketStandLocation = newMarketStandMiddle();
        standPosition = belongingMarketPlace.getMarketStands().size()%8;

        //berechnet die Area
        this.area = newMarketStandArea();
        if(!configLoad){
            for(Player i : Bukkit.getOnlinePlayers()){
                if(this.area.containsLocation(i.getLocation())){
                    Bukkit.getPlayer(Owner).sendMessage(ChatColor.RED + "You can't build a new MarketStand if someone is in the area of the MarketStand!");
                    belongingMarketPlace.removeMarketStand(this);
                    return;
                }
            }

        }

        //setzt die Blöcke
        if(!configLoad){
            setAreaBlocks();
        }

        //berechnet ob/den Pathway
        if(MarketPlaceManager.newPathWayNeeded(belongingMarketPlace)){
            new MarketPathWay(belongingMarketPlace, configLoad);
        }

        //Trade-Schild machen
        setTradeSign();
        setArmorStand();
        if(!configLoad){
            Bukkit.getPlayer(Owner).sendMessage(ChatColor.GREEN + "You built a new MarketStand!");
            Bukkit.getPlayer(Owner).playSound(Bukkit.getPlayer(Owner).getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1, 1);
        }


        //spawn ein Hologramm in dem Stand, auf dem der Owner steht

        this.tradeInventory = Bukkit.createInventory(null, 54, "trade Inventory");

        active = true;
    }

    public static Inventory fillWithItem(Inventory inv, ItemStack filler, ArrayList<Integer> list) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null && !list.contains(i)) {
                inv.setItem(i, filler);
            }
        }

        return inv;
    }

    public static Inventory fillWithItem(Inventory inv, ItemStack filler) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, filler);
            }
        }

        return inv;
    }


    private Location newMarketStandMiddle(){
        //Berechnet die Mitte des 5x5 MarketStands
        if(belongingMarketPlace.getMarketStands().size() % 8 == 1){
            return new Location(belongingMarketPlace.getMiddle().getWorld(),
                    belongingMarketPlace.getMiddle().getX()+4+((belongingMarketPlace.getMarketStands().size()/8+1)*5),
                    belongingMarketPlace.getMiddle().getY()+0,
                    belongingMarketPlace.getMiddle().getZ()+4);
        } else if(belongingMarketPlace.getMarketStands().size() %8 == 2){
            return new Location(belongingMarketPlace.getMiddle().getWorld(),
                    belongingMarketPlace.getMiddle().getX()+4+((belongingMarketPlace.getMarketStands().size()/8+1)*5),
                    belongingMarketPlace.getMiddle().getY()+0,
                    belongingMarketPlace.getMiddle().getZ()-4);
        }else if(belongingMarketPlace.getMarketStands().size() %8 == 3){
            return new Location(belongingMarketPlace.getMiddle().getWorld(),
                    belongingMarketPlace.getMiddle().getX()+4,
                    belongingMarketPlace.getMiddle().getY()+0,
                    belongingMarketPlace.getMiddle().getZ()-4+((belongingMarketPlace.getMarketStands().size()/8+1)*-5));
        }else if(belongingMarketPlace.getMarketStands().size() %8 == 4){
            return new Location(belongingMarketPlace.getMiddle().getWorld(),
                    belongingMarketPlace.getMiddle().getX()-4,
                    belongingMarketPlace.getMiddle().getY()+0,
                    belongingMarketPlace.getMiddle().getZ()-4+((belongingMarketPlace.getMarketStands().size()/8+1)*-5));
        }else if(belongingMarketPlace.getMarketStands().size() %8 == 5){
            return new Location(belongingMarketPlace.getMiddle().getWorld(),
                    belongingMarketPlace.getMiddle().getX()-4+((belongingMarketPlace.getMarketStands().size()/8+1)*-5),
                    belongingMarketPlace.getMiddle().getY()+0,
                    belongingMarketPlace.getMiddle().getZ()-4);
        }else if(belongingMarketPlace.getMarketStands().size() %8 == 6){
            return new Location(belongingMarketPlace.getMiddle().getWorld(),
                    belongingMarketPlace.getMiddle().getX()-4+((belongingMarketPlace.getMarketStands().size()/8+1)*-5),
                    belongingMarketPlace.getMiddle().getY()+0,
                    belongingMarketPlace.getMiddle().getZ()+4);
        }else if(belongingMarketPlace.getMarketStands().size() %8 == 7){
            return new Location(belongingMarketPlace.getMiddle().getWorld(),
                    belongingMarketPlace.getMiddle().getX()-4,
                    belongingMarketPlace.getMiddle().getY()+0,
                    belongingMarketPlace.getMiddle().getZ()+4+((belongingMarketPlace.getMarketStands().size()/8+1)*5));
        }else if(belongingMarketPlace.getMarketStands().size() %8 == 0){
            return new Location(belongingMarketPlace.getMiddle().getWorld(),
                    belongingMarketPlace.getMiddle().getX()+4,
                    belongingMarketPlace.getMiddle().getY()+0,
                    belongingMarketPlace.getMiddle().getZ()+4+((belongingMarketPlace.getMarketStands().size()/8)*5));
        }


        return null;
    }
    private Cuboid newMarketStandArea(){
        //berechnet die Area mit der Location als Mitte
        return new Cuboid(new Location(marketStandLocation.getWorld(), marketStandLocation.getX()-2d, marketStandLocation.getY(), marketStandLocation.getZ()-2d), new Location(marketStandLocation.getWorld(), marketStandLocation.getX()+2d, marketStandLocation.getY()+5d, marketStandLocation.getZ()+2d));
    }
    private void setAreaBlocks(){
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1(), area.getyPos1(), area.getzPos1()).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+1, area.getyPos1(), area.getzPos1()).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+2, area.getyPos1(), area.getzPos1()).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+3, area.getyPos1(), area.getzPos1()).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+4, area.getyPos1(), area.getzPos1()).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1(), area.getyPos1(), area.getzPos1()+1).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1(), area.getyPos1(), area.getzPos1()+2).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1(), area.getyPos1(), area.getzPos1()+3).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1(), area.getyPos1(), area.getzPos1()+4).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+4, area.getyPos1(), area.getzPos1()+1).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+4, area.getyPos1(), area.getzPos1()+2).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+4, area.getyPos1(), area.getzPos1()+3).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+4, area.getyPos1(), area.getzPos1()+4).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+1, area.getyPos1(), area.getzPos1()+4).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+2, area.getyPos1(), area.getzPos1()+4).getBlock().setType(Material.RED_WOOL);
        new Location(belongingMarketPlace.getMiddle().getWorld(), area.getxPos1()+3, area.getyPos1(), area.getzPos1()+4).getBlock().setType(Material.RED_WOOL);
    }
    public void setArmorStand(){
        if(armorStand != null){
            if(!armorStand.isDead()) armorStand.remove();
        }
        ArmorStand hologram = (ArmorStand) marketStandLocation.getWorld().spawnEntity(new Location(marketStandLocation.getWorld(), marketStandLocation.getX()+0.5, marketStandLocation.getY()+2, marketStandLocation.getZ()+0.5), EntityType.ARMOR_STAND);
        hologram.setVisible(false);
        hologram.setGravity(false);
        hologram.setInvulnerable(true);
        hologram.setCustomNameVisible(true);
        hologram.setPersistent(false);
        hologram.setSmall(true);
        hologram.setCustomName(ChatColor.GREEN+ "MarketStand of " + ChatColor.GREEN + Bukkit.getOfflinePlayer(Owner).getName());
        armorStand = hologram;
    }
    public void setTradeSign(){
        switch(standPosition){
            case 1:
            case 6:
                Location loc = new Location(marketStandLocation.getWorld(), marketStandLocation.getX(), marketStandLocation.getY()+1, marketStandLocation.getZ()-3);
                loc.getBlock().setType(Material.OAK_WALL_SIGN);
                WallSign sign = (WallSign) loc.getBlock().getBlockData();
                sign.setFacing(BlockFace.NORTH);
                Sign sign2 = (Sign) loc.getBlock().getState();
                sign2.setLine(1, ChatColor.GREEN+ "Trade!");
                sign2.setBlockData(sign);
                sign2.update();
                portLocation = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()-1);
                break;
            case 2:
            case 5:
                Location loc2 = new Location(marketStandLocation.getWorld(), marketStandLocation.getX(), marketStandLocation.getY()+1, marketStandLocation.getZ()+3);
                loc2.getBlock().setType(Material.OAK_WALL_SIGN);
                WallSign sign3 = (WallSign) loc2.getBlock().getBlockData();
                sign3.setFacing(BlockFace.SOUTH);
                Sign sign4 = (Sign) loc2.getBlock().getState();
                sign4.setLine(1, ChatColor.GREEN+ "Trade!");
                sign4.setBlockData(sign3);
                sign4.update();
                portLocation = new Location(loc2.getWorld(), loc2.getX(), loc2.getY(), loc2.getZ()+1);
                break;
            case 3:
            case 0:
                Location loc3 = new Location(marketStandLocation.getWorld(), marketStandLocation.getX()-3, marketStandLocation.getY()+1, marketStandLocation.getZ());
                loc3.getBlock().setType(Material.OAK_WALL_SIGN);
                WallSign sign5 = (WallSign) loc3.getBlock().getBlockData();
                sign5.setFacing(BlockFace.WEST);
                Sign sign6 = (Sign) loc3.getBlock().getState();
                sign6.setLine(1, ChatColor.GREEN+ "Trade!");
                sign6.setBlockData(sign5);
                sign6.update();
                portLocation = new Location(loc3.getWorld(), loc3.getX()-1, loc3.getY(), loc3.getZ());
                break;
            case 4:
            case 7:
                Location loc4 = new Location(marketStandLocation.getWorld(), marketStandLocation.getX()+3, marketStandLocation.getY()+1, marketStandLocation.getZ());
                loc4.getBlock().setType(Material.OAK_WALL_SIGN);
                WallSign sign7 = (WallSign) loc4.getBlock().getBlockData();
                sign7.setFacing(BlockFace.EAST);
                Sign sign8 = (Sign) loc4.getBlock().getState();
                sign8.setLine(1, ChatColor.GREEN+ "Trade!");
                sign8.setBlockData(sign7);
                sign8.update();
                portLocation = new Location(loc4.getWorld(), loc4.getX()+1, loc4.getY(), loc4.getZ());
                break;
        }
        }

    //Trade functions
    public void openTradeInventory(Player p, int page){
        //Trade Inventory mit den Trades
          //Seite-1 und dann in der Liste den Trade nehmen, gui erstellen und für den Spieler öffnen


        Inventory gui = Bukkit.createInventory(null, 27, "Trade page " + page);

        ItemStack trade = new ItemStack(Material.GREEN_DYE);
        ItemMeta tradeMeta = trade.getItemMeta();
        tradeMeta.setDisplayName(ChatColor.GREEN + "Trade!");
        trade.setItemMeta(tradeMeta);
        gui.setItem(22, trade);

        ItemStack nextPage = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) nextPage.getItemMeta();
        if(page != 1){
            meta.setDisplayName(ChatColor.GREEN + "last Page");
            meta.setBasePotionData(new PotionData(PotionType.POISON));
        }else{
            meta.setDisplayName(ChatColor.RED + "no last Page");
            meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
        }
        meta.setLore(new ArrayList<>());
        meta.setLocalizedName(Integer.toString(page));
        nextPage.setItemMeta(meta);
        gui.setItem(18, nextPage);


        ItemStack lastPage = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta2 = (PotionMeta) nextPage.getItemMeta();
        if(page != trades.size()){
            meta2.setDisplayName(ChatColor.GREEN + "next Page");
            meta2.setBasePotionData(new PotionData(PotionType.POISON));
        }else{
            meta2.setDisplayName(ChatColor.RED + "no next Page");
            meta2.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
        }
        meta2.setLore(new ArrayList<>());
        meta2.setLocalizedName(Integer.toString(page));
        lastPage.setItemMeta(meta2);
        gui.setItem(26, lastPage);

        gui.setItem(2, trades.get(page-1).getNeededItem());
        gui.setItem(6, trades.get(page-1).getTradeItem());

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta4 = arrow.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+ "to");
        arrow.setItemMeta(meta4);
        gui.setItem(4, arrow);

        ArrayList<Integer> emptySlots = new ArrayList<>();
        emptySlots.add(13);
        ItemStack fillItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta fillItemMeta = fillItem.getItemMeta();
        fillItemMeta.setDisplayName(ChatColor.GRAY + "filler");
        fillItem.setItemMeta(fillItemMeta);
        Inventory gui2 = fillWithItem(gui, fillItem, emptySlots);

        p.openInventory(gui2);

    }

    public void openTradeInventoryOwner(){
        //Trades hinzufügen/löschen

        //Inventar mit 2 Items öffnen:
            Inventory gui = Bukkit.createInventory(null, 27, "Configure Trades");

        //Remove Trades(Barrier)
          //Trade aus der Liste für Trades und die für Inventare entfernen
            ItemStack barrier = new ItemStack(Material.BARRIER);
            ItemMeta meta = barrier.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "Remove trades");
            barrier.setItemMeta(meta);

        //Add Trade (Grüne Farbe)
          //addTrade mit den 2 ItemStacks
            ItemStack dye = new ItemStack(Material.GREEN_DYE);
            ItemMeta meta2 = dye.getItemMeta();
            meta2.setDisplayName(ChatColor.GREEN + "Add trades");
            dye.setItemMeta(meta2);

            ItemStack chest = new ItemStack(Material.CHEST_MINECART);
            ItemMeta meta3 = chest.getItemMeta();
            meta3.setDisplayName(ChatColor.GOLD + "Open trade Inventory");
            chest.setItemMeta(meta3);


            ItemStack activeItem;
            if(active){
                activeItem = new ItemStack(Material.GREEN_WOOL);
                ItemMeta meta4 = activeItem.getItemMeta();
                meta4.setDisplayName(ChatColor.RED + "Deactivate Trading!");
                activeItem.setItemMeta(meta4);
            }else{
                activeItem = new ItemStack(Material.RED_WOOL);
                ItemMeta meta4 = activeItem.getItemMeta();
                meta4.setDisplayName(ChatColor.GREEN + "Activate Trading!");
                activeItem.setItemMeta(meta4);
            }

            gui.setItem(26, activeItem);
            gui.setItem(13, chest);
            gui.setItem(11, barrier);
            gui.setItem(15, dye);

        ItemStack fillItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta fillItemMeta = fillItem.getItemMeta();
        fillItemMeta.setDisplayName(ChatColor.GRAY + "filler");
        fillItem.setItemMeta(fillItemMeta);

            Player player = Bukkit.getPlayer(Owner);


            player.openInventory(fillWithItem(gui, fillItem));
    }

    public void addTradesInventory(){
        //Add trades Inventory öffnen
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Add Trades");

        ItemStack create = new ItemStack(Material.GREEN_DYE);
        ItemMeta createMeta = create.getItemMeta();
        createMeta.setDisplayName(ChatColor.GREEN + "add trade");
        create.setItemMeta(createMeta);

        gui.setItem(22, create);

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta = arrow.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "to");
        arrow.setItemMeta(meta);
        gui.setItem(4, arrow);

        ArrayList<Integer> emptySlots = new ArrayList<>();
        emptySlots.add(2);
        emptySlots.add(6);
        ItemStack fillItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta fillItemMeta = fillItem.getItemMeta();
        fillItemMeta.setDisplayName(ChatColor.GRAY + "filler");
        fillItem.setItemMeta(fillItemMeta);
        Inventory gui2 = fillWithItem(gui, fillItem, emptySlots);
        Bukkit.getPlayer(Owner).openInventory(gui2);
    }

    public void removeTradesInventory(int page){
        //Remove Trades auf der Seite öffnen

        //den Trade für die Seite öffnen und in den Titel die Seite packen
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.RED + "Remove Trades page " + page);

        ItemStack nextPage = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) nextPage.getItemMeta();
        if(page != 1){
            meta.setDisplayName(ChatColor.GREEN + "last page");
            meta.setBasePotionData(new PotionData(PotionType.POISON));
        }else{
            meta.setDisplayName(ChatColor.RED + "no last page");
            meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
        }
        meta.setLore(new ArrayList<>());
        meta.setLocalizedName(Integer.toString(page));
        nextPage.setItemMeta(meta);
        gui.setItem(18, nextPage);


        ItemStack lastPage = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta2 = (PotionMeta) nextPage.getItemMeta();
        if(page != trades.size()){
            meta2.setDisplayName(ChatColor.GREEN + "next page");
            meta2.setBasePotionData(new PotionData(PotionType.POISON));
        }else {
            meta2.setDisplayName(ChatColor.RED + "no next page");
            meta2.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
        }
        meta2.setLocalizedName(Integer.toString(page));
        meta2.setLore(new ArrayList<>());
        lastPage.setItemMeta(meta2);
        gui.setItem(26, lastPage);

        gui.setItem(6, trades.get(page-1).getTradeItem());
        gui.setItem(2, trades.get(page-1).getNeededItem());

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta8 = arrow.getItemMeta();
        meta8.setDisplayName(ChatColor.GRAY + "to");
        arrow.setItemMeta(meta8);

        gui.setItem(4, arrow);



        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta meta3 = barrier.getItemMeta();
        meta3.setLocalizedName(Integer.toString(page));
        meta3.setDisplayName(ChatColor.RED + "remove trade");
        barrier.setItemMeta(meta3);
        gui.setItem(13, barrier);
        ItemStack fillItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta fillItemMeta = fillItem.getItemMeta();
        fillItemMeta.setDisplayName(ChatColor.GRAY + "filler");
        fillItem.setItemMeta(fillItemMeta);

        fillWithItem(gui, fillItem);
        Bukkit.getPlayer(Owner).openInventory(gui);


    }
    public void addTrade(ItemStack trade, ItemStack needed, boolean setLocalizedName){
        //Neuen Trade erstellen
        trades.add(new Trade(trade, needed, setLocalizedName));
    }

    public void InventoryClick(InventoryClickEvent e){
        //Checken welches Inventar es ist
        Player p = (Player) e.getWhoClicked();
        if(e.getView().getTitle().contains("Trade page ")){
            //Wenn es das normale TradeInventar ist
            //Wenn es ein grüner Pfeil ist
            if(e.getCurrentItem().getType().equals(Material.TIPPED_ARROW) && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "next page")){
                //Seite wechseln, Seite im Namen des Inventars checken
                if(e.getClickedInventory().getItem(13)!= null){
                    p.getInventory().addItem(e.getClickedInventory().getItem(13));
                }
                openTradeInventory(p, Integer.parseInt(e.getCurrentItem().getItemMeta().getLocalizedName())+1);
                e.setCancelled(true);
            }else if(e.getCurrentItem().getType().equals(Material.TIPPED_ARROW) && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "last page")){
                //Seite wechseln, Seite im Namen des Inventars checken
                if(e.getClickedInventory().getItem(13) != null){
                    p.getInventory().addItem(e.getClickedInventory().getItem(13));
                }
                openTradeInventory(p, Integer.parseInt(e.getCurrentItem().getItemMeta().getLocalizedName())-1);
                e.setCancelled(true);
            }else if(e.getCurrentItem().getType().equals(Material.GREEN_DYE) && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Trade!")){
                //Checken ob die ItemStacks die gleichen sind und ob die Chest genügend hat und dann die entfernen und bei der anderen die hinzufügen
                //Inventar schließen und die Items hinzufügen
                //dann die entfernen und bei der anderen die hinzufügen
               if(e.getClickedInventory().getItem(13) != null && e.getClickedInventory().getItem(2).getItemMeta().getLocalizedName().equals(e.getClickedInventory().getItem(13).toString())){
                   boolean tradeable = false;
                   for(int j = 0; j<54; j++) {
                       //idk
                       ItemStack i = tradeInventory.getItem(j);
                       if (i != null) {
                           //Must be changed when using localizedname
                           String itemMetaString;
                           if(i.getItemMeta().toString().equalsIgnoreCase("UNSPECIFIC_META:{meta-type=UNSPECIFIC}")){
                               itemMetaString = " ";

                               ItemStack stack = new ItemStack(e.getClickedInventory().getItem(6));
                               ItemMeta meta = stack.getItemMeta();
                               meta.setLocalizedName(null);
                               stack.setItemMeta(meta);

                               if (e.getClickedInventory().getItem(6).getItemMeta().getLocalizedName().contains(i.getType().toString()) && i.getAmount() >= e.getClickedInventory().getItem(6).getAmount() && meta.toString().equals(i.getItemMeta().toString())) {


                                   ItemStack addItem;
                                   if (itemMetaString.equalsIgnoreCase(" ")) {
                                       addItem = new ItemStack(e.getClickedInventory().getItem(6));
                                       ItemMeta addItemMeta = new ItemStack(e.getClickedInventory().getItem(5).getType()).getItemMeta();
                                       addItemMeta.setLocalizedName(null);
                                       addItem.setItemMeta(addItemMeta);
                                   }else{
                                       addItem = new ItemStack(e.getClickedInventory().getItem(6));
                                       ItemMeta addItemMeta = addItem.getItemMeta();
                                       addItemMeta.setLocalizedName(null);
                                       addItem.setItemMeta(addItemMeta);
                                   }


                                   p.getInventory().addItem(addItem);

                                   i.setAmount(i.getAmount()-e.getClickedInventory().getItem(6).getAmount());
                                   tradeInventory.setItem(j, i);
                                   tradeInventory.addItem(e.getClickedInventory().getItem(13));
                                   e.getClickedInventory().getItem(13).setAmount(0);
                                   tradeable = true;
                                   break;
                               }
                           }else{
                               itemMetaString = i.getItemMeta().toString();

                               if (e.getClickedInventory().getItem(6).getItemMeta().getLocalizedName().contains(i.getType().toString()) && i.getAmount() >= e.getClickedInventory().getItem(6).getAmount()&& e.getClickedInventory().getItem(6).getItemMeta().getLocalizedName().contains(itemMetaString)) {


                                   ItemStack addItem;
                                   if (itemMetaString.equalsIgnoreCase(" ")) {
                                       addItem = new ItemStack(e.getClickedInventory().getItem(6));
                                       ItemMeta addItemMeta = new ItemStack(e.getClickedInventory().getItem(5).getType()).getItemMeta();
                                       addItemMeta.setLocalizedName(null);
                                       addItem.setItemMeta(addItemMeta);
                                   }else{
                                       addItem = new ItemStack(e.getClickedInventory().getItem(6));
                                       ItemMeta addItemMeta = addItem.getItemMeta();
                                       addItemMeta.setLocalizedName(null);
                                       addItem.setItemMeta(addItemMeta);
                                   }


                                   p.getInventory().addItem(addItem);

                                   i.setAmount(i.getAmount()-e.getClickedInventory().getItem(6).getAmount());
                                   tradeInventory.setItem(j, i);
                                   tradeInventory.addItem(e.getClickedInventory().getItem(13));
                                   e.getClickedInventory().getItem(13).setAmount(0);
                                   tradeable = true;
                                   break;
                               }
                           }

                       }
                   }
                   if(tradeable){
                       p.sendMessage(ChatColor.GREEN +"Traded!");
                       p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                   }else{
                       p.sendMessage(ChatColor.RED + "This Stand doesnt have that trade available!");
                       p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);

                   }
               }else{
                    p.sendMessage(ChatColor.RED + "Please put the Item to trade in the empty slot!");
                   p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                }


                e.setCancelled(true);
            }else if(e.getCurrentItem().getType() == Material.WHITE_STAINED_GLASS_PANE && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GRAY  +"filler")){
                e.setCancelled(true);
            }else if(e.getRawSlot() == 6 || e.getRawSlot() == 2 || e.getRawSlot() == 26 || e.getRawSlot() == 18 || e.getRawSlot() == 4){
                e.setCancelled(true);
            }

        }else if(e.getView().getTitle().equalsIgnoreCase("Configure Trades")){
            //Wenn es die 1. Seite für den Owner ist
            if(e.getCurrentItem().getType().equals(Material.BARRIER)&& e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "remove trades")){
                //Wenn es der BarrierBlock ist das trade löschen Inv öffnen
                if(trades.size() != 0){
                    removeTradesInventory(1);
                }else{
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().sendMessage(ChatColor.RED + "You dont have any Trades yet!");
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                }
            }else if(e.getCurrentItem().getType().equals(Material.GREEN_DYE) && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "add trades")){
                //Wenn es der grüne Farbstoff ist, das Trade hinzufügen Inventar öffnen

                addTradesInventory();
            }else if(e.getCurrentItem().getType().equals(Material.CHEST_MINECART) && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "open trade inventory")){
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
                    @Override
                    public void run() {
                        p.openInventory(tradeInventory);
                    }
                }, 1);
            }else if(e.getCurrentItem().getType() == Material.RED_WOOL && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Activate Trading!")){
                active = true;
                Bukkit.getPlayer(Owner).closeInventory();
                Bukkit.getPlayer(Owner).sendMessage(ChatColor.GREEN +"Activated Trading!");
            }else if(e.getCurrentItem().getType() == Material.GREEN_WOOL && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Deactivate Trading!")){
                active = false;
                Bukkit.getPlayer(Owner).closeInventory();
                Bukkit.getPlayer(Owner).sendMessage(ChatColor.GREEN +"Deactivated Trading!");
            }

            e.setCancelled(true);

        }else if(e.getView().getTitle().contains(ChatColor.RED + "Remove Trades")){
            //Wenn es die RemoveTrade Seite für den Owner ist
            if(e.getCurrentItem().getType().equals(Material.TIPPED_ARROW) && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "last page")){
                removeTradesInventory(Integer.parseInt(e.getCurrentItem().getItemMeta().getLocalizedName())-1);
                //Seite checken und zu der davor gehen(Funktion)
            }else if(e.getCurrentItem().getType().equals(Material.TIPPED_ARROW) && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "next page")){
                removeTradesInventory(Integer.parseInt(e.getCurrentItem().getItemMeta().getLocalizedName())+1);
                //Seite checken und zu der danach gehen(Funktion)
            }else if(e.getCurrentItem().getType().equals(Material.BARRIER) && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "remove trade")){
                trades.remove(Integer.parseInt(e.getCurrentItem().getItemMeta().getLocalizedName())-1);
                p.closeInventory();
                p.sendMessage(ChatColor.GREEN + "Trade removed!");
                p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                //Trade löschen und Inventar schließen;
            }
            e.setCancelled(true);

        }else if(e.getView().getTitle().equals( ChatColor.GREEN + "Add Trades")){
            //Wenn es die AddTrade Seite für den Owner ist
            //Inventar mit einem ok Button und 2 Plätzen für die Trades
            if((e.getCurrentItem().getType().equals(Material.ARROW) && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "to")) ||
                    (e.getCurrentItem().getType().equals(Material.WHITE_STAINED_GLASS_PANE) && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GRAY + "filler"))||
                    (e.getCurrentItem().getType().equals(Material.GREEN_DYE) && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN+ "add trade"))){

                e.setCancelled(true);

                if(e.getCurrentItem().getType().equals(Material.GREEN_DYE) && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN+ "add trade")){
                    if(e.getClickedInventory().getItem(2) != null && e.getClickedInventory().getItem(6) != null){
                        e.getWhoClicked().closeInventory();
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1, 1);
                        e.getWhoClicked().sendMessage(ChatColor.GREEN + "Trade added!");
                        p.sendMessage(ChatColor.BOLD + "Do not forget to add Items to your trade inventory!");
                        p.sendMessage(ChatColor.BOLD + "Traded Items will appear in your trade inventory");

                        addTrade(e.getClickedInventory().getItem(6), e.getClickedInventory().getItem(2), true);
                    }
                }
            }

        }
    }

    //getters
    public Cuboid getArea(){
        return area;
    }
    public UUID getOwner(){
        return Owner;
    }
    public Location getMarketStandLocation(){
        return marketStandLocation;
    }
    public ArrayList<Trade> getTrades(){
        return trades;
    }
    public Location getPortLocation(){
        return portLocation;
    }
    public boolean getActive(){
        return active;
    }
    public int getStandPosition(){
        return standPosition;
    }
    public ItemStack[] getTradeInventory(){
        return tradeInventory.getContents();
    }

    public void addItemToTradeInventory(ItemStack t){
        tradeInventory.addItem(t);
    }


}
