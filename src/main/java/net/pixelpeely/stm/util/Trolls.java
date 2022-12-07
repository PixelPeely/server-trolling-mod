package net.pixelpeely.stm.util;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static net.pixelpeely.stm.util.Utils.scheduleSyncRepeatingTask;
import static net.pixelpeely.stm.util.Utils.spawnEntity;

public class Trolls {
    public static final Map<String, Consumer<ServerPlayerEntity>> trolls = new HashMap<>();

    static {
        registerTroll("RANDOM", player -> trollPlayer(getRandomTroll(), player));
        registerTroll("DIORITE", player -> player.getInventory().setStack(22, new ItemStack(Items.DIORITE, 42)));
        registerTroll("SKY_LAVA", player -> player.getServer().getOverworld().setBlockState(new BlockPos(player.getBlockX(), 319, player.getBlockZ()), Blocks.LAVA.getDefaultState()));
        registerTroll("SMITE", player -> spawnEntity(player.getWorld(), EntityType.LIGHTNING_BOLT, "", player.getBlockPos()));
        registerTroll("ARROW_HEAD", player -> spawnEntity(player.getWorld(), EntityType.ARROW, "", player.getBlockPos().up(3)));
        registerTroll("YOUR_MOM_PIGLIN", player -> spawnEntity(player.getWorld(), EntityType.ZOMBIFIED_PIGLIN, "Your Mom in the Future", player.getBlockPos().up(5)));
        registerTroll("EXPLOSIVE_DIARRHEA", player -> spawnEntity(player.getWorld(), EntityType.CREEPER, "Explosive Diarrhea", new BlockPos(player.getEyePos().add(player.getRotationVector().multiply(-1, -1, -1))).down(1)));
        registerTroll("NAUSEA", player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 15*20, 255, true, false), null));
        registerTroll("TNT_UP", player -> spawnEntity(player.getWorld(), EntityType.TNT, "", player.getBlockPos().up(74)));
        registerTroll("FIRE_15S", player -> player.setOnFireFor(300));
        registerTroll("MAGENTA_TERRACOTTA", player -> player.giveItemStack(new ItemStack(Items.MAGENTA_GLAZED_TERRACOTTA, 21)));
        registerTroll("INVISIBLE_SPODER", player -> ((LivingEntity) spawnEntity(player.getWorld(), EntityType.SPIDER, "Spoder", player.getBlockPos())).addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 1, true, false)));
        registerTroll("WATER_DROP", player -> {
            player.world.setBlockState(player.getBlockPos().down(1), Blocks.WATER.getDefaultState());
            for (int i = player.getBlockY(); i <= 319; i++)
                player.world.setBlockState(new BlockPos(new Vec3d(player.getBlockX(), i, player.getBlockZ())), Blocks.AIR.getDefaultState());
            player.teleport(player.getBlockX() + 0.5, 319, player.getBlockZ() + 0.5);
        });
        registerTroll("JAIL", player -> {
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++) {
                    player.world.setBlockState(new BlockPos(player.getBlockX() + x, player.getBlockY() - 1, player.getBlockZ() + z), Blocks.STONE_BRICKS.getDefaultState());
                    player.world.setBlockState(new BlockPos(player.getBlockX() + x, player.getBlockY() + 3, player.getBlockZ() + z), Blocks.STONE_BRICKS.getDefaultState());
                    if (Math.abs(x) + Math.abs(z) != 0)
                        for (int y = 0; y <= 2; y++)
                            player.world.setBlockState(new BlockPos(player.getBlockX() + x, player.getBlockY() + y, player.getBlockZ() + z), Blocks.IRON_BARS.getDefaultState());
                }
        });
        registerTroll("ENDERMAN_NOISES", player -> {
            player.playSound(SoundEvents.ENTITY_ENDERMAN_SCREAM, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1);
            player.playSound(SoundEvents.ENTITY_ENDERMAN_STARE, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1);
        });
        registerTroll("JUMP_BOOST", player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 5*20, 120, false, false)));
        registerTroll("CANT_JUMP", player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 60*5*20, 200, false, false)));
        registerTroll("SPEED", player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10*20, 255, false, true)));
        registerTroll("BARRIER_FACE", player -> player.world.setBlockState(new BlockPos(player.getEyePos().add(player.getRotationVector().multiply(new Vec3d(2, 2, 2)))), Blocks.BARRIER.getDefaultState()));
        registerTroll("STRONG_SILVERFISH", player -> {
            LivingEntity entity = (LivingEntity)spawnEntity(player.getWorld(), EntityType.SILVERFISH, "A Measly Silverfish", player.getBlockPos());
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 255, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 255, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, Integer.MAX_VALUE, 6, false, false));
        });
        registerTroll("STACKED_ZOMBRO", player -> {
            LivingEntity entity = (LivingEntity)spawnEntity(player.getWorld(), EntityType.ZOMBIE, "Zombro", player.getBlockPos());
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, Integer.MAX_VALUE, 10, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, Integer.MAX_VALUE, 10, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 300*20, 255, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, Integer.MAX_VALUE, 10, false, false));
            entity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CARVED_PUMPKIN, 1));
            entity.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE, 1));
            entity.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS, 1));
            entity.equipStack(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS, 1));
            entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD, 1));
        });
        registerTroll("OBSIDIAN_DROWNED", player -> {
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
        });
        registerTroll("SILVERFISH_INFESTATION", player -> {
            BlockPos blockPos = player.getBlockPos();
            for (int x = -5; x <= 5; x++)
                for (int y = -5; y <= 5; y++)
                    for (int z = -5; z <= 5; z++)
                        if (Math.abs(x) + Math.abs(z) != 0 || (y != 0 && y != 1))
                            player.world.setBlockState(blockPos.add(x, y, z), Blocks.INFESTED_STONE.getDefaultState());
            LivingEntity entity = (LivingEntity) spawnEntity(player.getWorld(), EntityType.SILVERFISH, "Ticking Time Bomb", blockPos);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 4, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, Integer.MAX_VALUE, 255, false, false));
        });
        registerTroll("ANVIL_DROP", player -> {
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
        });
        registerTroll("END_CRYSTAL", player -> spawnEntity(player.getWorld(), EntityType.END_CRYSTAL, "", player.getBlockPos()));
        registerTroll("PHANTOM_AMOGUS", player -> ((LivingEntity)spawnEntity(player.getWorld(), EntityType.PHANTOM, "Amogus but in the sky", player.getBlockPos().up(50))).addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 255, false, false)));
        registerTroll("69_HOES", player -> player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.WOODEN_HOE, 69)));
        registerTroll("TELEPORT_GROUND", player -> player.teleport(player.getX(), player.getY() - 50, player.getZ()));
        registerTroll("INVISIBLE_GHAST_HELICOPTER", player -> {
            LivingEntity entity = (LivingEntity)spawnEntity(player.getWorld(), EntityType.GHAST, "I identify as an invisible attack helicopter", player.getBlockPos());
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 255, false, false));
        });
        registerTroll("CHEST_OPEN", player -> player.playSound(SoundEvents.BLOCK_CHEST_OPEN,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1));
        registerTroll("SHULKER_OPEN", player -> player.playSound(SoundEvents.BLOCK_SHULKER_BOX_OPEN,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1));
        registerTroll("CREEPER_HURT", player -> player.playSound(SoundEvents.ENTITY_CREEPER_HURT,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1));
        registerTroll("WITHER_SPAWN", player -> player.playSound(SoundEvents.ENTITY_WITHER_SPAWN,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1));
        registerTroll("WITHER_DEATH", player -> player.playSound(SoundEvents.ENTITY_WITHER_DEATH,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1));
        registerTroll("DRAGON_GROWL", player -> player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1));
        registerTroll("DRAGON_DEATH", player -> player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_DEATH,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 1));
        registerTroll("DISC_11", player -> player.playSound(SoundEvents.MUSIC_DISC_11,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 0.5f));
        registerTroll("DISC_11_HIGH", player -> player.playSound(SoundEvents.MUSIC_DISC_11,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 5f));
        registerTroll("DISC_13_LOW", player -> player.playSound(SoundEvents.MUSIC_DISC_13,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 0.5f));
        registerTroll("DISC_13_HIGH", player -> player.playSound(SoundEvents.MUSIC_DISC_13,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 5f));
        registerTroll("DISC_CHIRP_LOW", player -> player.playSound(SoundEvents.MUSIC_DISC_CHIRP,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 0.5f));
        registerTroll("DISC_CHIRP_HIGH", player -> player.playSound(SoundEvents.MUSIC_DISC_CHIRP,  SoundCategory.PLAYERS, Integer.MAX_VALUE, 5f));
        registerTroll("ALL_SOUNDS", player -> {
            for (SoundEvent sound : Registry.SOUND_EVENT)
                player.playSound(sound, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1);
        });
        registerTroll("ALL_SOUNDS_LOW", player -> {
            for (SoundEvent sound : Registry.SOUND_EVENT)
                player.playSound(sound, SoundCategory.PLAYERS, Integer.MAX_VALUE, 0.5f);
        });
        registerTroll("ALL_SOUNDS_REALLY_LOW", player -> {
            for (SoundEvent sound : Registry.SOUND_EVENT)
                player.playSound(sound, SoundCategory.PLAYERS, Integer.MAX_VALUE, 0.1f);
        });
        registerTroll("ALL_SOUNDS_HIGH", player -> {
            for (SoundEvent sound : Registry.SOUND_EVENT)
                player.playSound(sound, SoundCategory.PLAYERS, Integer.MAX_VALUE, 5f);
        });
        registerTroll("RAINING_PETS", player -> {
            for (int x = -15; x <= 15; x += 5)
                for (int z = -15; z <= 15; z += 5)
                    spawnEntity(player.getWorld(), new Random().ints(1, 0, 2).findFirst().getAsInt() == 1 ? EntityType.WOLF : EntityType.CAT, "It's raining cats and dogs!", player.getBlockPos().add(x, 8, z));
        });
        registerTroll("CURSED_PUMPKIN", player -> {
            ItemStack item = new ItemStack(Items.CARVED_PUMPKIN);
            item.addEnchantment(Enchantments.BINDING_CURSE, 1);

            player.getInventory().armor.set(3, item);
        });
        registerTroll("WITHERING_IRRITATION", player -> {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 300*20, 255, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 300*20, 2, false, false));
        });
        registerTroll("TOTEM_POP", player -> {
            player.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.TOTEM_OF_UNDYING, 1));
            player.damage(DamageSource.CACTUS, Integer.MAX_VALUE);
        });
        registerTroll("69_POTATOES", player -> player.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.POISONOUS_POTATO, 69)));
        registerTroll("HORSE_DEATH", player -> scheduleSyncRepeatingTask((world) -> player.playSound(SoundEvents.ENTITY_HORSE_DEATH, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1), 138, 2400, null));
        registerTroll("HORSE_LONG_FACE", player -> spawnEntity(player.getWorld(), EntityType.HORSE, "Why the long face?", player.getBlockPos().up(100)));
        registerTroll("DYING_GOLEM_UGLY", player -> ((LivingEntity)spawnEntity(player.getWorld(), EntityType.IRON_GOLEM, "I'm dying because you look so ugly", player.getBlockPos())).addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, Integer.MAX_VALUE, 255)));
        registerTroll("YOUR_MOM_WHOPPER", player -> {
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
            spawnEntity(player.getWorld(), EntityType.WARDEN, "Your mom after eating the impossible whopper", player.getBlockPos().down(3));
        });
        registerTroll("WANDERING_TRADER", player -> {
            for (int i = 0; i < 5; i++)
                spawnEntity(player.getWorld(), EntityType.WANDERING_TRADER, "me hav gud tredes", player.getBlockPos().up(5));
        });
        registerTroll("DRUGGED_PUFFERFISH", player -> {
            LivingEntity entity = ((LivingEntity) spawnEntity(player.getWorld(), EntityType.PUFFERFISH, "drug overdose", player.getBlockPos()));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 255, false, false));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 255, false, false));
            int index = ServerTickEventHandler.getAvailableSlotIndex();
            ServerTickEventHandler.registerTickEvent(index, (world) -> {
                entity.teleport(player.getX(), player.getY(), player.getZ());
                if (player.isDead())
                    ServerTickEventHandler.unregisterTickEvent(index);
            });

        });
        registerTroll("PANDA_DEATH", player -> scheduleSyncRepeatingTask((world) -> player.playSound(SoundEvents.ENTITY_PANDA_DEATH, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1), 138, 2400, null));
        registerTroll("PANDA_CRUSH", player -> spawnEntity(player.getWorld(), EntityType.PANDA, "I will crush you", player.getBlockPos().up(100)));
        registerTroll("BOB_THE_SHULKER", player -> spawnEntity(player.getWorld(), EntityType.SHULKER, "Bob", player.getBlockPos()));
        registerTroll("CANT_MOVE", player -> {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 255, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 100, 200, false, false));
        });
        registerTroll("PORTAL_OPEN", player -> scheduleSyncRepeatingTask((world) -> player.playSound(SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1), 10, 110, (world) -> player.playSound(SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1)));
        registerTroll("JUMPING_TELEPORTS", player -> scheduleSyncRepeatingTask((world) -> player.teleport(player.getX(), player.getY() + 4, player.getZ()), 60, 1200, null));
        registerTroll("MOB_PARTY", player -> player.getWorld().iterateEntities().forEach((entity -> {
            if (entity.getType() == EntityType.PLAYER)
                return;

            entity.teleport(player.getX(), player.getY(), player.getZ());
        })));
        registerTroll("PARTY_UNTIL_DEATH", player -> {
            int index = ServerTickEventHandler.getAvailableSlotIndex();
            ServerTickEventHandler.registerTickEvent(index, (world) -> {
                player.getWorld().iterateEntities().forEach((entity -> {
                    if (entity.getType() == EntityType.PLAYER)
                        return;

                    entity.teleport(player.getX(), player.getY(), player.getZ());
                }));

                if (player.isDead())
                    ServerTickEventHandler.unregisterTickEvent(index);
            });

        });
        registerTroll("MOB_NUKE", player -> player.getWorld().iterateEntities().forEach((entity -> {
            EntityType type = entity.getType();
            ServerWorld world = player.getWorld();

            if (type == EntityType.PLAYER || type == EntityType.TNT)
                return;

            world.spawnEntity(new TntEntity(EntityType.TNT, world));
        })));
        registerTroll("INVINCIBLE_MOBS", player -> player.getWorld().iterateEntities().forEach(entity -> {
            EntityType type = entity.getType();

            if (type == EntityType.PLAYER || entity.distanceTo(player) > 100)
                return;

            entity.setInvulnerable(true);
            ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, Integer.MAX_VALUE, 255));
        }));
        registerTroll("NO_FOOD_MEGAMIND", player -> player.getHungerManager().setFoodLevel(-Integer.MAX_VALUE));
        registerTroll("END_TIME", player -> player.world.setBlockState(player.getBlockPos(), Blocks.END_PORTAL.getDefaultState()));
        registerTroll("FLYING_MOBS", player -> player.getWorld().iterateEntities().forEach(entity -> {
            EntityType type = entity.getType();

            if (type == EntityType.PLAYER || entity.distanceTo(player) > 100)
                return;

            ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 1, 75));
        }));
        registerTroll("NUCLEAR_WARHEAD", player -> spawnEntity(player.getWorld(), EntityType.TNT, "Nuclear Warhead", new BlockPos(player.getEyePos().add(player.getRotationVector().multiply(5, 5, 5))).down(1)));
        registerTroll("LEVITATING_OBSIDIAN_BOX", player -> {
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
        });
        registerTroll("GHOST", player -> {
            EntityType[] entities = {EntityType.ALLAY, EntityType.BAT, EntityType.BEE, EntityType.BLAZE, EntityType.GHAST, EntityType.PARROT, EntityType.PHANTOM, EntityType.VEX};
            for (EntityType entity : entities) {
                LivingEntity livingEntity = ((LivingEntity)spawnEntity(player.getWorld(), entity, "I identify as a ghost", player.getBlockPos()));
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 255, false, true));
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, Integer.MAX_VALUE, 255, false, true));
            }
        });
        registerTroll("LEVITATING_MOBS", player -> {
            ServerWorld world = player.getWorld();
            world.iterateEntities().forEach(entity -> {
                EntityType type = entity.getType();

                if (type == EntityType.PLAYER || type == EntityType.ITEM || entity.distanceTo(player) > 100)
                    return;

                ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, Integer.MAX_VALUE, 1));
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        for (int y = 0; y <= 319; y++) {
                            BlockPos pos = entity.getBlockPos().add(x, y, z);
                            world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        }
                    }
                }
            });
        });
        registerTroll("DRAGON_DEATH_LOW", player -> player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.PLAYERS, Integer.MAX_VALUE, 0.1f));
        registerTroll("ZOGLIN_GROOT", player -> spawnEntity(player.getWorld(), EntityType.ZOGLIN, "I am groot", player.getBlockPos()));
        registerTroll("TELEPORT_DIAGONAL", player -> player.teleport(player.getX() + 2, player.getY() + 2, player.getZ() + 2));
        registerTroll("FREEZE_TO_DEATH", player -> {
            player.world.setBlockState(player.getBlockPos().up(1), Blocks.POWDER_SNOW.getDefaultState());
            BlockPos pos = player.getBlockPos();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, Integer.MAX_VALUE, 255, false, false));

            int index = ServerTickEventHandler.getAvailableSlotIndex();
            ServerTickEventHandler.registerTickEvent(index, (world) -> {
                player.teleport(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                if (player.isDead() || player.world.getBlockState(pos.up(1)) != Blocks.POWDER_SNOW.getDefaultState() || player.isDead()) {
                    ServerTickEventHandler.unregisterTickEvent(index);
                    player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
                }
            });
        });
        registerTroll("ANVIL_DEATH", player -> {
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
        });
        registerTroll("UNHEALTHY_PLAYER", player -> {
            player.setHealth(1);
            player.getHungerManager().setFoodLevel(1);
        });
        registerTroll("CACTUS_DEATH", player -> {
            player.world.setBlockState(player.getBlockPos().down(1), Blocks.CACTUS.getDefaultState());
            BlockPos pos = player.getBlockPos();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, Integer.MAX_VALUE, 255, false, true));

            int index = ServerTickEventHandler.getAvailableSlotIndex();
            ServerTickEventHandler.registerTickEvent(index, (world) -> {
                player.teleport(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                if (player.isDead() || player.world.getBlockState(pos.down(1)) != Blocks.CACTUS.getDefaultState() || player.isDead()) {
                    ServerTickEventHandler.unregisterTickEvent(index);
                    player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
                }
            });
        });
        registerTroll("BIG_SLIME", player -> {
            ServerWorld world = player.getWorld();

            SlimeEntity slime = new SlimeEntity(EntityType.SLIME, world);
            slime.setSize(50, false);
            slime.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, Integer.MAX_VALUE, 255, true, false));
            world.spawnEntity(slime);
        });
        registerTroll("LAVA_CAST", player -> {
            BlockPos root = new BlockPos(player.getBlockX(), 0, player.getBlockZ());
            for (int y = player.getBlockY() + 3; y < 317; y++)
                player.world.setBlockState(new BlockPos(player.getBlockX(), y, player.getBlockZ()), Blocks.LAVA.getDefaultState());
            player.world.setBlockState(root.up(319), Blocks.WATER.getDefaultState());
            player.world.setBlockState(root.up(318), Blocks.COBBLESTONE.getDefaultState());
        });
        registerTroll("PORTAL_AMBIENCE", player -> scheduleSyncRepeatingTask((world) -> player.playSound(SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.PLAYERS, Integer.MAX_VALUE, 1), 120, 60*5*20, null));
        registerTroll("CREEPER_SUICIDE", player -> ((LivingEntity)spawnEntity(player.getWorld(), EntityType.CREEPER, "a suicide bomber", player.getBlockPos())).addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 255, false, true)));
        registerTroll("BLINDNESS", player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 1200, 255, false, true)));
        registerTroll("DROP_ITEM", player -> player.getInventory().dropSelectedItem(true));
        registerTroll("DROP_INVENTORY", player -> {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                player.dropStack(player.getEquippedStack(slot));
                player.equipStack(slot, ItemStack.EMPTY);
            }
        });
        registerTroll("FELL_INTO_WATER", player -> player.damage(new DamageSource("fell.accident.water"), Integer.MAX_VALUE));
        registerTroll("HEROBRINE", player -> player.getServer().getPlayerManager().getPlayerList().forEach((serverPlayer) -> serverPlayer.sendMessage(Text
                .literal("Herobrine joined the game")
                .formatted(Formatting.YELLOW))));
        registerTroll("TNT_NUKE", player -> {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20, 255));
            ServerWorld world = player.getWorld();

            TntEntity tnt = new TntEntity(EntityType.TNT, world);
            tnt.setFuse(0);

            for (int i = 0; i < 100; i++)
                world.spawnEntity(tnt);
        });
        registerTroll("BOAT_DROP", player -> {
            Entity entity = spawnEntity(player.getWorld(), EntityType.BOAT, "WEEEEEEEEEEEEEEEEEEEE", player.getBlockPos().up(319));
            player.startRiding(entity);
        });
        registerTroll("RIDE_CART", player -> {
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
            Entity entity = spawnEntity(player.getWorld(), EntityType.MINECART, "LOL", pos);

            int index = ServerTickEventHandler.getAvailableSlotIndex();
            ServerTickEventHandler.registerTickEvent(index, (world) -> {
                if (entity.isAlive() && player.isAlive())
                    player.startRiding(entity);
                else
                    ServerTickEventHandler.unregisterTickEvent(index);
            });
        });
        registerTroll("ARMOR_STAND_HARASSMENT", player -> {
            Entity entity = spawnEntity(player.getWorld(), EntityType.ARMOR_STAND, "", player.getBlockPos());
            entity.setInvulnerable(true);
            scheduleSyncRepeatingTask((world) -> entity.teleport(player.getX(), player.getY(), player.getZ()), 1, 1200, (world) -> entity.kill());
        });
        registerTroll("XP_FARM", player -> {
            BlockPos pos = player.getBlockPos();
            player.world.setBlockState(pos.down(1), Blocks.SCULK_CATALYST.getDefaultState());
            scheduleSyncRepeatingTask((world) -> {
                Entity entity = spawnEntity(player.getWorld(), EntityType.PIGLIN_BRUTE, "Best XP farm in town", pos);
                entity.damage(DamageSource.FALL, Integer.MAX_VALUE);
            }, 10, 300, null);
        });
        registerTroll("SPAWN_ALL", player -> Registry.ENTITY_TYPE.forEach((entity) -> spawnEntity(player.getWorld(), entity, "wut", player.getBlockPos())));
        registerTroll("TELEPORT_69420", player -> player.teleport(69420, 69, 69420));
        registerTroll("VOID_SPAWNPOINT", player -> {
            player.world.setBlockState(BlockPos.ORIGIN.add(0, -64, 0), Blocks.STONE.getDefaultState());
            player.world.setBlockState(BlockPos.ORIGIN.add(0, -63, 0), Blocks.AIR.getDefaultState());
            player.world.setBlockState(BlockPos.ORIGIN.add(0, -62, 0), Blocks.AIR.getDefaultState());
            player.setSpawnPoint(World.OVERWORLD, new BlockPos(0, -63, 0), 0, true, false);
        });
        registerTroll("SKY_SPAWNPOINT", player -> {
            player.world.setBlockState(new BlockPos(player.getBlockX(), 319, player.getBlockZ()), Blocks.STONE.getDefaultState());
            player.setSpawnPoint(World.OVERWORLD, player.getBlockPos().withY(320), 0, true, false);
        });
        registerTroll("INVISIBLE_GUARDIAN", player -> ((LivingEntity) spawnEntity(player.getWorld(), EntityType.ELDER_GUARDIAN, "A spiky thing", player.getBlockPos())).addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 1, false, true)));
        registerTroll("GIVE_DRUGS", player -> {
            ItemStack cocaine = new ItemStack(Items.SUGAR);
            ItemStack meth = new ItemStack(Items.AMETHYST_SHARD);

            cocaine.setCustomName(Text.literal("Cocaine").setStyle(Style.EMPTY));
            meth.setCustomName(Text.literal("Crystal Meth").setStyle(Style.EMPTY));

            player.giveItemStack(cocaine);
            player.giveItemStack(meth);
        });
        registerTroll("FLESH_INVENTORY", player -> {
            PlayerInventory inv = player.getInventory();
            for (int i = 0; i < player.getInventory().size(); i++) {
                if (inv.getStack(i).getCount() != 0)
                    inv.setStack(i, new ItemStack(Items.ROTTEN_FLESH, inv.getStack(i).getCount()));
            }
        });
        registerTroll("ALL_EFFECTS", player -> Registry.STATUS_EFFECT.forEach((effect) -> player.addStatusEffect(new StatusEffectInstance(effect, 200, 255, false, true))));
        registerTroll("37_ROTTEN_FLESH", player -> player.giveItemStack(new ItemStack(Items.ROTTEN_FLESH, 37)));
    }

    public static Consumer<ServerPlayerEntity> getRandomTroll() {
        List<Consumer<ServerPlayerEntity>> allTrolls = trolls.values().stream().toList();

        return allTrolls.get((int) (Math.random() * allTrolls.size()));
    }

    public static void trollPlayer(Consumer<ServerPlayerEntity> troll, ServerPlayerEntity player) {
        troll.accept(player);
    }

    public static @Nullable Consumer<ServerPlayerEntity> getTroll(String id) {
        if (!trolls.containsKey(id))
            return null;

        return trolls.get(id);
    }

    private static void registerTroll(String id, Consumer<ServerPlayerEntity> callback) {
        if (trolls.containsKey(id))
            throw new IllegalStateException("Tried to register troll that already exists. Did you try to register twice?");

        trolls.put(id, callback);
    }
}
