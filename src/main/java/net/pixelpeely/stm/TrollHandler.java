package net.pixelpeely.stm;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TrollHandler {
    public static int numTrolls(){
        return trollActions.length;
    }

    private static Entity spawnEntity(EntityType entity, String name, BlockPos blockPos) {
        return entity.spawn(MinecraftClient.getInstance().getServer().getOverworld(), null, Text.literal(name), null, blockPos, null, true, false);
    }

    public static void delayExecution(ServerTickEvents.StartTick action, int time){
        int index = ServerTickEventHandler.getAvailableSlotIndex();
        AtomicInteger timer = new AtomicInteger(0);
        ServerTickEventHandler.registerTickEvent(index, (world) -> {
            timer.getAndIncrement();
            if (timer.get() > time){
                STMMain.LOGGER.info(String.valueOf(timer.get()));
                action.onStartTick(world);
                ServerTickEventHandler.unregisterTickEvent(index);
            }
        });
    }

    public static void registerRecursiveEvent(ServerTickEvents.StartTick action, int recursionTime, int duration, ServerTickEvents.StartTick endAction) {
        int index = ServerTickEventHandler.getAvailableSlotIndex();
        AtomicInteger timer = new AtomicInteger(0);
        ServerTickEventHandler.registerTickEvent(index, (world) -> {
            STMMain.LOGGER.info(String.valueOf(timer.get()));
            timer.getAndIncrement();
            if (timer.get() % recursionTime == 0)
                action.onStartTick(world);
            if (timer.get() > duration) {
                ServerTickEventHandler.unregisterTickEvent(index);
                if (endAction != null)
                    endAction.onStartTick(MinecraftClient.getInstance().getServer());
            }
        });
    }

    interface TrollAction {
        void troll(PlayerEntity player);
    }
    private static final TrollAction[] trollActions = new TrollAction[] {
            player -> player.getInventory().setStack(22, new ItemStack(Items.DIORITE, 42)),
            player -> player.world.setBlockState(new BlockPos(player.getBlockX(), 319, player.getBlockZ()), Blocks.LAVA.getDefaultState()),
            player -> spawnEntity(EntityType.LIGHTNING_BOLT, "", player.getBlockPos()),
            player -> spawnEntity(EntityType.ARROW, "", player.getBlockPos().up(3)),
            player -> spawnEntity(EntityType.ZOMBIFIED_PIGLIN, "Your Mom in the Future", player.getBlockPos().up(5)),
            player -> spawnEntity(EntityType.CREEPER, "Explosive Diarrhea", new BlockPos(player.getEyePos().add(player.getRotationVector().multiply(-1, -1, -1))).down(1)),
            player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 15*20, 255, true, false), null),
            player -> spawnEntity(EntityType.TNT, "", player.getBlockPos().up(74)),
            player -> player.setOnFireFor(300),
            player -> player.giveItemStack(new ItemStack(Items.MAGENTA_GLAZED_TERRACOTTA, 21)),
            player -> ((LivingEntity) spawnEntity(EntityType.SPIDER, "Spoder", player.getBlockPos())).addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 1, true, false)),
            player -> {
                player.world.setBlockState(player.getBlockPos().down(1), Blocks.WATER.getDefaultState());
                for (int i = player.getBlockY(); i <= 319; i++)
                    player.world.setBlockState(new BlockPos(new Vec3d(player.getBlockX(), i, player.getBlockZ())), Blocks.AIR.getDefaultState());
                player.teleport(player.getBlockX() + 0.5, 319, player.getBlockZ() + 0.5);
            },
            player -> {
                for (int x = -1; x <= 1; x++)
                    for (int z = -1; z <= 1; z++) {
                        player.world.setBlockState(new BlockPos(player.getBlockX() + x, player.getBlockY() - 1, player.getBlockZ() + z), Blocks.STONE_BRICKS.getDefaultState());
                        player.world.setBlockState(new BlockPos(player.getBlockX() + x, player.getBlockY() + 3, player.getBlockZ() + z), Blocks.STONE_BRICKS.getDefaultState());
                        if (Math.abs(x) + Math.abs(z) != 0)
                            for (int y = 0; y <= 2; y++)
                                player.world.setBlockState(new BlockPos(player.getBlockX() + x, player.getBlockY() + y, player.getBlockZ() + z), Blocks.IRON_BARS.getDefaultState());
                    }
            },
            player -> {
                player.playSound(SoundEvents.ENTITY_ENDERMAN_SCREAM, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1);
                player.playSound(SoundEvents.ENTITY_ENDERMAN_STARE, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1);
            },
            player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 5*20, 120, false, false)),
            player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 60*5*20, 200, false, false)),
            player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10*20, 255, false, true)),
            player -> player.world.setBlockState(new BlockPos(player.getEyePos().add(player.getRotationVector().multiply(new Vec3d(2, 2, 2)))), Blocks.BARRIER.getDefaultState()),
            player -> {
                LivingEntity entity = (LivingEntity)spawnEntity(EntityType.SILVERFISH, "A Measly Silverfish", player.getBlockPos());
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 255, false, false));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 255, false, false));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, Integer.MAX_VALUE, 6, false, false));
            },
            player -> {
                LivingEntity entity = (LivingEntity)spawnEntity(EntityType.ZOMBIE, "Zombro", player.getBlockPos());
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 10, false, false));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, Integer.MAX_VALUE, 10, false, false));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 300*20, 255, false, false));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, Integer.MAX_VALUE, 10, false, false));
                entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CARVED_PUMPKIN, 1));
                entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE, 1));
                entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS, 1));
                entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS, 1));
                entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD, 1));
            },
            player -> {
                BlockPos pos = player.getBlockPos();
                player.teleport(pos.getX(), pos.getY() + 5, pos.getZ());
                player.getInventory().dropAll();
                player.teleport(player.getBlockX(), player.getBlockY(), player.getBlockZ());
                for (int x = -1; x <= 1; x++)
                    for (int y = -1; y <= 2; y++)
                        for (int z = -1; z <= 1; z++) {
                            BlockPos blockPos = new BlockPos(player.getBlockX() + x, player.getBlockY() + y, player.getBlockZ() + z);
                            player.world.setBlockState(blockPos, Blocks.OBSIDIAN.getDefaultState());
                            player.world.setBlockState(blockPos, Blocks.OBSIDIAN.getDefaultState());
                            if (Math.abs(x) + Math.abs(z) == 0 && (y == 0 || y == 1))
                                player.world.setBlockState(blockPos, Blocks.WATER.getDefaultState());
                        }
            },
            player -> {
                BlockPos blockPos = player.getBlockPos();
                for (int x = -5; x <= 5; x++)
                    for (int y = -5; y <= 5; y++)
                        for (int z = -5; z <= 5; z++)
                            if (Math.abs(x) + Math.abs(z) != 0 || (y != 0 && y != 1))
                                player.world.setBlockState(blockPos.add(x, y, z), Blocks.INFESTED_STONE.getDefaultState());
                LivingEntity entity = (LivingEntity) spawnEntity(EntityType.SILVERFISH, "Ticking Time Bomb", blockPos);
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 4, false, false));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, Integer.MAX_VALUE, 255, false, false));
            },
            player -> {
                FallingBlockEntity anvil = FallingBlockEntity.spawnFromBlock(player.world, player.getBlockPos().up(20), Blocks.ANVIL.getDefaultState());
                int index = ServerTickEventHandler.getAvailableSlotIndex();
                ServerTickEventHandler.registerTickEvent(index, (world) -> {
                    anvil.requestTeleport(player.getX(), anvil.getY(), player.getZ());
                    if (anvil.getBlockY() > player.getBlockY())
                        for (int x = -1; x <= 1; x++)
                            for (int y = -1; y <= 1; y++)
                                for (int z = -1; z <= 1; z++)
                                    player.world.setBlockState(anvil.getBlockPos().add(x, y, z), Blocks.AIR.getDefaultState());
                    else {
                        ServerTickEventHandler.unregisterTickEvent(index);
                        anvil.kill();
                        player.damage(DamageSource.ANVIL, 10);
                        player.playSound(SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1);
                        player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1);
                    }
                });
            },
            player -> spawnEntity(EntityType.END_CRYSTAL, "", player.getBlockPos()),
            player -> ((LivingEntity)spawnEntity(EntityType.PHANTOM, "Amogus but in the sky", player.getBlockPos().up(50))).addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 255, false, false)),
            player -> player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.WOODEN_HOE, 69)),
            player -> player.teleport(player.getX(), player.getY() - 50, player.getZ()),
            player -> {
                LivingEntity entity = (LivingEntity)spawnEntity(EntityType.GHAST, "I identify as an invisible attack helicopter", player.getBlockPos());
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 255, false, false));
            },
            player -> player.playSound(SoundEvents.BLOCK_CHEST_OPEN,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1),
            player -> player.playSound(SoundEvents.BLOCK_SHULKER_BOX_OPEN,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1),
            player -> player.playSound(SoundEvents.ENTITY_CREEPER_HURT,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1),
            player -> player.playSound(SoundEvents.ENTITY_WITHER_SPAWN,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1),
            player -> player.playSound(SoundEvents.ENTITY_WITHER_DEATH,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1),
            player -> player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1),
            player -> player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_DEATH,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1),
            player -> player.playSound(SoundEvents.MUSIC_DISC_11,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 0.5f),
            player -> player.playSound(SoundEvents.MUSIC_DISC_11,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 5f),
            player -> player.playSound(SoundEvents.MUSIC_DISC_13,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 0.5f),
            player -> player.playSound(SoundEvents.MUSIC_DISC_13,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 5f),
            player -> player.playSound(SoundEvents.MUSIC_DISC_CHIRP,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 0.5f),
            player -> player.playSound(SoundEvents.MUSIC_DISC_CHIRP,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 5f),
            player -> {
                for (SoundEvent sound : Registry.SOUND_EVENT)
                    player.playSound(sound, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1);
            },
            player -> {
                for (SoundEvent sound : Registry.SOUND_EVENT)
                    player.playSound(sound, SoundCategory.PLAYERS, Integer.MAX_VALUE, 0.5f);
            },
            player -> {
                for (SoundEvent sound : Registry.SOUND_EVENT)
                    player.playSound(sound, SoundCategory.PLAYERS, Integer.MAX_VALUE, 0.1f);
            },
            player -> {
                for (SoundEvent sound : Registry.SOUND_EVENT)
                    player.playSound(sound, SoundCategory.PLAYERS, Integer.MAX_VALUE, 5f);
            },
            player -> {
                for (int x = -15; x <= 15; x += 5)
                    for (int z = -15; z <= 15; z += 5)
                        spawnEntity(new Random().ints(1, 0, 2).findFirst().getAsInt() == 1 ? EntityType.WOLF : EntityType.CAT, "It's raining cats and dogs!", player.getBlockPos().add(x, 8, z));
            },
            player -> player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "item replace entity @s armor.head with minecraft:carved_pumpkin{Enchantments:[{id:binding_curse,lvl:1}]}"),
            player -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 300*20, 255, false, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 300*20, 2, false, false));
            },
            player -> {
                player.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING, 1));
                player.damage(DamageSource.CACTUS, Integer.MAX_VALUE);
            },
            player -> player.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.POISONOUS_POTATO, 69)),
            player -> registerRecursiveEvent((world) -> player.playSound(SoundEvents.ENTITY_HORSE_DEATH, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1), 138, 2400, null),
            player -> spawnEntity(EntityType.HORSE, "Why the long face?", player.getBlockPos().up(100)),
            player -> ((LivingEntity)spawnEntity(EntityType.IRON_GOLEM, "I'm dying because you look so ugly", player.getBlockPos())).addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, Integer.MAX_VALUE, 255)),
            player -> {
                for (int x = -2; x <= 2; x++)
                    for (int y = -2; y <= 2; y++)
                        for (int z = -2; z <= 2; z++) {
                            BlockPos pos = player.getBlockPos().add(x, y, z);
                            if (x >= -1 && x <= 1 && z >= -1 && z <= 1 && y >= -1) {
                                if (y == 2)
                                    player.world.setBlockState(pos, Blocks.GLASS.getDefaultState());
                                else
                                    player.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                            }
                            else
                                player.world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
                        }
                spawnEntity(EntityType.WARDEN, "Your mom after eating the impossible whopper", player.getBlockPos().down(3));
            },
            player -> {
                for (int i = 0; i < 5; i++)
                    spawnEntity(EntityType.WANDERING_TRADER, "me hav gud tredes", player.getBlockPos().up(5));
            },
            player -> {
                LivingEntity entity = ((LivingEntity) spawnEntity(EntityType.PUFFERFISH, "drug overdose", player.getBlockPos()));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 255, false, false));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 255, false, false));
                int index = ServerTickEventHandler.getAvailableSlotIndex();
                ServerTickEventHandler.registerTickEvent(index, (world) -> {
                    entity.teleport(player.getX(), player.getY(), player.getZ());
                    if (player.isDead())
                        ServerTickEventHandler.unregisterTickEvent(index);
                });

            },
            player -> registerRecursiveEvent((world) -> player.playSound(SoundEvents.ENTITY_PANDA_DEATH, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1), 138, 2400, null),
            player -> spawnEntity(EntityType.PANDA, "I will crush you", player.getBlockPos().up(100)),
            player -> spawnEntity(EntityType.SHULKER, "Bob", player.getBlockPos()),
            player -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 255, false, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 100, 200, false, false));
            },
            player -> registerRecursiveEvent((world) -> player.playSound(SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1), 10, 110, (world) -> player.playSound(SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1)),
            player -> registerRecursiveEvent((world) -> player.teleport(player.getX(), player.getY() + 4, player.getZ()), 60, 1200, null),
            player -> player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "tp @e[type=!player] @s"),
            player -> {
                int index = ServerTickEventHandler.getAvailableSlotIndex();
                ServerTickEventHandler.registerTickEvent(index, (world) -> {
                    player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "tp @e[type=!player] @s");
                    if (player.isDead())
                        ServerTickEventHandler.unregisterTickEvent(index);
                });

            },
            player -> player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "execute at @e[type=!player, type=!tnt, distance=..100] run summon tnt ~ ~ ~ {Fuse:200}"),
            player -> {
                player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "effect give @e[type=!player, distance=..100] minecraft:resistance 999999 255");
                player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "effect give @e[type=!player, distance=..100] minecraft:wither 999999 255");
            },
            player -> player.getHungerManager().setFoodLevel(-Integer.MAX_VALUE),
            player -> player.world.setBlockState(player.getBlockPos(), Blocks.END_PORTAL.getDefaultState()),
            player -> player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "effect give @e[type=!player, distance=..100] minecraft:levitation 1 75"),
            player -> spawnEntity(EntityType.TNT, "Nuclear Warhead", new BlockPos(player.getEyePos().add(player.getRotationVector().multiply(5, 5, 5))).down(1)),
            player -> {
                for (int x = -1; x <= 1; x++)
                    for (int y = -1; y <= 1; y++)
                        for (int z = -1; z <= 1; z++) {
                            if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 0 || y == -1)
                                player.world.setBlockState(player.getBlockPos().up(1).add(x, y ,z), Blocks.AIR.getDefaultState());
                            else
                                player.world.setBlockState(player.getBlockPos().up(1).add(x, y, z), Blocks.OBSIDIAN.getDefaultState());
                        }
                for (int y = -64; y < player.getBlockY(); y++)
                    player.world.setBlockState(new BlockPos(player.getBlockX(), y, player.getBlockZ()), Blocks.AIR.getDefaultState());
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 50, 1, false, true));
            },
            player -> {
                EntityType[] entities = {EntityType.ALLAY, EntityType.BAT, EntityType.BEE, EntityType.BLAZE, EntityType.GHAST, EntityType.PARROT, EntityType.PHANTOM, EntityType.VEX};
                for (EntityType entity : entities) {
                    LivingEntity livingEntity = ((LivingEntity)spawnEntity(entity, "I identify as a ghost", player.getBlockPos()));
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 255, false, true));
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 255, false, true));
                }
            },
            player -> {
                player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "execute at @e[type=!player, type=!item, distance=..100] run fill ~-1 ~ ~-1 ~1 319 ~1 air");
                player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "effect give @e[type=!player, distance=..100] minecraft:levitation 999999 1");
            },
            player -> player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.PLAYERS, Integer.MAX_VALUE, 0.1f),
            player -> spawnEntity(EntityType.ZOGLIN, "I am groot", player.getBlockPos()),
            player -> player.teleport(player.getX() + 2, player.getY() + 2, player.getZ() + 2),
            player -> {
                player.world.setBlockState(player.getBlockPos().up(1), Blocks.POWDER_SNOW.getDefaultState());
                BlockPos pos = player.getBlockPos();
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, Integer.MAX_VALUE, 255, false, false));

                int index = ServerTickEventHandler.getAvailableSlotIndex();
                ServerTickEventHandler.registerTickEvent(index, (world) -> {
                    player.teleport(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    if (player.getHealth() <= 1 || player.world.getBlockState(pos.up(1)) != Blocks.POWDER_SNOW.getDefaultState() || player.isDead()) {
                        ServerTickEventHandler.unregisterTickEvent(index);
                        player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
                    }
                });
            },
            player -> {
                FallingBlockEntity anvil = FallingBlockEntity.spawnFromBlock(player.world, player.getBlockPos().up(200), Blocks.ANVIL.getDefaultState());
                int index = ServerTickEventHandler.getAvailableSlotIndex();
                BlockPos pos = player.getBlockPos();
                player.teleport(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                ServerTickEventHandler.registerTickEvent(index, (world) -> {
                    if (anvil.getBlockY() > pos.getY() && player.isAlive() && anvil.isAlive()) {
                        for (int y = pos.getY(); y < 319; y++)
                            player.world.setBlockState(new BlockPos(pos.getX(), y, pos.getZ()), Blocks.AIR.getDefaultState());
                        player.teleport(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                        player.lookAt(player.getCommandSource().getEntityAnchor(), anvil.getPos());
                    }
                    else {
                        ServerTickEventHandler.unregisterTickEvent(index);
                        anvil.kill();
                        player.damage(DamageSource.ANVIL, Integer.MAX_VALUE);
                        player.playSound(SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1);
                        player.playSound(SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1);
                    }
                });
            },
            player -> {
                player.setHealth(1);
                player.getHungerManager().setFoodLevel(1);
            },
            player -> {
                player.world.setBlockState(player.getBlockPos().down(1), Blocks.CACTUS.getDefaultState());
                BlockPos pos = player.getBlockPos();
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, Integer.MAX_VALUE, 255, false, true));

                int index = ServerTickEventHandler.getAvailableSlotIndex();
                ServerTickEventHandler.registerTickEvent(index, (world) -> {
                    player.teleport(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    if (player.getHealth() <= 1 || player.world.getBlockState(pos.down(1)) != Blocks.CACTUS.getDefaultState() || player.isDead()) {
                        ServerTickEventHandler.unregisterTickEvent(index);
                        player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
                    }
                });
            },
            player -> {
                player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "summon slime ~ ~ ~ {Size:50}");
                player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "effect give @e[type=slime, limit=1] minecraft:slowness 999999 255 true");
            },
            player -> {
                BlockPos root = new BlockPos(player.getBlockX(), 0, player.getBlockZ());
                for (int y = player.getBlockY() + 3; y < 317; y++)
                    player.world.setBlockState(new BlockPos(player.getBlockX(), y, player.getBlockZ()), Blocks.LAVA.getDefaultState());
                player.world.setBlockState(root.up(319), Blocks.WATER.getDefaultState());
                player.world.setBlockState(root.up(318), Blocks.COBBLESTONE.getDefaultState());
            },
            player -> registerRecursiveEvent((world) -> player.playSound(SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1), 120, 60*5*20, null),
            player -> ((LivingEntity)spawnEntity(EntityType.CREEPER, "a suicide bomber", player.getBlockPos())).addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 255, false, true)),
            player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 1200, 255, false, true)),
            player -> player.getInventory().dropSelectedItem(true),
            player -> {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    player.dropStack(player.getEquippedStack(slot));
                    player.equipStack(slot, ItemStack.EMPTY);
                }
            },
            player -> player.damage(new DamageSource("fell.accident.water"), Integer.MAX_VALUE),
            player -> player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "tellraw @a {\"text\":\"Herobrine joined the game\",\"color\":\"yellow\"}"),
            player -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20, 255));
                for (int i = 0; i < 100; i++)
                    player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "summon tnt");
            },
            player -> {
                Entity entity = spawnEntity(EntityType.BOAT, "WEEEEEEEEEEEEEEEEEEEE", player.getBlockPos().up(319));
                player.startRiding(entity);
            },
                player -> {
                BlockPos pos = player.getBlockPos();
                player.world.setBlockState(pos.add(-1, 0, 0), Blocks.DIRT.getDefaultState());
                for (int x = 0; x < 20; x++) {
                    player.world.setBlockState(pos.add(x, -1, 0), Blocks.DIRT.getDefaultState());
                    player.world.setBlockState(pos.add(x, 0, 0), Blocks.POWERED_RAIL.getDefaultState());
                    player.world.setBlockState(pos.add(x, -1, 1), Blocks.DIRT.getDefaultState());
                    player.world.setBlockState(pos.add(x, 0, 1), Blocks.REDSTONE_TORCH.getDefaultState());
                }
                for (int x = -1; x <= 1; x++)
                    for (int z = -1; z <= 1; z++)
                        player.world.setBlockState(pos.add(21 + x, -1, z), Blocks.LAVA.getDefaultState());
                Entity entity = spawnEntity(EntityType.MINECART, "LOL", pos);

                int index = ServerTickEventHandler.getAvailableSlotIndex();
                ServerTickEventHandler.registerTickEvent(index, (world) -> {
                    if (entity.isAlive() && player.isAlive())
                        player.startRiding(entity);
                    else
                        ServerTickEventHandler.unregisterTickEvent(index);
                });
            },
            player -> {
                Entity entity = spawnEntity(EntityType.ARMOR_STAND, "", player.getBlockPos());
                entity.setInvulnerable(true);
                registerRecursiveEvent((world) -> entity.teleport(player.getX(), player.getY(), player.getZ()), 1, 1200, (world) -> entity.kill());
            },
            player -> {
                BlockPos pos = player.getBlockPos();
                player.world.setBlockState(pos.down(1), Blocks.SCULK_CATALYST.getDefaultState());
                registerRecursiveEvent((world) -> {
                    Entity entity = spawnEntity(EntityType.PIGLIN_BRUTE, "Best XP farm in town", pos);
                    entity.damage(DamageSource.FALL, Integer.MAX_VALUE);
                }, 10, 300, null);
            },
            player -> Registry.ENTITY_TYPE.forEach((entity) -> spawnEntity(entity, "wut", player.getBlockPos())),
            player -> player.teleport(69420, 69, 69420),
