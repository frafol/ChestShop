package de.minnivini.chestshop.commands;

import de.minnivini.chestshop.GUIs.InfoGUI;
import de.minnivini.chestshop.Util.lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class csCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        InfoGUI InfoGUI = new InfoGUI();
        search search = new search();

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(lang.getMessage("PlayerOnly"));
            return true;
        }

        Player player = (Player) commandSender;
        if (args.length == 0) {
            player.sendMessage(lang.getMessage("noArr"));
            return false;
        }

        if (args[0].equalsIgnoreCase("info")) {
            InfoGUI.InfoGUI(commandSender);
            return false;
        }

        if (!args[0].equalsIgnoreCase("search") || !player.hasPermission("chestshop.search")) {
            player.sendMessage(lang.getMessage("noArr"));
            return false;
        }

        if (args.length > 1) {
            String material = args[1].toUpperCase();
            search.search(material, player);
            return false;
        }

        player.sendMessage(lang.getMessage("noArr"));
        return false;
    }
}

