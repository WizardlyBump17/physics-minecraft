package com.wizardlybump17.physics.minecraft.renderer;

import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.shape.Shape;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public abstract class WorldShapeRenderer<S extends Shape> implements ShapeRenderer<S> {

    private final @NotNull World world;

    public WorldShapeRenderer(@NotNull World world) {
        this.world = world;
    }

    public @NotNull World getWorld() {
        return world;
    }

    public void drawPoint(@NotNull Vector position, @NotNull Color color, float size) {
        world.spawnParticle(Particle.DUST, position.getX(), position.getY(), position.getZ(), 1, new Particle.DustOptions(color, size));
    }

    public void drawPoint(@NotNull Vector3D position, @NotNull Color color, float size) {
        world.spawnParticle(Particle.DUST, position.x(), position.y(), position.z(), 1, new Particle.DustOptions(color, size));
    }
}
