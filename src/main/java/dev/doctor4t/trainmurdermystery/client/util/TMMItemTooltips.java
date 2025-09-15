package dev.doctor4t.trainmurdermystery.client.util;

import dev.doctor4t.trainmurdermystery.client.TrainMurderMysteryClient;
import dev.doctor4t.trainmurdermystery.game.GameLoop;
import dev.doctor4t.trainmurdermystery.index.TrainMurderMysteryItems;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class TMMItemTooltips {
    public static void addTooltips() {
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, tooltipList) -> {
            addTooltipsForItem(TrainMurderMysteryItems.KNIFE, 3, itemStack, tooltipList);
            addTooltipsForItem(TrainMurderMysteryItems.LOCKPICK, 2, itemStack, tooltipList);

            if (itemStack.isOf(TrainMurderMysteryItems.LETTER)) {
                Item item = itemStack.getItem();

                if (TrainMurderMysteryClient.isHitman()) {
                    String tooltipString = "tip." + item.getTranslationKey().substring(24) + ".hitman.tooltip";

                    int letterTextColor = 0xC5AE8B;
                    tooltipList.add(Text.translatable(tooltipString + "1").withColor(letterTextColor));

                    for (UUID target : TrainMurderMysteryClient.getTargets()) {
                        PlayerEntity player = MinecraftClient.getInstance().world.getPlayerByUuid(target);

                        UnaryOperator<Style> stylizer = style -> GameLoop.isPlayerEliminated(player) ? style.withStrikethrough(true).withColor(0x1B8943) : style.withColor(0x8A1B29);
                        PlayerListEntry playerListEntry = TrainMurderMysteryClient.PLAYER_ENTRIES_CACHE.get(target);
                        if (playerListEntry != null) tooltipList.add(Text.translatable(tooltipString + ".target", playerListEntry.getProfile().getName()).styled(stylizer));
                    }

                    for (int i = 2; i <= 4; i++) {
                        tooltipList.add(Text.translatable(tooltipString + i).withColor(letterTextColor));
                    }
                } else if (TrainMurderMysteryClient.isDetective()) {

                } else if (TrainMurderMysteryClient.isPassenger()) {

                }

            }
        });
    }

    private static void addTooltipsForItem(Item item, int tooltipLineCount, ItemStack itemStack, List<Text> tooltipList) {
        if (itemStack.isOf(item)) {
            addCooldownText(item, tooltipList);

            for (int i = 1; i <= tooltipLineCount; i++) {
                tooltipList.add(Text.translatable("tip." + item.getTranslationKey().substring(24) + ".tooltip" + i).withColor(0x808080));
            }
        }
    }

    private static void addCooldownText(Item item, List<Text> tooltipList) {
        ItemCooldownManager itemCooldownManager = MinecraftClient.getInstance().player.getItemCooldownManager();
        if (itemCooldownManager.isCoolingDown(item)) {
            ItemCooldownManager.Entry knifeEntry = itemCooldownManager.entries.get(item);
            int timeLeft = knifeEntry.endTick - itemCooldownManager.tick;

            if (timeLeft > 0) {
                int minutes = (int) Math.floor((double) timeLeft / 1200);
                int seconds = (timeLeft - (minutes * 1200)) / 20;
                String countdown = (minutes > 0 ? minutes + "m" : "") + (seconds > 0 ? seconds + "s" : "");
                tooltipList.add(Text.translatable("tip.cooldown", countdown).withColor(0xC90000));
            }
        }
    }
}
