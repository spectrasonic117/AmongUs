package com.spectrasonic.AmongUs.Traits;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;
import com.spectrasonic.AmongUs.Utils.MessageUtils;

@TraitName("impostor")
public class ImpostorTrait extends Trait {
    public ImpostorTrait() {
        super("impostor");
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (event.getNPC() == this.getNPC()) {
            MessageUtils.sendTitle(event.getClicker(),
                    "<green>¡Atrapaste al impostor!</green>",
                    "",
                    5, 20, 5);
        }
    }
}
