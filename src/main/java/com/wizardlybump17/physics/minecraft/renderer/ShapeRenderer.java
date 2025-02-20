package com.wizardlybump17.physics.minecraft.renderer;

import com.wizardlybump17.physics.three.shape.Shape;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ShapeRenderer<S extends Shape> {

    void render(@NotNull S shape);

    @NotNull Class<S> getShapeType();

    @NotNull UUID getRendererId();
}
