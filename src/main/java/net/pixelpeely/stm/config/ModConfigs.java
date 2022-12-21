package net.pixelpeely.stm.config;

import com.mojang.datafixers.util.Pair;
import net.pixelpeely.stm.STMMain;

import java.util.Arrays;
import java.util.List;

public class ModConfigs {
    public static STMConfig CONFIG;
    private static ModConfigProvider configs;

    public static List<String> targets;
    public static List<String> listeners;
    public static int cooldown;
    public static int maxConcurrentTrolls;
    public static int chanceForAllNumerator;
    public static int chanceForAllDenominator;
    public static int stackChanceNumerator;
    public static int stackChanceDenominator;
    public static int stackChanceMax;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = STMConfig.of(STMMain.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("key.targets", "Player1, Player2"), "These are the players to be targeted, separate each by a comma and a space");
        configs.addKeyValuePair(new Pair<>("key.listeners", "Player1, Player2"), "Listeners are players who will be notified in a private chat message whenever a target is trolled, separate each by a comma and a space");
        configs.addKeyValuePair(new Pair<>("key.cooldown", 60), "How many seconds between each troll");
        configs.addKeyValuePair(new Pair<>("key.chance_for_all", "100/14406"), "Chance for all trolls to be run at once, expressed as a fraction");
        configs.addKeyValuePair(new Pair<>("key.stack_chance", "1/4"), "Chance for another troll to be run on top of the current one, expressed as a fraction (cascading is possible)");
        configs.addKeyValuePair(new Pair<>("key.max_stack", "10"), "How many trolls may stack (ignored by chance_for_all)");
        configs.addKeyValuePair(new Pair<>("key.max_concurrent_trolls", 1024), "How many trolls can be running for each hooked event");
    }

    private static void assignConfigs() {
        try {
            targets = Arrays.stream(CONFIG.getOrDefault("key.targets", ", ").replace(" ", "").split(",")).toList();
            listeners = Arrays.stream(CONFIG.getOrDefault("key.listeners", ", ").replace(" ", "").split(",")).toList();
            cooldown = Integer.parseInt(CONFIG.getOrDefault("key.cooldown", "60").replace(" ", ""));
            maxConcurrentTrolls = Integer.parseInt(CONFIG.getOrDefault("key.max_concurrent_trolls", "1024").replace(" ", ""));
            stackChanceMax = Integer.parseInt(CONFIG.getOrDefault("key.max_stack", "10").replace(" ", ""));

            String[] chanceForAll = CONFIG.getOrDefault("key.chance_for_all", "100/14406").replace(" ", "").split("/");
            chanceForAllNumerator = Integer.parseInt(chanceForAll[0]);
            chanceForAllDenominator = Integer.parseInt(chanceForAll[1]);
            String[] stackChance = CONFIG.getOrDefault("key.stack_chance", "1/4").replace(" ", "").split("/");
            stackChanceNumerator = Integer.parseInt(stackChance[0]);
            stackChanceDenominator = Integer.parseInt(stackChance[1]);

            STMMain.LOGGER.info("All " + configs.getConfigsList().size() + " server trolling mod properties have been set properly");
        }
        catch (Exception e) {
            STMMain.LOGGER.error("Unable to load server trolling mod properties from config! This is most likely caused by incorrect formatting (deleting the config file will revert it back to default and most likely fix this issue)");
            e.printStackTrace();
        }
    }
}