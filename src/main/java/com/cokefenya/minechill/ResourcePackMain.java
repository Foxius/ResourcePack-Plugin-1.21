package com.cokefenya.minechill;

import com.cokefenya.minechill.command.ResourcePackSetCommand;
import com.cokefenya.minechill.command.ResourcepackCommand;
import com.cokefenya.minechill.data.BanWordsManager;
import com.cokefenya.minechill.data.ResourcepackManager;
import com.cokefenya.minechill.gui.ResourcepackGui;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public final class ResourcePackMain extends JavaPlugin implements Listener {
    public ResourcepackManager resourcepackManager;
    public ResourcepackCommand resourcepackCommand;
    public ResourcepackGui resourcepackGui;
    public static FileConfiguration config;
    public BanWordsManager banWordsManager;
    public ResourcePackMain() {
    }

    public void onEnable() {
        this.printStartupBanner();
        File dataFolder = this.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        config = this.getConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();

        this.banWordsManager = new BanWordsManager(this);
        File banWordsFile = new File(getDataFolder(), "banwords.json");
        if (!banWordsFile.exists()) {
            saveResource("banwords.json", false);
        }


        this.resourcepackManager = new ResourcepackManager(this.getDataFolder(), this.getConfig());
        this.resourcepackCommand = new ResourcepackCommand(this.resourcepackManager, this, this.getConfig(),banWordsManager);
        this.resourcepackGui = new ResourcepackGui(this.resourcepackManager, this, this.getConfig());
        Objects.requireNonNull(this.getCommand("rp")).setExecutor(this.resourcepackCommand);


    }

    public void printStartupBanner() {
        String yellow = "\u001b[33m";
        String resetColor = "\u001b[0m";
        String purple = "\u001b[35m";
        String cyan = "\u001b[36m";
        String redBold = "\u001b[31;1m";
        String green = "\u001b[32m";
        this.getLogger().info(yellow + " ===============================================" + resetColor);
        this.getLogger().info("           \u001b[33;1mMineChill-plugin Запустился!!" + resetColor);
        this.getLogger().info("              \u001b[35mBy " + purple + "CokeFenya \u001b[33mи " + cyan + "Fess_" + resetColor);
        this.getLogger().info("                  \u001b[31;1mVersion: 1.5" + resetColor);
        this.getLogger().info("               \u001b[31;1mПлагин был сделан:" + resetColor);
        this.getLogger().info("           " + green + "https://github.com/CokeFenya" + resetColor);
        this.getLogger().info(yellow + " ===============================================" + resetColor);
    }

    public void onDisable() {
        this.getLogger().info("MineChill-plugin был отключен эф!");
    }
}
