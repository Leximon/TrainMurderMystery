package dev.doctor4t.trainmurdermystery.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.Random;

public class PlayerRoleComponent implements AutoSyncedComponent {
    private final PlayerEntity player;

    private Role role = Role.NONE;

    public PlayerRoleComponent(PlayerEntity player) {
        this.player = player;
    }

    private void sync() {
        TrainMurderMysteryComponents.ROLE.sync(this.player);
    }

    public void setRole(Role role) {
        this.role = role;
        this.sync();
    }

    public Role getRole() {
        return role;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.role = Role.valueOf(nbtCompound.getString("Role"));

    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putString("Role", role.name());
    }

    public enum Role {
        NONE,
        PASSENGER,
        HITMAN,
        DETECTIVE;
    }
}