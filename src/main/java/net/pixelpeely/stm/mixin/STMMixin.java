package net.pixelpeely.stm.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import net.pixelpeely.stm.STMMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class STMMixin {
	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
		STMMain.LOGGER.info("STM Mixin Initialized");
	}
}
