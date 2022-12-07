package net.pixelpeely.stm.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.pixelpeely.stm.STMMain;
import net.pixelpeely.stm.config.ModConfigs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerTickEventHandler {
    private static final HashMap<Integer, ServerTickEvents.StartTick> actions = new HashMap<>();
    private static final List<Integer> garbage = new ArrayList<>();

    public static int getAvailableSlotIndex() {
        for (int i = 0; i <= ModConfigs.maxConcurrentTrolls; i++)
            if (!actions.containsKey(i))
                return i;
        STMMain.LOGGER.info("Max concurrent troll limit reached!");
        return -1;
    }

    public static void registerTickEvent(int index, ServerTickEvents.StartTick listener) {
        if (index != -1)
            actions.put(index, listener);
    }

    public static void unregisterTickEvent(int index) {
        garbage.add(index);
    }

    public static void registerEventExecution(){
        ServerTickEvents.START_SERVER_TICK.register((world) -> {
            for (ServerTickEvents.StartTick action : actions.values())
                action.onStartTick(world);
            garbage.forEach(actions::remove);
            garbage.clear();
        });
    }
}