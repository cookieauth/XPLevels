package sh.solsk.xplevels;

import org.bukkit.plugin.java.JavaPlugin;

public final class XPLevels extends JavaPlugin {

    private static XPLevels xpLevels;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.xpLevels = this;

        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new XPLevelEvent(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static XPLevels getXpLevels() {
        return xpLevels;
    }
}
