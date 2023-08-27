package com.cokefenya.minechill.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.cokefenya.minechill.data.ResourcepackInfo;
import com.cokefenya.minechill.data.ResourcepackManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("deprecation")
public class ResourcepackGui implements Listener {
    public ResourcepackManager manager;
    public Plugin plugin;
    public FileConfiguration config;
    public final String packDescriptionMessage;
    public final String packUrlMessage;
    public final String guiTitle;
    public final String grayGlassName;
    public final int guiSize;

    public ResourcepackGui(ResourcepackManager manager, Plugin plugin, FileConfiguration config) {
        this.manager = manager;
        this.plugin = plugin;
        this.config = config;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.guiTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("gui-settings.gui-title")));
        this.grayGlassName = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("gui-settings.free-slot-name")));
        this.packDescriptionMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("gui-settings.pack-description")));
        this.packUrlMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("gui-settings.pack-url")));
        this.guiSize = config.getInt("gui-settings.gui-size", 27);
    }

    public void openGui(Player player) {
        Inventory gui = Bukkit.createInventory((InventoryHolder)null, this.guiSize, this.guiTitle);

        int slot;
        for(slot = 0; slot < gui.getSize(); ++slot) {
            ItemStack grayGlass = this.createGrayGlassItem(this.grayGlassName);
            gui.setItem(slot, grayGlass);
        }

        slot = 0;

        for(Iterator<ResourcepackInfo.PackInfo> var7 = this.manager.getPacksBySender(player.getName()).iterator(); var7.hasNext(); ++slot) {
            ResourcepackInfo.PackInfo packInfo = var7.next();
            if (slot >= gui.getSize()) {
                break;
            }

            ItemStack book = this.createBookItem(packInfo.getName(), packInfo.getDescription(), packInfo.getUrl());
            gui.setItem(slot, book);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() == null && event.getView().getTitle().equals(this.guiTitle)) {
            event.setCancelled(true);
        }

    }

    private ItemStack createBookItem(String name, String description, String url) {
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE.toString() + name);
        List<String> lore = new ArrayList<>();
        lore.add(this.packDescriptionMessage.replace("%description%", description));
        lore.add(this.packUrlMessage.replace("%url%", this.getFileNameFromUrl(url)));
        meta.setLore(lore);
        book.setItemMeta(meta);
        return book;
    }

    private ItemStack createGrayGlassItem(String name) {
        ItemStack grayGlass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = grayGlass.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + name);
        grayGlass.setItemMeta(meta);
        return grayGlass;
    }

    private String getFileNameFromUrl(String url) {
        int lastSlashIndex = url.lastIndexOf(47);
        return lastSlashIndex != -1 && lastSlashIndex < url.length() - 1 ? url.substring(lastSlashIndex + 1) : url;
    }
}
