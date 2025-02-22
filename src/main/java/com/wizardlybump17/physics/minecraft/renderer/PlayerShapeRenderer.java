package com.wizardlybump17.physics.minecraft.renderer;

import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.container.BaseObjectContainer;
import com.wizardlybump17.physics.three.registry.BaseObjectContainerRegistry;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class PlayerShapeRenderer implements ShapeRenderer {

    private final @NotNull Set<Player> viewers = new HashSet<>();
    private final @NotNull BaseObjectContainer container;

    public PlayerShapeRenderer(@NotNull BaseObjectContainer container) {
        this.container = container;
    }

    public void drawPoint(@NotNull Player viewer, @NotNull Vector position, @NotNull Color color, float size) {
        drawPoint(viewer, position.getX(), position.getY(), position.getZ(), color, size);
    }

    public void drawPoint(@NotNull Player viewer, @NotNull Vector3D position, @NotNull Color color, float size) {
        drawPoint(viewer, position.x(), position.y(), position.z(), color, size);
    }

    public void drawPoint(@NotNull Player viewer, double x, double y, double z, @NotNull Color color, float size) {
        viewer.spawnParticle(Particle.DUST, x, y, z, 1, new Particle.DustOptions(color, size));
    }

    public @NotNull BaseObjectContainer getContainer() {
        return container;
    }

    @Override
    public @NotNull Collection<Player> getViewers() {
        return Collections.unmodifiableSet(viewers);
    }

    @Override
    public void addViewer(@NotNull Player player) {
        viewers.add(player);
    }

    @Override
    public boolean hasViewer(@NotNull Player player) {
        return viewers.contains(player);
    }

    @Override
    public void removeViewer(@NotNull Player player) {
        viewers.remove(player);
    }
}
