package net.pixelpeely.stm.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.pixelpeely.stm.STMMain;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Shadow @Final private static String LEVEL_KEY;

    @Accessor("LEVEL_KEY")
    private static String getLevelKey() {
        return LEVEL_KEY;
    }

    /**
     * @author PixelPeely
     * @reason Remove the enchantment level limit
     */
    @Overwrite
    public static int getLevelFromNbt(NbtCompound nbt) {
        return nbt.getInt(getLevelKey());
    }
}
