package me.tekkitcommando.ingamekits.command;

import me.tekkitcommando.ingamekits.IngameKits;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitCommand implements CommandExecutor {

    private IngameKits plugin;

    public KitCommand(IngameKits plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("kit")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must be a player to send that command!");
            } else {
                Player player = (Player) sender;

                if (args.length == 0) {
                    sendHelpMessage(player);
                } else if (args.length == 1) {
                    String subcommand = args[0];

                    if (plugin.getKits().contains("kits." + subcommand)) {
                        if (player.hasPermission("kits." + subcommand)) {
                            // Give kit to player
                            Inventory inv = player.getInventory();

                            for (Material mat : Material.values()) {
                                if (plugin.getKits().contains("kits." + subcommand + ".items." + mat.toString())) {
                                    int amount = plugin.getKits().getInt("kits." + subcommand + ".items." + mat.toString() + ".amount");
                                    ItemStack item = new ItemStack(mat, amount);

                                    if (item.getType().getId() > 297 && item.getType().getId() < 318 && plugin.getKits().getBoolean("kits." + subcommand + ".items." + mat.toString() + ".wearing")) {
                                        if (mat.toString().contains("HELMET")) {
                                            player.getInventory().setHelmet(item);
                                        } else if (mat.toString().contains("CHESTPLATE")) {
                                            player.getInventory().setChestplate(item);
                                        } else if (mat.toString().contains("LEGGINGS")) {
                                            player.getInventory().setLeggings(item);
                                        } else {
                                            player.getInventory().setBoots(item);
                                        }
                                    } else {
                                        inv.addItem(item);
                                    }
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getString("messages.kitGiveNotAllowed")));
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getString("messages.kitDoesntExist")));
                    }
                }else if (args.length == 2) {
                    String subcommand = args[0];
                    String kitName = args[1];

                    if (subcommand.equalsIgnoreCase("create")) {
                        if (player.hasPermission("kits.create")) {
                            if (!(plugin.getKits().contains("kits." + kitName))) {
                                String itemName;

                                for (ItemStack armor : player.getInventory().getArmorContents()) {
                                    if (armor != null) {
                                        itemName = armor.getType().toString();
                                    } else {
                                        continue;
                                    }

                                    plugin.getKits().set("kits." + kitName + ".items." + itemName + ".amount", 1);
                                    plugin.getKits().set("kits." + kitName + ".items." + itemName + ".wearing", true);
                                }

                                for (ItemStack item : player.getInventory().getContents()) {
                                    if (item != null) {
                                        itemName = item.getType().toString();
                                    } else {
                                        continue;
                                    }

                                    // 298 - 317 is armor
                                    if (item.getType().getId() > 297 && item.getType().getId() < 318) {
                                        plugin.getKits().set("kits." + kitName + ".items." + itemName + ".amount", 1);
                                        plugin.getKits().set("kits." + kitName + ".items." + itemName + ".wearing", false);
                                    } else {
                                        plugin.getKits().set("kits." + kitName + ".items." + itemName + ".amount", item.getAmount());
                                    }
                                }

                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getString("messages.kitCreated").replace("%kit%", kitName)));
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getString("messages.kitAlreadyExists")));
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getString("messages.kitCreationNotAllowed")));
                        }
                    } else {
                        sendHelpMessage(player);
                    }
                } else {
                    sendHelpMessage(player);
                }
            }
        }
        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getString("help.message")));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getString("help.command.receive")));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getString("help.command.create")));
    }
}
