package de.minnivini.chestshop.listeners;

import de.minnivini.chestshop.ChestShop;
import de.minnivini.chestshop.Util.lang;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    @EventHandler
    public void onSignBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        if (!(block.getState() instanceof Sign)) return;
        if (ChestShop.getInstance().getItemFromShopConfig(player.getWorld().getName(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()) == null) return;
        removeShopSign(player, block, e);
    }

    @EventHandler
    public void onChestBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        Location chestLocation = block.getLocation();
        int[][] directions = {{0, 0, 1}, {0, 0, -1}, {1, 0, 0}, {-1, 0, 0}};

        for (int[] direction : directions) {
            Location signLocation = chestLocation.clone().add(direction[0], direction[1], direction[2]);
            Block signBlock = signLocation.getBlock();
            if (!(signBlock.getState() instanceof Sign)) continue;
            Sign sign = (Sign) signBlock.getState();
            if (!sign.getLine(0).equalsIgnoreCase("§a[Shop]") && !sign.getLine(0).equalsIgnoreCase("§a[Adminshop]")) continue;
            removeShopSign(player, signBlock, e);
        }
    }

    private void removeShopSign(Player player, Block block, BlockBreakEvent e) {
        Sign sign = (Sign) block.getState();
        String playerName = player.getName();
        int xCoord = block.getLocation().getBlockX();
        int yCoord = block.getLocation().getBlockY();
        int zCoord = block.getLocation().getBlockZ();
        String worldName = player.getWorld().getName();

        if (!player.hasPermission("chestshop.break") && !sign.getLine(2).equalsIgnoreCase(playerName)) {
            e.setCancelled(true);
            player.sendMessage(lang.getMessage("noShopBreakPerm"));
            return;
        }

        ChestShop.getInstance().removeItemFromShopConfig(worldName, xCoord, yCoord, zCoord);

        if (ChestShop.getInstance().getItemFromShopConfig(worldName, xCoord, yCoord, zCoord) == null) {
            player.sendMessage(lang.getMessage("shopRemove"));
        } else {
            e.setCancelled(true);
            player.sendMessage(lang.getMessage("shopRemoveErr"));
        }
    }
}
