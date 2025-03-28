// GameManager.java actualizado
package com.spectrasonic.AmongUs.Managers;

import com.spectrasonic.AmongUs.Main;
import com.spectrasonic.AmongUs.Traits.ImpostorTrait;
import com.spectrasonic.AmongUs.Utils.MessageUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.CommandSender;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class GameManager {

    private final Main plugin;
    private final NPCRegistry npcRegistry;
    private final List<NPC> activeNPCs = new ArrayList<>();
    private final List<NPC> impostorNPCs = new ArrayList<>();
    private boolean gameRunning = false;
    private final Random random = new Random();

    public GameManager(Main plugin) {
        this.plugin = plugin;
        this.npcRegistry = CitizensAPI.getNPCRegistry();
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ImpostorTrait.class));
    }

    public void startGame(CommandSender sender) {
        if (gameRunning) {
            MessageUtils.sendMessage(sender, "<red>El juego ya está en ejecución!</red>");
            return;
        }

        // Reset scoring for all impostors
        impostorNPCs.forEach(npc -> {
            ImpostorTrait trait = npc.getTrait(ImpostorTrait.class);
            if (trait != null) {
                trait.resetScoring();
                trait.setPointsManager(plugin.getPointsManager());
            }
        });

        FileConfiguration config = plugin.getConfig();
        int npcCount = config.getInt("npc_spawns", 10);
        int impostorCount = config.getInt("impostor_spawn", 1);
        List<String> impostorNames = config.getStringList("impostor_names");

        Location pos1 = null;
        Location pos2 = null;

        if (config.contains("spawn_loc.pos1") && config.contains("spawn_loc.pos2")) {
            double x1 = config.getDouble("spawn_loc.pos1.x");
            double y1 = config.getDouble("spawn_loc.pos1.y");
            double z1 = config.getDouble("spawn_loc.pos1.z");

            double x2 = config.getDouble("spawn_loc.pos2.x");
            double y2 = config.getDouble("spawn_loc.pos2.y");
            double z2 = config.getDouble("spawn_loc.pos2.z");

            String worldName = config.getString("spawn_loc.world", "world");
            World world = plugin.getServer().getWorld(worldName);
            if (world == null)
                world = plugin.getServer().getWorlds().get(0);
            pos1 = new Location(world, x1, y1, z1);
            pos2 = new Location(world, x2, y2, z2);
        }

        if (pos1 == null || pos2 == null) {
            MessageUtils.sendMessage(sender, "<red>Configuración de ubicaciones inválida!</red>");
            return;
        }

        final Location finalPos1 = pos1;
        final Location finalPos2 = pos2;

        // Spawn normal NPCs
        for (int i = 0; i < npcCount; i++) {
            Location spawnLoc = getRandomLocation(pos1, pos2);
            NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "NPC_" + i);
            npc.spawn(spawnLoc);
            activeNPCs.add(npc);
        }

        // Spawn impostors
        for (int i = 0; i < impostorCount && i < impostorNames.size(); i++) {
            Location spawnLoc = getRandomLocation(pos1, pos2);
            NPC impostor = npcRegistry.createNPC(EntityType.PLAYER, impostorNames.get(i));
            impostor.spawn(spawnLoc);
            ImpostorTrait trait = new ImpostorTrait();
            trait.setPointsManager(plugin.getPointsManager()); // Set the PointsManager here
            impostor.addTrait(trait);
            activeNPCs.add(impostor);
            impostorNPCs.add(impostor);
        }

        gameRunning = true;
        MessageUtils.sendMessage(sender,
                String.format("<green>Juego iniciado con %d NPCs (%d impostores)!</green>", npcCount, impostorCount));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameRunning) {
                    cancel();
                    return;
                }

                for (NPC npc : activeNPCs) {
                    if (npc.isSpawned()) {
                        moveNPC(npc, finalPos1, finalPos2);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void stopGame(CommandSender sender) {
        if (!gameRunning) {
            MessageUtils.sendMessage(sender, "<red>El juego no está en ejecución!</red>");
            return;
        }

        activeNPCs.forEach(npc -> {
            if (npc.isSpawned())
                npc.despawn();
            npc.destroy();
        });
        activeNPCs.clear();
        impostorNPCs.clear();
        gameRunning = false;
        MessageUtils.sendMessage(sender, "<green>Juego detenido y todos los NPCs eliminados!</green>");
    }

    public void reloadConfig(CommandSender sender) {
        plugin.reloadConfig();
        MessageUtils.sendMessage(sender, "<green>Configuración recargada!</green>");
    }

    private Location getRandomLocation(Location pos1, Location pos2) {
        double minX = Math.min(pos1.getX(), pos2.getX());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        double x = minX + (maxX - minX) * random.nextDouble();
        double y = Math.min(pos1.getY(), pos2.getY());
        double z = minZ + (maxZ - minZ) * random.nextDouble();

        Location location = new Location(pos1.getWorld(), x, y, z);
        return ensureSafeLocation(location);
    }

    private Location ensureSafeLocation(Location location) {
        while (location.getBlock().getType().isSolid() &&
                location.getY() < location.getWorld().getMaxHeight() - 2) {
            location.add(0, 1, 0);
        }

        if (location.getBlock().getType().isSolid()) {
            location = location.subtract(0, 2, 0);
            while (location.getBlock().getType().isSolid() && location.getY() > 0) {
                location.subtract(0, 1, 0);
            }
        }

        return location;
    }

    private void moveNPC(NPC npc, Location pos1, Location pos2) {
        Location target = getRandomLocation(pos1, pos2);
        npc.getNavigator().setTarget(target);
    }

    public boolean isGameRunning() {
        return gameRunning;
    }
}
