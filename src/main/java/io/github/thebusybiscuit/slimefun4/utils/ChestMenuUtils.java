package io.github.thebusybiscuit.slimefun4.utils;

import io.github.thebusybiscuit.cscorelib2.chat.ChatColors;
import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.MenuClickHandler;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public final class ChestMenuUtils {

    private ChestMenuUtils() {
    }

    private static final ItemStack UI_BACKGROUND = new SlimefunItemStack("_UI_BACKGROUND", Material.GRAY_STAINED_GLASS_PANE, " ");
    private static final ItemStack BACK_BUTTON = new SlimefunItemStack("_UI_BACK", Material.ENCHANTED_BOOK, "&7\u21E6 返回", meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
    private static final ItemStack MENU_BUTTON = new SlimefunItemStack("_UI_MENU", Material.COMPARATOR, "&e设置 | 关于", "", "&7\u21E8 单击查看更多");
    private static final ItemStack SEARCH_BUTTON = new SlimefunItemStack("_UI_SEARCH", Material.NAME_TAG, "&b搜索");
    private static final ItemStack WIKI_BUTTON = new SlimefunItemStack("_UI_WIKI", Material.KNOWLEDGE_BOOK, "&3粘液科技 Wiki");

    private static final ItemStack PREV_BUTTON_ACTIVE = new SlimefunItemStack("_UI_PREVIOUS_ACTIVE", Material.LIME_STAINED_GLASS_PANE, "&r\u21E6 上一页");
    private static final ItemStack NEXT_BUTTON_ACTIVE = new SlimefunItemStack("_UI_NEXT_ACTIVE", Material.LIME_STAINED_GLASS_PANE, "&r下一页 \u21E8");
    private static final ItemStack PREV_BUTTON_INACTIVE = new SlimefunItemStack("_UI_PREVIOUS_INACTIVE", Material.BLACK_STAINED_GLASS_PANE, "&8\u21E6 上一页");
    private static final ItemStack NEXT_BUTTON_INACTIVE = new SlimefunItemStack("_UI_NEXT_INACTIVE", Material.BLACK_STAINED_GLASS_PANE, "&8下一页 \u21E8");

    private static final MenuClickHandler CLICK_HANDLER = (p, s, i, a) -> false;

    public static ItemStack getBackground() {
        return UI_BACKGROUND;
    }

    public static MenuClickHandler getEmptyClickHandler() {
        return CLICK_HANDLER;
    }

    public static ItemStack getBackButton(Player p, String... lore) {
        return new CustomItem(BACK_BUTTON, "&7\u21E6 " + SlimefunPlugin.getLocalization().getMessage(p, "guide.back.title"), lore);
    }

    public static ItemStack getMenuButton(Player p) {
        return new CustomItem(MENU_BUTTON, ChatColor.YELLOW + SlimefunPlugin.getLocalization().getMessage(p, "guide.title.settings"), "", "&7\u21E8 " + SlimefunPlugin.getLocalization().getMessage(p, "guide.tooltips.open-category"));
    }

    public static ItemStack getSearchButton(Player p) {
        return new CustomItem(SEARCH_BUTTON, meta -> {
            meta.setDisplayName(ChatColors.color(SlimefunPlugin.getLocalization().getMessage(p, "guide.search.name")));

            List<String> lore = Arrays.asList("", ChatColor.GRAY + "\u21E8 " + SlimefunPlugin.getLocalization().getMessage(p, "guide.search.tooltip"));
            lore.replaceAll(ChatColors::color);
            meta.setLore(lore);
        });
    }

    public static ItemStack getWikiButton() {
        return WIKI_BUTTON;
    }

    public static ItemStack getPreviousButton(Player p, int page, int pages) {
        if (pages == 1 || page == 1) {
            return new CustomItem(PREV_BUTTON_INACTIVE, meta -> {
                meta.setDisplayName(ChatColor.DARK_GRAY + "\u21E6 " + SlimefunPlugin.getLocalization().getMessage(p, "guide.pages.previous"));
                meta.setLore(Arrays.asList("", ChatColor.GRAY + "(" + page + " / " + pages + ")"));
            });
        }

        return new CustomItem(PREV_BUTTON_ACTIVE, meta -> {
            meta.setDisplayName(ChatColor.WHITE + "\u21E6 " + SlimefunPlugin.getLocalization().getMessage(p, "guide.pages.previous"));
            meta.setLore(Arrays.asList("", ChatColor.GRAY + "(" + page + " / " + pages + ")"));
        });
    }

    public static ItemStack getNextButton(Player p, int page, int pages) {
        if (pages == 1 || page == pages) {
            return new CustomItem(NEXT_BUTTON_INACTIVE, meta -> {
                meta.setDisplayName(ChatColor.DARK_GRAY + SlimefunPlugin.getLocalization().getMessage(p, "guide.pages.next") + " \u21E8");
                meta.setLore(Arrays.asList("", ChatColor.GRAY + "(" + page + " / " + pages + ")"));
            });
        }

        return new CustomItem(NEXT_BUTTON_ACTIVE, meta -> {
            meta.setDisplayName(ChatColor.WHITE + SlimefunPlugin.getLocalization().getMessage(p, "guide.pages.next") + " \u21E8");
            meta.setLore(Arrays.asList("", ChatColor.GRAY + "(" + page + " / " + pages + ")"));
        });
    }

    public static void drawBackground(ChestMenu menu, int... slots) {
        for (int slot : slots) {
            menu.addItem(slot, getBackground(), getEmptyClickHandler());
        }
    }

    public static void updateProgressbar(ChestMenu menu, int slot, int timeLeft, int time, ItemStack indicator) {

        Inventory inv = menu.toInventory();

        // We don't need to update the progress bar if noone is watching :o
        if (inv == null || inv.getViewers().isEmpty()) {
            return;
        }

        ItemStack item = indicator.clone();
        ItemMeta im = item.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        if (im instanceof Damageable) {
            ((Damageable) im).setDamage(getDurability(item, timeLeft, time));
        }

        im.setDisplayName(" ");
        im.setLore(Arrays.asList(getProgressBar(timeLeft, time), "", ChatColor.GRAY + "剩余 " + NumberUtils.getTimeLeft(timeLeft / 2)));
        item.setItemMeta(im);

        menu.replaceExistingItem(slot, item);
    }

    public static String getProgressBar(int time, int total) {
        StringBuilder builder = new StringBuilder();
        float percentage = Math.round(((((total - time) * 100.0F) / total) * 100.0F) / 100.0F);

        builder.append(NumberUtils.getColorFromPercentage(percentage));

        int rest = 20;
        for (int i = (int) percentage; i >= 5; i = i - 5) {
            builder.append(':');
            rest--;
        }

        builder.append("&7");

        for (int i = 0; i < rest; i++) {
            builder.append(':');
        }

        builder.append(" - ").append(percentage).append('%');
        return ChatColors.color(builder.toString());
    }


    private static short getDurability(ItemStack item, int timeLeft, int max) {
        return (short) ((item.getType().getMaxDurability() / max) * timeLeft);
    }

}
