package net.pixelpeely.stm.config;

import com.mojang.datafixers.util.Pair;
import net.pixelpeely.stm.STMMain;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static String[] targets;
    public static int cooldown;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(STMMain.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("key.targets", "Player1, Player2"), "These are the players to be targeted, separate each by a comma and a space");
        configs.addKeyValuePair(new Pair<>("key.cooldown", 60), "How many seconds between each troll");
    }

    private static void assignConfigs() {
        targets = CONFIG.getOrDefault("key.targets", ", ").replace(" ", "").split(",");
        cooldown = Integer.parseInt(CONFIG.getOrDefault("key.cooldown", "60").replace(" ", ""));

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }
}