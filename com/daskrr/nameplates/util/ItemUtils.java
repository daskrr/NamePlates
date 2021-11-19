package com.daskrr.nameplates.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {
    public static String getName(ItemStack itemStack) {
        if (itemStack == null)
            return "";

        if (itemStack.hasItemMeta())
            if (itemStack.getItemMeta().hasDisplayName())
                return itemStack.getItemMeta().getDisplayName();

        return StringUtils.capitalize(itemStack.getType().toString().replaceAll("_", "").toLowerCase());
    }
}
