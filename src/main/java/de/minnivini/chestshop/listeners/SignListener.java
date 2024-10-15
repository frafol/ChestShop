package de.minnivini.chestshop.listeners;

import de.minnivini.chestshop.ChestShop;
import de.minnivini.chestshop.Util.lang;
import de.minnivini.chestshop.Util.util;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SignListener implements Listener {
    public String itemName;
    util util = new util();

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        String player = p.getName();

        if (!e.getBlock().getType().toString().endsWith("_WALL_SIGN")) return;

        Block attachedBlock = e.getBlock().getRelative(((org.bukkit.block.data.type.WallSign) e.getBlock().getBlockData()).getFacing().getOppositeFace());
        if (attachedBlock.getType() != Material.CHEST && attachedBlock.getType() != Material.TRAPPED_CHEST) return;

        BlockState state = attachedBlock.getState();
        if (!(state instanceof Chest)) return;

        List<String> WBlacklist = ChestShop.getInstance().getBlackWorlds();
        if (WBlacklist.contains(p.getWorld().getName())) {
            lang.getMessage("FalseWorld");
            return;
        }

        if (e.getLine(0).equalsIgnoreCase("[Shop]") && p.hasPermission("chestshop.create")) {

            String priceString = e.getLine(1);
            if (priceString == null || priceString.isEmpty() || !priceString.matches("\\d+(\\.\\d{1,2})?")) {
                p.sendMessage(lang.getMessage("invalidPrice"));
                e.setCancelled(true);
                return;
            }

            Chest chest = (Chest) state;
            ItemStack firstItem = chest.getBlockInventory().getContents()[0];
            if (firstItem == null) {
                return;
            }

            e.setLine(0, "§a[Shop]");
            e.setLine(1, priceString);
            e.setLine(2, player);

            if (firstItem.hasItemMeta() && ChestShop.getInstance().searchForItemStack(firstItem) != null) {
                itemName = ChestShop.getInstance().searchForItemStack(firstItem);
            } else {
                ChestShop.getInstance().addcurrentnumber(firstItem);
                itemName = firstItem.getType() + "#" + ChestShop.getInstance().curentID();
            }

            e.setLine(3, itemName);
            ChestShop.getInstance().addItemToShopConfig(p.getWorld().getName(), e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ(), itemName, p);
            itemName = util.Splt("#", itemName);
            p.sendMessage(lang.getMessage("ShopCreatewitch") + itemName.toLowerCase() + lang.getMessage("for") + priceString + lang.getMessage("sells"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null || !(clickedBlock.getState() instanceof Sign)) return;

        Sign sign = (Sign) clickedBlock.getState();
        if (!sign.getLine(0).equalsIgnoreCase("§a[Shop]")) return;

        e.setCancelled(true);
        Player player = e.getPlayer();
        int xCoord = clickedBlock.getLocation().getBlockX();
        int yCoord = clickedBlock.getLocation().getBlockY();
        int zCoord = clickedBlock.getLocation().getBlockZ();
        double Preis = Double.parseDouble(sign.getLine(1));
        int amount = player.isSneaking() ? 64 : 1;
        Preis *= player.isSneaking() ? 64 : 1;

        String materialName = ChestShop.getInstance().getItemFromShopConfig(player.getWorld().getName(), xCoord, yCoord, zCoord);
        String itemName = util.Splt("#", materialName);
        Material material = Material.matchMaterial(itemName);
        if (material == null || material == Material.AIR) {
            player.sendMessage(lang.getMessage("invalidMaterial"));
            return;
        }

        Block chestBlock = clickedBlock.getRelative(((org.bukkit.block.data.type.WallSign) clickedBlock.getBlockData()).getFacing().getOppositeFace());
        if (!(chestBlock.getState() instanceof Chest)) {
            player.sendMessage(lang.getMessage("noChestBehindSign"));
            return;
        }

        Chest chest = (Chest) chestBlock.getState();
        Inventory chestInventory = chest.getInventory();
        ItemStack item = new ItemStack(material, amount);
        int count = countItems(chestInventory, material);

        if (count < amount) {
            player.sendMessage(lang.getMessage("notEnough") + sign.getLine(3) + lang.getMessage("inChest"));
            return;
        }

        Economy economy = ChestShop.getEconomy();
        if (economy.getBalance(player) < Preis) {
            player.sendMessage(lang.getMessage("notEnoughMoney"));
            return;
        }

        double finalPreis = Preis;
        ChestShop.getInstance().getServer().getScheduler().runTaskAsynchronously(ChestShop.getInstance(), () -> {
            economy.withdrawPlayer(player, finalPreis);
            economy.depositPlayer(ChestShop.getInstance().getServer().getOfflinePlayer(sign.getLine(2)), finalPreis);
        });

        chestInventory.removeItem(item);
        player.getInventory().addItem(item);
        player.sendMessage(lang.getMessage("youHave") + amount + " " + sign.getLine(3) + lang.getMessage("for") + Preis + lang.getMessage("bought"));
    }

    private int countItems(Inventory inventory, Material material) {
        int count = 0;
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.getType() == material) count += stack.getAmount();
        }
        return count;
    }
}