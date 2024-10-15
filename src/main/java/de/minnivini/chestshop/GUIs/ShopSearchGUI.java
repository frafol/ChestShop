package de.minnivini.chestshop.GUIs;

import de.minnivini.chestshop.ChestShop;
import de.minnivini.chestshop.Util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopSearchGUI {
    
    public void ShopSearch(Player p, List<String> shops, String material) {
        
        Inventory inventory;
        ItemStack item1 = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build();
        ItemStack item2 = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).build();
        inventory = ChestShop.getInstance().getServer().createInventory(null, 27, "§bShop Search");

        inventory.setItem(0, item1);
        inventory.setItem(1, item1);
        inventory.setItem(2, item1);
        inventory.setItem(3, item1);
        inventory.setItem(4, item1);
        inventory.setItem(5, item1);
        inventory.setItem(6, item1);
        inventory.setItem(7, item1);
        inventory.setItem(8, item1);
        inventory.setItem(9, item1);

        inventory.setItem(10, item2);
        inventory.setItem(11, item2);
        inventory.setItem(12, item2);
        inventory.setItem(13, item2);
        inventory.setItem(14, item2);
        inventory.setItem(15, item2);
        inventory.setItem(16, item2);

        inventory.setItem(17, item1);
        inventory.setItem(18, item1);
        inventory.setItem(19, item1);
        inventory.setItem(20, item1);
        inventory.setItem(21, item1);
        inventory.setItem(22, item1);
        inventory.setItem(23, item1);
        inventory.setItem(24, item1);
        inventory.setItem(25, item1);
        inventory.setItem(26, item1);

        for (int i = 0; i < shops.size(); i++) {
            String Koordinaten = shops.get(i);
            String[] teile = Koordinaten.split("§");
            String world = teile[0];
            String x = teile[1];
            String y = teile[2];
            String z = teile[3];
            if (ChestShop.getInstance().IDCheck(material) != null) {
                ItemStack item = ChestShop.getInstance().getNBT(material);
                inventory.setItem(i + 10, new ItemBuilder(item.getType()).setDisplayname(ChatColor.RESET + x + " " + y + " " + z).setLocalizedName("shopTP").setLore(world).build());
            } else {
                inventory.setItem(i + 10, new ItemBuilder(Material.valueOf(material)).setDisplayname(ChatColor.RESET + x + " " + y + " " + z).setLocalizedName("shopTP").setLore(world).build());
            }
        }
        p.openInventory(inventory);
    }
}
