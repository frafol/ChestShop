package de.minnivini.chestshop.GUIs;

import de.minnivini.chestshop.ChestShop;
import de.minnivini.chestshop.Util.ItemBuilder;
import de.minnivini.chestshop.Util.lang;
import de.minnivini.chestshop.Util.util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

public class InfoGUI {

    public void InfoGUI(CommandSender commandSender) {
        Player player = (Player) commandSender;
        int maxDistance = 100;

        Block block = getTargetBlock(player, maxDistance);
        if (block == null || !(block.getState() instanceof Sign)) {
            player.sendMessage(lang.getMessage("lookatSign"));
            return;
        }

        int xCoord = block.getLocation().getBlockX();
        int yCoord = block.getLocation().getBlockY();
        int zCoord = block.getLocation().getBlockZ();
        String worldName = player.getWorld().getName();

        String item = ChestShop.getInstance().getItemFromShopConfig(worldName, xCoord, yCoord, zCoord);
        if (item == null) {
            player.sendMessage(lang.getMessage("lookatSign"));
            return;
        }

        if (!player.hasPermission("chestshop.shopinfo")) {
            player.sendMessage(lang.getMessage("noPermission"));
            return;
        }

        Sign sign = (Sign) block.getState();
        String seller = sign.getLine(2);
        String price = sign.getLine(1);

        if ("Air".equalsIgnoreCase(item)) {
            player.sendMessage(lang.getMessage("invalidMaterial"));
            return;
        }

        ItemStack itemStack = ChestShop.getInstance().IDCheck(item) != null
                ? ChestShop.getInstance().getNBT(item)
                : new ItemStack(Material.valueOf(item));

        Inventory inv = Bukkit.createInventory(null, 9, "§bShop Info");
        inv.setItem(2, itemStack);
        inv.setItem(6, new ItemBuilder(Material.PAPER)
                .setDisplayname("§dInfos: ")
                .setLore(ChatColor.WHITE + lang.getMessage("seller") + seller,
                        ChatColor.WHITE + lang.getMessage("Prize") + price)
                .build());

        player.openInventory(inv);
    }

    public Block getTargetBlock(Player player, int maxDistance) {
        BlockIterator iterator = new BlockIterator(player, maxDistance);

        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (!block.getType().isAir()) {
                return block;
            }
        }
        return null;
    }
}
