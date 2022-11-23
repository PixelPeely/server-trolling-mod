package net.pixelpeely.stm;


import net.minecraft.entity.player.PlayerEntity;

import java.util.Random;

public class TrollHandler {

    interface TrollAction {
        void troll(PlayerEntity player);
    }

    private static final TrollAction[] trollActions = new TrollAction[] {
            (player) -> STMMain.LOGGER.info(player.getEntityName() + " is sus"),
            (player) -> STMMain.LOGGER.info(player.getEntityName() + " is sus but backwards"),
    };

    public static void ExecuteTroll(PlayerEntity player) {
        trollActions[new Random().ints(1, 0, trollActions.length).findFirst().getAsInt()].troll(player);
    }
}