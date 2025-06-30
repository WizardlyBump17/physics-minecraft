package com.wizardlybump17.physics.minecraft.renderer.shape;

import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.container.ShapesGroupsContainer;
import com.wizardlybump17.physics.three.shape.Shape;
import com.wizardlybump17.physics.three.shape.rotating.RotatingCube;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RotatingCubeRenderer extends WorldShapeRenderer {

    public static final @NotNull Color COLOR = Color.YELLOW;

    public static final float SIZE = 0.2F;

    public RotatingCubeRenderer(@NotNull ShapesGroupsContainer container) {
        super(container);
    }

    @Override
    public void render(@NotNull Player viewer, @NotNull Shape shape) {
        RotatingCube cube = (RotatingCube) shape;

        List<Vector3D> points = cube.getTransformedPoints();
        for (int i = 0; i < points.size(); i++) {
            Vector3D current = points.get(i);
            Vector3D next = points.get((i + 1) % points.size());

            Vector3D normalize = next.subtract(current).normalize();
            double distance = current.distance(next);
            for (double j = 0; j <= distance; j += 0.1) {
                Vector3D target = normalize.multiply(j).add(current);
                drawPoint(viewer, target, COLOR, SIZE);
            }

            drawPoint(viewer, current, Color.RED, SIZE);
        }

        for (double d = 0; d < 1; d += 0.05) {
            Vector3D rotation = cube.getRotation();
            drawPoint(viewer, cube.getPosition().add(new Vector3D(d, 0, 0).rotateAround(rotation)), Color.RED, 0.15F);
            drawPoint(viewer, cube.getPosition().add(new Vector3D(0, d, 0).rotateAround(rotation)), Color.BLUE, 0.15F);
            drawPoint(viewer, cube.getPosition().add(new Vector3D(0, 0, d).rotateAround(rotation)), Color.GREEN, 0.15F);
        }
    }

    @Override
    public @NotNull Class<RotatingCube> getShapeType() {
        return RotatingCube.class;
    }
}
