package tech.thatgravyboat.rewardclaim.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.rewardclaim.ui.RewardClaimScreens;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow @Nullable public Screen screen;

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void rewardclaim$setScreen(Screen screen, CallbackInfo ci) {
        if (this.screen != null && RewardClaimScreens.isOpening() && (screen instanceof BookViewScreen || screen == null)) {
            ci.cancel();
        }
    }
}
