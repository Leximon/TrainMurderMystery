package dev.doctor4t.trainmurdermystery.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.ArrayList;
import java.util.List;

public class WorldGameComponent implements AutoSyncedComponent {
    private final World world;

    private boolean running = false;
    private List<PlayerEntity> players = new ArrayList<>();
    private List<PlayerEntity> hitmen = new ArrayList<>();
    private List<PlayerEntity> detectives = new ArrayList<>();
    private List<PlayerEntity> targets = new ArrayList<>();

    public WorldGameComponent(World world) {
        this.world = world;
    }

    private void sync() {
        TrainMurderMysteryComponents.TRAIN.sync(this.world);
    }

    public void setRunning(boolean running) {
        this.running = running;
        this.sync();
    }

    public boolean isRunning() {
        return running;
    }

    public void setPlayers(List<PlayerEntity> players) {
        this.players = players;
        this.sync();
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public List<PlayerEntity> getHitmen() {
        return hitmen;
    }

    public void addHitman(PlayerEntity hitman) {
        this.hitmen.add(hitman);
        this.sync();
    }

    public void setHitmen(List<PlayerEntity> hitmen) {
        this.hitmen = hitmen;
        this.sync();
    }

    public List<PlayerEntity> getDetectives() {
        return detectives;
    }

    public void addDetective(PlayerEntity detective) {
        this.detectives.add(detective);
        this.sync();
    }

    public void setDetectives(List<PlayerEntity> detectives) {
        this.detectives = detectives;
        this.sync();
    }

    public List<PlayerEntity> getTargets() {
        return targets;
    }

    public void addTarget(PlayerEntity detective) {
        this.targets.add(detective);
        this.sync();
    }

    public void setTargets(List<PlayerEntity> targets) {
        this.targets = targets;
        this.sync();
    }

    public void resetLists() {
        setDetectives(new ArrayList<>());
        setHitmen(new ArrayList<>());
        setTargets(new ArrayList<>());
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.running = nbtCompound.getBoolean("Running");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putBoolean("Running", running);
    }
}
