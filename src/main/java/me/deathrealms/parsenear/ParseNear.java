package me.deathrealms.parsenear;

import com.google.common.primitives.Ints;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String[] args = identifier.split("_");
        Integer radius = Ints.tryParse(args[0]);
        List<Entity> nearbyEntities = player.getWorld().getEntities();

        if (radius != null) {
            nearbyEntities = nearbyEntities.stream()
                    .filter(entity -> {
                        double distance = entity.getLocation().distance(player.getLocation());
                        return distance <= radius;
                    })
                    .collect(Collectors.toList());
            args = (String[]) ArrayUtils.remove(args, 0);
        }

        Player near = (Player) nearbyEntities.stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> entity != player)
                .filter(entity -> entity.getWorld() == player.getWorld())
                .min(Comparator.comparingDouble(entity -> entity.getLocation().distanceSquared(player.getLocation())))
                .orElse(null);

        if (near == null) return "";

        return PlaceholderAPI.setPlaceholders(near, "%" + String.join("_", args) + "%");
    }
}
