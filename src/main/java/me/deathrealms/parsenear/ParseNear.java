package me.deathrealms.parsenear;

import com.google.common.primitives.Ints;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ParseNear extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "DeathRealms";
    }

    @Override
    public String getName() {
        return "ParseNear";
    }

    @Override
    public String getIdentifier() {
        return "parsenear";
    }

    @Override
    public String getVersion() {
        return "1.1.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        Player player = (Player) offlinePlayer;

        if (player == null) {
            return "";
        }

        String[] args = identifier.split("_");
        Integer radius = Ints.tryParse(args[0]);
        List<Player> nearbyEntities = player.getWorld().getPlayers();

        if (radius != null) {
            nearbyEntities = nearbyEntities.stream()
                    .filter(entity -> {
                        double distance = entity.getLocation().distance(player.getLocation());
                        return distance <= radius;
                    })
                    .collect(Collectors.toList());
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        OfflinePlayer near = nearbyEntities.stream()
                .filter(entity -> entity != player)
                .min(Comparator.comparingDouble(entity -> entity.getLocation().distanceSquared(player.getLocation())))
                .orElse(null);

        if (near == null) {
            return "";
        }

        return PlaceholderAPI.setPlaceholders(near, "%" + String.join("_", args) + "%");
    }
}
