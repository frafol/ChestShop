package de.minnivini.chestshop.listeners;

import de.minnivini.chestshop.Util.lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class InvListener implements Listener {
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {

        if (e.getCurrentItem() == null) return;
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();

        if (title.equals("§bShop Search")) {
            e.setCancelled(true);
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            if (itemMeta == null || !itemMeta.hasLocalizedName()) return;
            if ("shopTP".equals(itemMeta.getLocalizedName())) {
                String koords = itemMeta.getDisplayName();
                List<String> lore = itemMeta.getLore();
                if (lore == null || lore.isEmpty()) {
                    p.sendMessage("Invalid world information.");
                    return;
                }

                String formattedWorld = lore.get(0);
                String[] parts = koords.split(" ");

                if (parts.length < 3) {
                    p.sendMessage("Invalid coordinates.");
                    return;
                }

                try {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    double z = Double.parseDouble(parts[2]);

                    if (p.hasPermission("chestshop.tp")) {
                        p.closeInventory();
                        Location tpLocation = new Location(Bukkit.getWorld(formattedWorld), x, y, z);
                        p.teleport(tpLocation);
                    } else {
                        p.sendMessage(lang.getMessage("noPermission"));
                    }
                } catch (NumberFormatException ex) {
                    p.sendMessage("Invalid coordinates format.");
                }
            }
            return;
        }

        if (title.equals("§bShop Info")) {
            e.setCancelled(true);
        }
    }
}
