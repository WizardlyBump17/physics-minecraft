package com.wizardlybump17.physics.minecraft.renderer;

import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.shape.Shape;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class WorldShapeRenderer<S extends Shape> implements ShapeRenderer<S> {

    private final @NotNull World world;

    public WorldShapeRenderer(@NotNull World world) {
        this.world = world;
    }

    public @NotNull World getWorld() {
        return world;
    }

    @Override
    public @NotNull UUID getRendererId() {
        return world.getUID();
    }

    public void drawPoint(@NotNull Vector position, @NotNull Color color, float size) {
        drawPoint(position.getX(), position.getY(), position.getZ(), color, size);
    }

    public void drawPoint(@NotNull Vector3D position, @NotNull Color color, float size) {
        drawPoint(position.x(), position.y(), position.z(), color, size);
    }

    public void drawPoint(double x, double y, double z, @NotNull Color color, float size) {
        world.spawnParticle(Particle.DUST, x, y, z, 1, new Particle.DustOptions(color, size));
    }
}
