package dev.doctor4t.trainmurdermystery.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.trainmurdermystery.client.TrainMurderMysteryClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @ModifyReturnValue(method = "hasOutline", at = @At("RETURN"))
    public boolean tmm$hasInstinctOutline(boolean original, @Local(argsOnly = true) Entity entity) {
        if (TrainMurderMysteryClient.instinctKeybind.isPressed() && TrainMurderMysteryClient.isHitman() && entity instanceof PlayerEntity && TrainMurderMysteryClient.shouldRestrictPlayerOptions()) {
            return true;
        }
        return original;
    }
}
