package me.tekkitcommando.ingamekits;

import de.leonhard.storage.Json;
import de.leonhard.storage.Yaml;
import me.tekkitcommando.ingamekits.command.KitCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class IngameKits extends JavaPlugin {

    private Logger logger;
    private Yaml config = new Yaml("config", getDataFolder().getAbsolutePath());
    private Json kits = new Json("kits", getDataFolder().getAbsolutePath());

    @Override
    public void onDisable() {
        logger.info("[IngameKits] Disabled!");
    }

    @Override
    public void onEnable() {
        this.logger = getLogger();
        setup();
        setupCommands();
        logger.info("[IngameKits] Enabled!");
    }

    private void setup() {
        config.setDefault("messages.kitGiven", "&a[&bServer&a] &bYou have been given the %kit% kit.");
        config.setDefault("messages.kitGiveNotAllowed", "&a[&bServer&a] &cOops. &bYou are not allowed to use that kit.");
        config.setDefault("messages.kitCreated", "&a[&bServer&a] &bYou have created the %kit% kit.");
        config.setDefault("messages.kitCreationNotAllowed", "&a[&bServer&a] &cOops. &bIt looks like you don't have permission to create kits.");
        config.setDefault("messages.kitAlreadyExists", "&a[&bServer&a] &cOops. &bThat kit already exists.");
        config.setDefault("messages.kitDoesntExist", "&a[&bServer&a] &cOops. &bThat kit doesn't exist.");

        config.setDefault("help.message", "&a[&bServer&a] &bThese are the current kit commands:");
        config.setDefault("help.command.receive", "&b/kit <name>: receive the kit you typed the name of.");
        config.setDefault("help.command.create", "&b/kit create <name>: create a kit with all of the items in your inv and what you're wearing.");

        kits.setDefault("kits.example.items.stone_sword.amount", 1);
        kits.setDefault("kits.example.items.leather_chestplate.amount", 1);
        kits.setDefault("kits.example.items.leather_chestplate.wearing", true);
        kits.setDefault("kits.example.items.leather_boots.amount", 1);
        kits.setDefault("kits.example.items.leather_boots.wearing", true);
    }

    private void setupCommands() {
        getCommand("kit").setExecutor(new KitCommand(this));
    }

    public Yaml getPluginConfig() {
        return config;
    }

    public Json getKits() {
        return kits;
    }
}
