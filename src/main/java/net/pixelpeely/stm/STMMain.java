package net.pixelpeely.stm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.pixelpeely.stm.command.TrollCommand;
import net.pixelpeely.stm.config.ModConfigs;
import net.pixelpeely.stm.util.ServerTickEventHandler;
import net.pixelpeely.stm.util.TrollHandler;
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
		LOGGER.info(String.valueOf(TrollHandler.class));
		ModConfigs.registerConfigs();
		LOGGER.info("Targeting " + ModConfigs.targets.size() + " players at an interval of " + ModConfigs.cooldown + " seconds");
		LOGGER.info("There is a " + ModConfigs.chanceForAllNumerator + "/" + ModConfigs.chanceForAllDenominator + " chance for all trolls to be run at once and a " + ModConfigs.stackChanceNumerator + "/" + ModConfigs.stackChanceDenominator + " chance for trolls to stack");
		maxCooldown = ModConfigs.cooldown * 20;
		cooldown = maxCooldown;

		registerEvents();
		CommandRegistrationCallback.EVENT.register(TrollCommand::register);

		LOGGER.info("Server Trolling Mod Initialized.");
	}

	private void registerEvents() {
		//OnJoin: Add player to target list if name matches config
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			PlayerEntity player = handler.player;
			if (ModConfigs.targets.contains(player.getEntityName())){
				targets.add(player);
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((identifier, disconnect) -> {
			targets.remove(identifier.player);
		});

		ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, alive) -> {
			if (targets.contains(oldPlayer)){
				targets.remove(oldPlayer);
				targets.add(newPlayer);
			}
		}));

		ServerTickEventHandler.registerEventExecution();

		//OnServerTick: Count down, if the cooldown is at 0 execute a troll
		ServerTickEvents.START_SERVER_TICK.register((world) -> {
			if (cooldown == 0){
				targets.forEach((target) -> TrollHandler.executeRandomTroll(target, true));
				cooldown = maxCooldown;
			}
			else
				cooldown -= 1;
		});

		ServerWorldEvents.LOAD.register(((server, world) -> TrollHandler.world = world));
	}
}