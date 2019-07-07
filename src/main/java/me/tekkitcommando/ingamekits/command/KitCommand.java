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
import org.bukkit.inventory.meta.ItemMeta;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

                            // Check if uses are up
                            if (plugin.getKits().contains("kits." + subcommand + ".puses")) {
                                if (plugin.getRestrictions().contains("players." + player.getUniqueId().toString() + "." + subcommand + ".uses")) {
                                    int uses = plugin.getKits().getInt("kits." + subcommand + ".puses");
                                    int usesUsed = plugin.getRestrictions().getInt("players." + player.getUniqueId().toString() + "." + subcommand + ".uses");

                                    if (usesUsed >= uses) {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getString("messages.noUsesLeft")));
                                        return true;
                                    } else {
                                        plugin.getRestrictions().set("players." + player.getUniqueId().toString() + "." + subcommand + ".uses", usesUsed + 1);
                                    }
                                } else {
                                    plugin.getRestrictions().set("players." + player.getUniqueId().toString() + "." + subcommand + ".uses", 1);
                                }
                            } else if (plugin.getKits().contains("kits." + subcommand + ".guses")) {
                                // Check global uses
                                if (plugin.getRestrictions().contains("kits." + subcommand + ".uses")) {
                                    int uses = plugin.getKits().getInt("kits." + subcommand + ".guses");
                                    int usedUses = plugin.getRestrictions().getInt("kits." + subcommand + ".uses");

                                    if (usedUses >= uses) {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getString("messages.noUsesLeft")));
                                        return true;
                                    } else {
                                        plugin.getRestrictions().set("kits." + subcommand + ".uses", usedUses + 1);
                                    }
                                } else {
                                    plugin.getRestrictions().set("kits." + subcommand + ".uses", 1);
                                }
                            } else if (plugin.getKits().contains("kits." + subcommand + ".time")) {
                                // Check time
                                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
                                DateTime dateTimeNow = new DateTime();
                                DateTime dateTimeFormatted = formatter.parseDateTime(dateTimeNow.toString());

                                if (plugin.getRestrictions().contains("players." + player.getUniqueId().toString() + "." + subcommand + ".time")) {
                                    String dateTimeUsedString = plugin.getRestrictions().getString("players." + player.getUniqueId().toString() + "." + subcommand + ".time");
                                    DateTime dateTimeUsed = formatter.parseDateTime(dateTimeUsedString);

                                    if (Hours.hoursBetween(dateTimeUsed, dateTimeFormatted).getHours() < plugin.getKits().getInt("kits." + subcommand + ".time")) {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getString("messages.stillOnCooldown")));
                                        return true;
                                    } else {
                                        // Record receive time
                                        plugin.getRestrictions().set("players." + player.getUniqueId().toString() + "." + subcommand + ".time", dateTimeFormatted);
                                    }
                                } else {
                                    // Record receive time
                                    plugin.getRestrictions().set("players." + player.getUniqueId().toString() + "." + subcommand + ".time", dateTimeFormatted);
                                }
                            }

                            // Give kit to player
                            Inventory inv = player.getInventory();

                            for (Material mat : Material.values()) {
                                if (plugin.getKits().contains("kits." + subcommand + ".items." + mat.toString())) {
                                    int amount = plugin.getKits().getInt("kits." + subcommand + ".items." + mat.toString() + ".amount");
                                    ItemStack item = new ItemStack(mat, amount);
                                    ItemMeta meta = item.getItemMeta();

                                    if (plugin.getKits().contains("kits." + subcommand + ".items." + mat.toString() + ".name")) {
                                        Objects.requireNonNull(meta).setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getKits().getString("kits." + subcommand + ".items." + mat.toString() + ".name")));
                                    }

                                    if (plugin.getKits().contains("kits." + subcommand + ".items." + mat.toString() + ".lore")) {
                                        List<String> loreItems = plugin.getKits().getStringList("kits." + subcommand + ".items." + mat.toString() + ".lore");
                                        List<String> newList = new ArrayList<>();

                                        for (String loreItem : loreItems) {
                                            loreItem = ChatColor.translateAlternateColorCodes('&', loreItem);
                                            newList.add(loreItem);
                                        }

                                        Objects.requireNonNull(meta).setLore(newList);
                                    }

                                    item.setItemMeta(meta);

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
                } else {
                    String subcommand = args[0];
                    String kitName = args[1];
                    String usesOrTime;

                    if (subcommand.equalsIgnoreCase("create")) {
                        if (player.hasPermission("kits.create")) {
                            if (!(plugin.getKits().contains("kits." + kitName))) {
                                if (args.length > 2) {
                                    usesOrTime = args[2];
                                    if (usesOrTime.startsWith("time:")) {
                                        plugin.getKits().set("kits." + kitName + ".time", usesOrTime.split(":")[1]);
                                    } else if (usesOrTime.startsWith("puses:")) {
                                        plugin.getKits().set("kits." + kitName + ".puses", usesOrTime.split(":")[1]);
                                    } else if (usesOrTime.startsWith("guses:")) {
                                        plugin.getKits().set("kits." + kitName + ".guses", usesOrTime.split(":")[1]);
                                    } else {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getString("messages.invalidUseRestriction")));
                                    }
                                }

                                String itemName;

                                for (ItemStack armor : player.getInventory().getArmorContents()) {
                                    if (armor != null) {
                                        itemName = armor.getType().toString();
                                        saveCustomMeta(armor, kitName, itemName);
                                    } else {
                                        continue;
                                    }

                                    plugin.getKits().set("kits." + kitName + ".items." + itemName + ".amount", 1);
                                    plugin.getKits().set("kits." + kitName + ".items." + itemName + ".wearing", true);
                                }

                                for (ItemStack item : player.getInventory().getContents()) {
                                    if (item != null) {
                                        itemName = item.getType().toString();
                                        saveCustomMeta(item, kitName, itemName);
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

    private void saveCustomMeta(ItemStack item, String kitName, String itemName) {
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) {
                String customName = item.getItemMeta().getDisplayName();
                plugin.getKits().set("kits." + kitName + ".items." + itemName + ".name", customName.replace("§", "&"));
            }

            if (item.getItemMeta().hasLore()) {
                List<String> lores = item.getItemMeta().getLore();
                List<String> newList = new ArrayList<>();

                for (String loreItem : lores) {
                    loreItem = loreItem.replace("§", "&");
                    newList.add(loreItem);
                }

                plugin.getKits().set("kits." + kitName + ".items." + itemName + ".lore", newList);
            }
        }
    }
}
