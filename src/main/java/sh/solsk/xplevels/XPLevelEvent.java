package sh.solsk.xplevels;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class XPLevelEvent implements Listener {

    FileConfiguration configuration = XPLevels.getXpLevels().getConfig();

    @EventHandler
    public void onLevelEvent(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();

        if (!(event.getNewLevel() < event.getOldLevel())) {
            // Send the player a message if enabled.
            if (configuration.getBoolean("messages.chat.enabled"))
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', replace("chat", player, event)));

            // Send the player a title & subtitle if enabled.
            if (configuration.getBoolean("messages.titles.enabled")) {
                player.resetTitle();
                player.sendTitle(ChatColor.translateAlternateColorCodes('&', replace("title", player, event)),
                        ChatColor.translateAlternateColorCodes('&', replace("subtitle", player, event)),
                        configuration.getInt("messages.titles.fade-in"),
                        configuration.getInt("messages.titles.stay"),
                        configuration.getInt("messages.titles.fade-out"));
            }

            // Send a sound effect to the player if enabled.
            if (configuration.getBoolean("effects.sounds.enabled"))
                player.playSound(player.getLocation(), Sound.valueOf(configuration.getString("effects.sounds.name").toUpperCase()), 1, 1);

            // Send a particle effect to the player if enabled.
            if (configuration.getBoolean("effects.particles.enabled")) player.playEffect(player.getLocation(),
                    Effect.valueOf(configuration.getString("effects.particles.name").toUpperCase()), 10);

            if (configuration.getBoolean("commands.enabled")) {
                for (String command : configuration.getStringList("commands.list")) {
                    Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), command.replace("{player}", player.getName())
                            .replace("{displayplayer}", player.getDisplayName())
                            .replace("{new-level}", String.valueOf(event.getNewLevel()))
                            .replace("{prior-level}", String.valueOf(event.getOldLevel()))
                            .replace("{xp-to-level}", String.valueOf(player.getExpToLevel())));
                }
            }
        }
    }

    private String replace(String type, Player player, PlayerLevelChangeEvent event) {
        if (type.equalsIgnoreCase("chat")) {
            return configuration.getString("messages.chat.message")
                    .replace("{player}", player.getName())
                    .replace("{new-level}", String.valueOf(event.getNewLevel()))
                    .replace("{prior-level}", String.valueOf(event.getOldLevel()))
                    .replace("{xp-to-level}", String.valueOf(player.getExpToLevel()));
        } else if (type.equalsIgnoreCase("title")) {
            return configuration.getString("messages.titles.title")
                    .replace("{player}", player.getName())
                    .replace("{new-level}", String.valueOf(event.getNewLevel()))
                    .replace("{prior-level}", String.valueOf(event.getOldLevel()))
                    .replace("{xp-to-level}", String.valueOf(player.getExpToLevel()));
        } else if (type.equalsIgnoreCase("subtitle")) {
            return configuration.getString("messages.titles.subtitle")
                    .replace("{player}", player.getName())
                    .replace("{new-level}", String.valueOf(event.getNewLevel()))
                    .replace("{prior-level}", String.valueOf(event.getOldLevel()))
                    .replace("{xp-to-level}", String.valueOf(player.getExpToLevel()));
        }
        return "Ruh-roh! There was an error, if you are an administrator, please contact the developer immediately!";
    }
}
