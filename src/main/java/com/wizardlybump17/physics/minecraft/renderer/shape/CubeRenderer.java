package com.wizardlybump17.physics.minecraft.renderer.shape;

import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.container.BaseObjectContainer;
import com.wizardlybump17.physics.three.shape.Cube;
import com.wizardlybump17.physics.three.shape.Shape;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CubeRenderer extends WorldShapeRenderer {

    public static final @NotNull Color COLOR = Color.AQUA;

    public static final float SIZE = 0.2F;

    public CubeRenderer(@NotNull BaseObjectContainer container) {
        super(container);
    }

    @Override
    public void render(@NotNull Player viewer, @NotNull Shape shape) {
        Cube cube = (Cube) shape;

        Vector3D min = cube.getMin();
        Vector3D max = cube.getMax();

        for (double x = min.x(); x <= max.x(); x += 0.1) {
            drawPoint(viewer, x, min.y(), min.z(), COLOR, SIZE);
            drawPoint(viewer, x, max.y(), min.z(), COLOR, SIZE);
            drawPoint(viewer, x, min.y(), max.z(), COLOR, SIZE);
            drawPoint(viewer, x, max.y(), max.z(), COLOR, SIZE);
        }

        for (double y = min.y(); y <= max.y(); y += 0.1) {
            drawPoint(viewer, min.x(), y, min.z(), COLOR, SIZE);
            drawPoint(viewer, max.x(), y, min.z(), COLOR, SIZE);
            drawPoint(viewer, min.x(), y, max.z(), COLOR, SIZE);
            drawPoint(viewer, max.x(), y, max.z(), COLOR, SIZE);
        }

        for (double z = min.z(); z <= max.z(); z += 0.1) {
            drawPoint(viewer, min.x(), min.y(), z, COLOR, SIZE);
            drawPoint(viewer, max.x(), min.y(), z, COLOR, SIZE);
            drawPoint(viewer, min.x(), max.y(), z, COLOR, SIZE);
            drawPoint(viewer, max.x(), max.y(), z, COLOR, SIZE);
        }
    }

    @Override
    public @NotNull Class<Cube> getShapeType() {
        return Cube.class;
    }
}
