package com.wizardlybump17.physics.minecraft.renderer.shape;

import com.wizardlybump17.physics.three.shape.Shape;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ShapeRenderer {

    void render(@NotNull Player viewer, @NotNull Shape shape);

    @NotNull Class<? extends Shape> getShapeType();

    default boolean isValidShape(@NotNull Shape shape) {
        return getShapeType().equals(shape.getClass());
    }

    @NotNull Collection<Player> getViewers();

    void addViewer(@NotNull Player player);

    boolean hasViewer(@NotNull Player player);

    void removeViewer(@NotNull Player player);
}
