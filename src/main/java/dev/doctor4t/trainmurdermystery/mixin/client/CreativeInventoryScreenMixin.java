package dev.doctor4t.trainmurdermystery.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.doctor4t.trainmurdermystery.client.TrainMurderMysteryClient;
import dev.doctor4t.trainmurdermystery.client.gui.screen.ingame.LimitedInventoryScreen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
    public CreativeInventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @WrapMethod(method = "handledScreenTick")
    public void tmm$replaceSurvivalInventory(Operation<Void> original) {
        if (TrainMurderMysteryClient.shouldRestrictPlayerOptions()) {
            this.client.setScreen(new LimitedInventoryScreen(this.client.player));
        } else {
            original.call();
        }
    }
}
