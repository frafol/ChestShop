package de.minnivini.chestshop.Util;

public class util {

    public String Splt(String Splitter, String input) {
        String[] parts = input.split(Splitter);
        String itemName = null;
        if (parts.length == 2) {
            itemName = parts[0];
        }
        return itemName;
    }
}