/*96*/            player -> {
                player.world.setBlockState(BlockPos.ORIGIN.add(0, -64, 0), Blocks.STONE.getDefaultState());
                player.world.setBlockState(BlockPos.ORIGIN.add(0, -63, 0), Blocks.AIR.getDefaultState());
                player.world.setBlockState(BlockPos.ORIGIN.add(0, -62, 0), Blocks.AIR.getDefaultState());
                player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "spawnpoint @s 0 -63 0");
            },
            player -> {
                player.world.setBlockState(new BlockPos(player.getBlockX(), 319, player.getBlockZ()), Blocks.STONE.getDefaultState());
                player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "spawnpoint @s ~ 320 ~");
            },
            player -> ((LivingEntity) spawnEntity(EntityType.ELDER_GUARDIAN, "A spiky thing", player.getBlockPos())).addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 1, false, true)),
            player -> {
                player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "give @s sugar{display:{Name:\"\\\"Cocaine\\\"\"}}");
                player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), "give @s amethyst_shard{display:{Name:\"\\\"Crystal Meth\\\"\"}}");
            },
            player -> Registry.STATUS_EFFECT.forEach((effect) -> player.addStatusEffect(new StatusEffectInstance(effect, 200, 255, false, true))),
            player -> player.giveItemStack(new ItemStack(Items.ROTTEN_FLESH, 37)),
            player -> {
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < player.getInventory().size(); i++) {
                    if (inv.getStack(i).getCount() != 0)
                        inv.setStack(i, new ItemStack(Items.ROTTEN_FLESH, inv.getStack(i).getCount()));
                }
            }
    };

    public static void executeRandomTroll(PlayerEntity player) {
        Random random = new Random();
        if (random.ints(1, 1, 14406).findFirst().getAsInt() <= 100)//~0.69420%
            for (int i = 0; i < numTrolls(); i++)
                executeTroll(player, i);
        else
            executeTroll(player, random.ints(1, 0, trollActions.length).findFirst().getAsInt());
    }

    public static void executeTroll(PlayerEntity player, int id) {
        trollActions[id].troll(player);
    }
}