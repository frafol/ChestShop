package de.minnivini.chestshop.commands;

import de.minnivini.chestshop.ChestShop;
import de.minnivini.chestshop.Util.lang;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockIterator;

public class ShopInfo implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player player = (Player) commandSender;
        int maxDistance = 100;
        Block block = getTargetBlock(player, maxDistance);
        if (block == null || !(block.getState() instanceof Sign)) {
            player.sendMessage(lang.getMessage("lookatSign"));
            return false;
        }

        int xCoord = block.getLocation().getBlockX();
        int yCoord = block.getLocation().getBlockY();
        int zCoord = block.getLocation().getBlockZ();
        String worldName = player.getWorld().getName();

        // Controlla se esiste un negozio nel blocco specificato
        String item = ChestShop.getInstance().getItemFromShopConfig(worldName, xCoord, yCoord, zCoord);
        if (item == null) {
            player.sendMessage(lang.getMessage("lookatSign"));
            return false;
        }

        if (!player.hasPermission("chestshop.shopinfo")) {
            player.sendMessage(lang.getMessage("noPermission"));
            return false;
        }

        player.sendMessage("§dShopinfo:");
        player.sendMessage("    §b-Item: §e" + item.toLowerCase());

        ItemStack itemStack = ChestShop.getInstance().getNBT(item);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) {
            return false;
        }

        if (meta.hasDisplayName()) {
            player.sendMessage("    §b-displayname: §e" + meta.getDisplayName());
        }

        if (meta.hasLore()) {
            player.sendMessage("    §b-lore: §e" + meta.getLore());
        }

        if (meta.hasEnchants()) {
            player.sendMessage("    §b-enchants:");
            meta.getEnchants().forEach((enchant, level) ->
                    player.sendMessage("        §e-" + enchant.getKey().getKey().toLowerCase() + " " + level));
        }

        if (meta.hasLocalizedName()) {
            player.sendMessage("    §b-localizedname: §e" + meta.getLocalizedName());
        }

        if (meta.getItemFlags() != null && !meta.getItemFlags().isEmpty()) {
            player.sendMessage("    §b-itemFlags:");
            meta.getItemFlags().forEach(flag ->
                    player.sendMessage("        §e-" + flag.name()));
        }

        if (meta.hasAttributeModifiers()) {
            player.sendMessage("    §b-attributes:");
            meta.getAttributeModifiers().forEach((attribute, modifier) ->
                    player.sendMessage("        §e-" + attribute.getKey().getKey() + " " + modifier));
        }

        return false;
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
