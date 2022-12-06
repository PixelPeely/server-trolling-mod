package net.pixelpeely.stm.config;

import com.mojang.datafixers.util.Pair;
import net.pixelpeely.stm.STMMain;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static String[] targets;
    public static int cooldown;
    public static int maxConcurrentTrolls;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(STMMain.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("key.targets", "Player1, Player2"), "These are the players to be targeted, separate each by a comma and a space");
        configs.addKeyValuePair(new Pair<>("key.cooldown", 60), "How many seconds between each troll");
        configs.addKeyValuePair(new Pair<>("key.max_concurrent_trolls", 1024), "How many trolls can be running for each hooked event");
    }

    private static void assignConfigs() {
        targets = CONFIG.getOrDefault("key.targets", ", ").replace(" ", "").split(",");
        cooldown = Integer.parseInt(CONFIG.getOrDefault("key.cooldown", "60").replace(" ", ""));
        maxConcurrentTrolls = Integer.parseInt(CONFIG.getOrDefault("key.max_concurrent_trolls", "1024").replace(" ", ""));

        System.out.println("All " + configs.getConfigsList().size() + " server trolling mod properties have been set properly");
    }
}