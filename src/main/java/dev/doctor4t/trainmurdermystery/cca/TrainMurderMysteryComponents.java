package dev.doctor4t.trainmurdermystery.cca;

import dev.doctor4t.trainmurdermystery.TrainMurderMystery;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class TrainMurderMysteryComponents implements WorldComponentInitializer {
    public static final ComponentKey<WorldTrainComponent> TRAIN = ComponentRegistry.getOrCreate(TrainMurderMystery.id("train"), WorldTrainComponent.class);
    public static final ComponentKey<WorldGameComponent> GAME = ComponentRegistry.getOrCreate(TrainMurderMystery.id("game"), WorldGameComponent.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(TRAIN, WorldTrainComponent::new);
        registry.register(GAME, WorldGameComponent::new);
    }
}