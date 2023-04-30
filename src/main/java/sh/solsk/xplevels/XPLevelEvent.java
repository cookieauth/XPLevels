package sh.solsk.xplevels;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
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
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class XPLevelEvent implements Listener {

    FileConfiguration configuration = XPLevels.getXpLevels().getConfig();

    @EventHandler
    public void onLevelEvent(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();

        if (!(event.getNewLevel() < event.getOldLevel())) {
            // Send the player a message if enabled.
            if (configuration.getBoolean("messages.chat.enabled")) sendMessage(player, player, event);

            // Send the player a title & subtitle if enabled.
            if (configuration.getBoolean("messages.titles.enabled")) showTitle(player, player, event);

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



    private void showTitle(final @NotNull Audience audience, Player player, PlayerLevelChangeEvent event) {
        audience.clearTitle();

        final Component mainTitle = MiniMessage.miniMessage().deserialize(replace("title", player, event)).decoration(TextDecoration.ITALIC, false);
        final Component subTitle = MiniMessage.miniMessage().deserialize(replace("subtitle", player, event)).decoration(TextDecoration.ITALIC, false);

        final @NotNull Title.Times times = Title.Times.times(Duration.ofMillis(configuration.getLong("messages.titles.fade-in")), Duration.ofMillis(configuration.getLong("messages.titles.stay")), Duration.ofMillis(configuration.getLong("messages.titles.fade-out")));

        Title title = Title.title(mainTitle, subTitle, times);

        audience.showTitle(title);
    }

    private void sendMessage(final @NotNull Audience audience, Player player, PlayerLevelChangeEvent event) {
        audience.sendMessage(MiniMessage.miniMessage().deserialize(replace("chat", player, event)).decoration(TextDecoration.ITALIC, false));
    }
}
