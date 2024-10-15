package de.minnivini.chestshop.commands;

import de.minnivini.chestshop.ChestShop;
import de.minnivini.chestshop.GUIs.ShopSearchGUI;
import de.minnivini.chestshop.Util.lang;
import org.bukkit.entity.Player;

import java.util.List;

public class search {
    ShopSearchGUI shopSearchGUI = new ShopSearchGUI();
    
    public void search(String searchItem, Player p) {
        
        if (searchItem == null) {
            p.sendMessage(lang.getMessage("noArr"));
            return;
        }
        
        List<String> shops = ChestShop.getInstance().searchItemFromShopCongig(searchItem);
        if (shops == null) {
            p.sendMessage(lang.getMessage("noShop"));
            return;
        }
        
        shopSearchGUI.ShopSearch(p, shops, searchItem);
    }
}
