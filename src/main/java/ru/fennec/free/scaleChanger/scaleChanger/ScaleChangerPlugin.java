package ru.fennec.free.scaleChanger.scaleChanger;

import org.bukkit.plugin.java.JavaPlugin;
import ru.fennec.free.scaleChanger.scaleChanger.common.configs.ConfigManager;
import ru.fennec.free.scaleChanger.scaleChanger.handlers.database.configs.MainConfig;
import ru.fennec.free.scaleChanger.scaleChanger.handlers.listeners.ScaleChangerCommand;

public final class ScaleChangerPlugin extends JavaPlugin {

    private ConfigManager<MainConfig> mainConfigManager;

    @Override
    public void onEnable() {
        loadConfigs();
        registerCommand();
    }

    private void loadConfigs() {
        this.mainConfigManager = ConfigManager.create(this.getDataFolder().toPath(), "config.yml", MainConfig.class);
        this.mainConfigManager.reloadConfig(getLogger());
    }

    private void registerCommand() {
        new ScaleChangerCommand(this, mainConfigManager);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
