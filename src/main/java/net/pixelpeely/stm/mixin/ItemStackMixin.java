package net.pixelpeely.stm.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.pixelpeely.stm.STMMain;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract NbtCompound getOrCreateNbt();

    @Shadow private @Nullable NbtCompound nbt;

    @Shadow @Final public static String ENCHANTMENTS_KEY;

    /**
     * @author PixelPeely
     * @reason Remove the conversion of int to byte to allow enchantments over 127 when using the addEnchantment method in the itemstack class
     */
    @Overwrite
    public void addEnchantment(Enchantment enchantment, int level){
        this.getOrCreateNbt();
        if (!this.nbt.contains(ENCHANTMENTS_KEY, NbtElement.LIST_TYPE)) {
            this.nbt.put(ENCHANTMENTS_KEY, new NbtList());
        }
        NbtList nbtList = this.nbt.getList(ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE);
        nbtList.add(EnchantmentHelper.createNbt(EnchantmentHelper.getEnchantmentId(enchantment), level));
    }
}
