package com.spectrasonic.AmongUs.Traits;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;
import com.spectrasonic.AmongUs.Utils.MessageUtils;
import com.spectrasonic.AmongUs.Utils.SoundUtils;
import org.bukkit.Sound;

@TraitName("impostor")
public class ImpostorTrait extends Trait {
    public ImpostorTrait() {
        super("impostor");
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (event.getNPC() == this.getNPC()) {
            // Correct player reference from event
            SoundUtils.playerSound(event.getClicker(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            MessageUtils.sendTitle(event.getClicker(),
                    "<green>¡Atrapaste al impostor!</green>",
                    "",
                    1, 2, 1);
        }
    }
}
