package com.spectrasonic.AmongUs.Traits;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;
import com.spectrasonic.AmongUs.Utils.MessageUtils;
import com.spectrasonic.AmongUs.Utils.SoundUtils;
import com.spectrasonic.AmongUs.Utils.PointsManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@TraitName("impostor")
public class ImpostorTrait extends Trait {
    private PointsManager pointsManager;
    private final Set<Player> playersWhoScored;

    public ImpostorTrait() {
        super("impostor");
        this.playersWhoScored = new HashSet<>();
    }

    public void setPointsManager(PointsManager pointsManager) {
        this.pointsManager = pointsManager;
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (event.getNPC() == this.getNPC()) {
            Player player = event.getClicker();
            if (!playersWhoScored.contains(player)) {
                SoundUtils.playerSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                pointsManager.addPoints(player, 1);
                MessageUtils.sendTitle(player,
                        "<green>¡Atrapaste al impostor!</green>",
                        "",
                        1, 2, 1);
                playersWhoScored.add(player);
            } else {
                MessageUtils.sendMessage(player, "<red>Ya has atrapado a un impostor!</red>");
            }
        }
    }

    public void resetScoring() {
        playersWhoScored.clear();
    }
}