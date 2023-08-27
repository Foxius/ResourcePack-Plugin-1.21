package com.cokefenya.minechill.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class ResourcePackSetCommand implements CommandExecutor {
    private final Plugin plugin;

    public ResourcePackSetCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.player-only-command"))));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("minechill.command.rps")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.no-permission"))));

            return true;
        }

        String resourcepackUrl = plugin.getConfig().getString("resourcepack-url");
        player.setResourcePack(Objects.requireNonNull(resourcepackUrl));

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.resourcepack-set"))));
        return true;
    }
}