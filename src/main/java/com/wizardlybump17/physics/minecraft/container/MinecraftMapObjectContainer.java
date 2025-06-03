package com.wizardlybump17.physics.minecraft.container;

import com.wizardlybump17.physics.three.container.MapShapesGroupsContainer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MinecraftMapObjectContainer extends MapShapesGroupsContainer {

    private final @NotNull World world;

    public MinecraftMapObjectContainer(@NotNull World world, @NotNull UUID id) {
        super(id);
        this.world = world;
    }

    public MinecraftMapObjectContainer(@NotNull World world) {
        this(world, world.getUID());
    }

    public @NotNull World getWorld() {
        return world;
    }
}
