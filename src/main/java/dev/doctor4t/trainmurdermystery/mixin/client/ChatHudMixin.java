package dev.doctor4t.trainmurdermystery.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.doctor4t.trainmurdermystery.client.TrainMurderMysteryClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @WrapMethod(method = "render")
    public void tmm$disableChatRender(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, Operation<Void> original) {
        if (!TrainMurderMysteryClient.shouldRestrictPlayerOptions()) {
            original.call(context, currentTick, mouseX, mouseY, focused);
        }
    }
}
