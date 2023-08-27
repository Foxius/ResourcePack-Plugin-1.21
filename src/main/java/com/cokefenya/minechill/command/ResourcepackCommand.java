package com.cokefenya.minechill.command;

import java.util.*;

import com.cokefenya.minechill.data.BanWordsManager;
import com.cokefenya.minechill.data.ResourcepackInfo;
import com.cokefenya.minechill.data.ResourcepackManager;
import com.cokefenya.minechill.gui.ResourcepackGui;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class ResourcepackCommand implements CommandExecutor, TabCompleter {
    public ResourcepackManager manager;
    public BanWordsManager banWordsManager;
    public ResourcepackGui resourcepackGui;
    public FileConfiguration config;
    public Plugin plugin;

    public ResourcepackCommand(ResourcepackManager manager, Plugin plugin, FileConfiguration config, BanWordsManager banWordsManager) {
        this.manager = manager;
        this.banWordsManager = banWordsManager;
        this.resourcepackGui = new ResourcepackGui(manager, plugin, config);
        this.config = plugin.getConfig();
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length >= 1) {
            String subcommand = args[0].toLowerCase();
            String name;
            String newUrl;
            if (subcommand.equals("server")) {
                return new ResourcePackSetCommand(plugin).onCommand(sender, command, label, args);
            }
            if (subcommand.equals("create")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("minechill.command.rp.create")) {
                        if (args.length >= 4) {
                            name = args[1];
                            String description = args[2];
                            String url = args[3];
                            newUrl = name.toLowerCase();

                            // Check for banned words in name and description
                            if (banWordsManager.containsBannedWords(name) || banWordsManager.containsBannedWords(description)) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.banned-words-message"))));
                                return true;
                            }

                            if (this.manager.getPackInfo(newUrl) != null) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.package-exists")).replace("%name%", name)));
                                return true;
                            }

                            this.manager.createResourcepackFile(sender, name, description, url);
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.usage-create"))));
                        }
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.usage-create"))));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.player-only-command"))));
                }
            } else if (subcommand.equals("set")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("minechill.command.rp.set")) {
                        if (args.length >= 2) {
                            name = args[1];
                            if (this.manager.hasResourcepack(name)) {
                                this.manager.setResourcepack((Player)sender, name);
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(this.config.getString("messages.package-set")).replace("%name%", name))));
                            } else {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(this.config.getString("messages.package-not-found")).replace("%name%", name))));
                            }
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.usage-set"))));
                        }
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.no-permission"))));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.player-only-command"))));
                }
            } else {
                Player player;
                switch (subcommand) {
                    case "delete":
                        if (sender instanceof Player) {
                            if (sender.hasPermission("minechill.command.rp.delete")) {
                                if (args.length >= 2) {
                                    player = (Player) sender;
                                    name = args[1];
                                    ResourcepackInfo.PackInfo packInfo = this.manager.getPackInfo(name);
                                    if (packInfo != null && packInfo.getSenderName().equalsIgnoreCase(player.getName())) {
                                        this.manager.deletePackInfo(packInfo);
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(this.config.getString("messages.package-deleted")).replace("%name%", name))));
                                    } else {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(this.config.getString("messages.package-not-found")).replace("%name%", name))));
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.usage-delete"))));
                                }
                            } else {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.no-permission"))));
                            }
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.player-only-command"))));
                        }
                        break;
                    case "list":
                        if (sender instanceof Player) {
                            if (sender.hasPermission("minechill.command.rp.list")) {
                                player = (Player) sender;
                                this.resourcepackGui.openGui(player);
                            } else {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.no-permission"))));
                            }
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.player-only-command"))));
                        }

                        break;
                    case "edit":
                        if (sender instanceof Player) {
                            if (sender.hasPermission("minechill.command.rp.edit")) {
                                if (args.length >= 4) {
                                    name = args[1];
                                    String newDescription = args[2];
                                    newUrl = args[3];

                                    if (this.manager.hasResourcepack(name)) {
                                        ResourcepackInfo.PackInfo packInfo = this.manager.getPackInfo(name);
                                        packInfo.setDescription(newDescription);
                                        packInfo.setUrl(newUrl);
                                        this.manager.savePackInfo();


                                        this.config.set("resourcepacks." + name + ".description", newDescription);
                                        this.config.set("resourcepacks." + name + ".url", newUrl);
                                        this.plugin.saveConfig();

                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(this.config.getString("messages.package-edit")).replace("%name%", name))));
                                    } else {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(this.config.getString("messages.package-not-found")).replace("%name%", name))));
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.usage-edit"))));
                                }
                            } else {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.no-permission"))));
                            }
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.player-only-command"))));
                        }
                        break;
                    case "help":
                        if (sender.hasPermission("minechill.command.rp.help")) {
                            this.sendHelp(sender);
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.no-permission"))));
                        }
                        break;
                    default:
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.invalid-command"))));
                        break;
                }
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("messages.usage"))));
        }

        return true;
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> subcommands = Arrays.asList("create", "set", "delete", "list", "edit", "help", "clear");
            List<String> completions = new ArrayList<>();
            for (String subcommand : subcommands) {
                if (subcommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subcommand);
                }
            }
            return completions;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            return List.of("Name");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            return List.of("Description");
        } else if (args.length == 4 && args[0].equalsIgnoreCase("create")) {
            return List.of("Url");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            List<String> completions = new ArrayList<>();
            List<ResourcepackInfo.PackInfo> playerPacks = this.manager.getPacksBySender(sender.getName());
            for (ResourcepackInfo.PackInfo packInfo : playerPacks) {
                completions.add(packInfo.getName());
            }
            return completions;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("edit")) {
            List<String> completions = new ArrayList<>();
            List<String> playerPacks = this.manager.listPacksBySender(sender.getName());
            for (String packName : playerPacks) {
                if (packName.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(packName);
                }
            }
            return completions;
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("s"))) {
            List<String> completions = new ArrayList<>();
            List<String> playerPacks = this.manager.listPacksBySender(sender.getName());
            for (String packName : playerPacks) {
                if (packName.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(packName);
                }
            }
            return completions;
        } else {
            return null;
        }
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("help.help-title"))));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("help.create-command"))));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("help.set-command"))));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("help.delete-command"))));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("help.list-command"))));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("help.help-command"))));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(this.config.getString("help.help-footer"))));
    }
}
