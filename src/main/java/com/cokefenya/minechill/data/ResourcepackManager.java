package com.cokefenya.minechill.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ResourcepackManager {
    private final int MAX_NAMES_PER_SENDER;
    public final File dataFolder;
    public final Gson gson;
    public final File packJsonFile;
    public final List<ResourcepackInfo.PackInfo> resourcepacks;

    private final FileConfiguration config;

    public ResourcepackManager(File dataFolder, FileConfiguration config) {
        this.dataFolder = dataFolder;
        File packConfigFolder = new File(dataFolder, "PackConfig"); // Папка PackConfig
        if (!packConfigFolder.exists()) {
            packConfigFolder.mkdir();
        }
        File packJsonFile = new File(packConfigFolder, "Pack.json"); // Файл Pack.json внутри PackConfig
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.packJsonFile = packJsonFile;
        this.config = config;
        this.resourcepacks = loadPackInfo();
        this.MAX_NAMES_PER_SENDER = config.getInt("settings.max-names-per-sender", 10);

    }


    public void createResourcepackFile(CommandSender sender, String name, String description, String url) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.player-only-command")));
            return;
        }

        Player player = (Player) sender;
        String senderName = player.getName();
        List<ResourcepackInfo.PackInfo> packsBySender = this.getPacksBySender(senderName);
        if (packsBySender.size() >= MAX_NAMES_PER_SENDER) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.max-packs-reached")));
            return;
        }

        String lowerCaseName = name.toLowerCase();
        for (ResourcepackInfo.PackInfo existingPack : this.resourcepacks) {
            if (existingPack.getName().equalsIgnoreCase(lowerCaseName)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.package-exists")));
                return;
            }
        }

        String capitalizedDisplayName = capitalizeFirstLetter(name);
        ResourcepackInfo.PackInfo packInfo = new ResourcepackInfo.PackInfo(capitalizedDisplayName, sender.getName(), url, description);
        this.addPackInfo(packInfo);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.package-created").replace("%name%", capitalizedDisplayName).replace("%url%", url).replace("%description%", description)));}

    public void setResourcepack(Player player, String name) {
        ResourcepackInfo.PackInfo packInfo = getPackInfoByNameIgnoreCase(name);
        if (packInfo != null) {
            player.setResourcePack(packInfo.getUrl());
        }
    }

    private ResourcepackInfo.PackInfo getPackInfoByNameIgnoreCase(String name) {
        for (ResourcepackInfo.PackInfo packInfo : resourcepacks) {
            if (packInfo.getName().equalsIgnoreCase(name)) {
                return packInfo;
            }
        }
        return null;
    }


    public boolean hasResourcepack(String name) {
        return getPackInfo(name) != null;
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public int getPackCountByPlayer(Player player) {
        int count = 0;
        for (ResourcepackInfo.PackInfo packInfo : resourcepacks) {
            if (packInfo.getSenderName().equalsIgnoreCase(player.getName())) {
                count++;
            }
        }
        return count;
    }
    public ResourcepackInfo.PackInfo getPackInfo(String name) {
        for (ResourcepackInfo.PackInfo packInfo : resourcepacks) {
            if (packInfo != null && packInfo.getName() != null && packInfo.getName().equals(name)) {
                return packInfo;
            }
        }
        return null;
    }

    public void deletePackInfo(ResourcepackInfo.PackInfo packInfo) {
        resourcepacks.remove(packInfo);
        savePackInfo();
    }


    public List<ResourcepackInfo.PackInfo> loadPackInfo() {
        if (packJsonFile.exists()) {
            try (FileReader reader = new FileReader(packJsonFile)) {
                ResourcepackInfo.PackInfo[] packArray = gson.fromJson(reader, ResourcepackInfo.PackInfo[].class);
                if (packArray != null) {
                    List<ResourcepackInfo.PackInfo> packList = new ArrayList<>();
                    for (ResourcepackInfo.PackInfo pack : packArray) {
                        packList.add(pack);
                    }
                    return packList;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public List<ResourcepackInfo.PackInfo> getPacksBySender(String senderName) {
        List<ResourcepackInfo.PackInfo> packsBySender = new ArrayList<>();
        for (ResourcepackInfo.PackInfo packInfo : resourcepacks) {
            if (packInfo.getSenderName().equals(senderName)) {
                packsBySender.add(packInfo);
            }
        }
        return packsBySender;
    }

    public void addPackInfo(ResourcepackInfo.PackInfo packInfo) {
        resourcepacks.add(packInfo);
        savePackInfo();
    }
    public List<String> listPacksBySender(String senderName) {
        List<String> packNames = new ArrayList<>();
        for (ResourcepackInfo.PackInfo packInfo : resourcepacks) {
            if (packInfo.getSenderName().equalsIgnoreCase(senderName)) {
                packNames.add(packInfo.getName());
            }
        }
        return packNames;
    }

    public void savePackInfo() {
        String jsonString = gson.toJson(resourcepacks);
        try (FileWriter writer = new FileWriter(packJsonFile)) {
            writer.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}