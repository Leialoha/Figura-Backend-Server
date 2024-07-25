package dev.leialoha.plugins.figuraserver;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import dev.leialoha.plugins.figuraserver.commands.FiguraWebCommand;
import dev.leialoha.plugins.figuraserver.web.ServerHandler;

public class FiguraServer extends JavaPlugin {

    public static FiguraServer INSTANCE;
    public final Logger LOGGER = getLogger();
    private ServerHandler SERVER_HANDLER;

    @Override
    public void onLoad() {
        INSTANCE = this;
        saveDefaultConfig();

        getCommand("figuraw").setExecutor(new FiguraWebCommand());
    }

    public void refreshConfig() {
        FiguraConfig.loadConfig(getConfig());
    }

    public void refreshServer() {
        SERVER_HANDLER.stopServer();
        // Create a new instance
        SERVER_HANDLER = new ServerHandler();
        SERVER_HANDLER.startServer();
    }

    @Override
    public void onEnable() {
        FiguraConfig.loadConfig(getConfig());
        SERVER_HANDLER = new ServerHandler();
        SERVER_HANDLER.startServer();
    }

    @Override
    public void onDisable() {
        SERVER_HANDLER.stopServer();
    }
}
