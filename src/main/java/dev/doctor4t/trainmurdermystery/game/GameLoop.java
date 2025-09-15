package dev.doctor4t.trainmurdermystery.game;

import dev.doctor4t.trainmurdermystery.cca.TrainMurderMysteryComponents;
import dev.doctor4t.trainmurdermystery.cca.WorldGameComponent;
import dev.doctor4t.trainmurdermystery.index.TrainMurderMysteryItems;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.*;

public class GameLoop {
    public static void tick(ServerWorld serverWorld) {
        WorldGameComponent game = TrainMurderMysteryComponents.GAME.get(serverWorld);

        if (game.isRunning()) {
            // check hitman win condition (all targets are dead)
            WinStatus winStatus = WinStatus.HITMEN;
            for (UUID player : game.getTargets()) {
                if (!isPlayerEliminated(serverWorld.getPlayerByUuid(player))) {
                    winStatus = WinStatus.NONE;
                }
            }

            // check passenger win condition (all hitmen are dead)
            if (winStatus == WinStatus.NONE) {
                winStatus = WinStatus.PASSENGERS;
                for (UUID player : game.getHitmen()) {
                    if (!isPlayerEliminated(serverWorld.getPlayerByUuid(player))) {
                        winStatus = WinStatus.NONE;
                    }
                }
            }

            // win display
            if (winStatus != WinStatus.NONE) {
                for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                    player.sendMessage(Text.translatable("game.win." + winStatus.name().toLowerCase(Locale.ROOT)), true);
                }
                game.stop();
            }
        }
    }

    public static void startGame(ServerWorld world) {
        TrainMurderMysteryComponents.TRAIN.get(world).setTrainSpeed(130);
        WorldGameComponent gameComponent = TrainMurderMysteryComponents.GAME.get(world);

        List<ServerPlayerEntity> playerPool = new ArrayList<>(world.getPlayers().stream().filter(serverPlayerEntity -> !serverPlayerEntity.isInCreativeMode() && !serverPlayerEntity.isSpectator()).toList());

        // limit the game to 14 players, put players 15 to n in spectator mode
        Collections.shuffle(playerPool);
        while (playerPool.size() > 14) {
            playerPool.getFirst().changeGameMode(GameMode.SPECTATOR);
            playerPool.removeFirst();
        }

        List<ServerPlayerEntity> rolePlayerPool = new ArrayList<>(playerPool);

        // clear items, clear previous game data
        for (ServerPlayerEntity serverPlayerEntity : rolePlayerPool) {
            serverPlayerEntity.getInventory().clear();
        }
        gameComponent.resetLists();

        // select hitmen
        int hitmanCount = (int) Math.floor(rolePlayerPool.size() * .2f);
        Collections.shuffle(rolePlayerPool);
        for (int i = 0; i < hitmanCount; i++) {
            ServerPlayerEntity player = rolePlayerPool.getFirst();
            rolePlayerPool.removeFirst();
            player.giveItemStack(new ItemStack(TrainMurderMysteryItems.KNIFE));
            player.giveItemStack(new ItemStack(TrainMurderMysteryItems.LOCKPICK));

            ItemStack letter = new ItemStack(TrainMurderMysteryItems.LETTER);
            letter.set(DataComponentTypes.ITEM_NAME, Text.translatable(letter.getTranslationKey() + ".instructions"));
            player.giveItemStack(letter);

            gameComponent.addHitman(player);
        }

        // select detectives
        int detectiveCount = hitmanCount;
        Collections.shuffle(rolePlayerPool);
        for (int i = 0; i < detectiveCount; i++) {
            ServerPlayerEntity player = rolePlayerPool.getFirst();
            rolePlayerPool.removeFirst();
            player.giveItemStack(new ItemStack(TrainMurderMysteryItems.REVOLVER));
            gameComponent.addDetective(player);
        }

        // select targets
        int targetCount = rolePlayerPool.size() / 2;
        Collections.shuffle(rolePlayerPool);
        for (int i = 0; i < targetCount; i++) {
            ServerPlayerEntity player = rolePlayerPool.getFirst();
            rolePlayerPool.removeFirst();
            player.giveItemStack(new ItemStack(Blocks.TARGET.asItem()));
            gameComponent.addTarget(player);
        }

        // select rooms
        Collections.shuffle(playerPool);
        for (int i = 0; i < playerPool.size(); i++) {
            ItemStack itemStack = new ItemStack(TrainMurderMysteryItems.KEY);
            int roomNumber = (int) Math.floor((double) (i + 2) / 2);
            itemStack.apply(DataComponentTypes.LORE, LoreComponent.DEFAULT, component -> new LoreComponent(Text.literal("Room "+ roomNumber).getWithStyle(Style.EMPTY.withItalic(false).withColor(0xFF8C00))));
            playerPool.get(i).giveItemStack(itemStack);
        }

        gameComponent.start();
    }

    public static boolean isPlayerEliminated(PlayerEntity player) {
        return player == null || !player.isAlive() || player.isCreative() || player.isSpectator();
    }

    public enum WinStatus {
        NONE, HITMEN, PASSENGERS
    }
}
