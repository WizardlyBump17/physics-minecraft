package com.wizardlybump17.physics.minecraft.debug;

import com.wizardlybump17.physics.three.container.MapObjectContainer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class DebugObjectContainer extends MapObjectContainer {

    private final @NotNull World world;

    public DebugObjectContainer(@NotNull World world) {
        super(getId(world));
        this.world = world;
    }

    public @NotNull World getWorld() {
        return world;
    }

    public static @NotNull UUID getId(@NotNull World world) {
        return UUID.nameUUIDFromBytes((world.getName() + "-debug").getBytes(StandardCharsets.UTF_8));
    }
}
