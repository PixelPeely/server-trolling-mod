package net.pixelpeely.stm.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.atomic.AtomicInteger;

public class Utils {
    public static void scheduleSyncRepeatingTask(ServerTickEvents.StartTick action, int period, int maxTicks, ServerTickEvents.StartTick endAction) {
        int index = ServerTickEventHandler.getAvailableSlotIndex();
        AtomicInteger timer = new AtomicInteger(0);
        ServerTickEventHandler.registerTickEvent(index, (world) -> {
            timer.getAndIncrement();
            if (timer.get() % period == 0)
                action.onStartTick(world);
            if (timer.get() > maxTicks) {
                ServerTickEventHandler.unregisterTickEvent(index);
                if (endAction != null)
                    endAction.onStartTick(world);
            }
        });
    }

    public static Entity spawnEntity(ServerWorld world,EntityType entity, String name, BlockPos blockPos) {
        return entity.spawn(world, null, Text.literal(name), null, blockPos, null, true, false);
    }
}
