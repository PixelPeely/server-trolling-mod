package net.pixelpeely.stm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.pixelpeely.stm.config.ModConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class STMMain implements ModInitializer {
	public static final String MOD_ID = "stm";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final List<PlayerEntity> targets = new ArrayList<>();
	public static int maxCooldown;
	public static int cooldown;

	@Override
	public void onInitialize() {
		ModConfigs.registerConfigs();
		LOGGER.info("Targeting " + ModConfigs.targets.length + " players at an interval of " + ModConfigs.cooldown + " seconds");
		maxCooldown = ModConfigs.cooldown * 20;
		cooldown = maxCooldown;

		registerEvents();

		LOGGER.info("Server Trolling Mod Initialized.");
	}

	private void registerEvents() {
		//OnJoin: Add player to target list if name matches config
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			PlayerEntity player = handler.player;
			if (Arrays.stream(ModConfigs.targets).toList().contains(player.getEntityName()))
				targets.add(player);
		});

		//OnServerTick: Count down, if the cooldown is at 0 execute a troll
		ServerTickEvents.START_SERVER_TICK.register((world) -> {
			if (cooldown == 0){
				targets.forEach(TrollHandler::ExecuteTroll);
				cooldown = maxCooldown;
			}
			else
				cooldown -= 1;
		});
	}
}