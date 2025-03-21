package com.wizardlybump17.physics.minecraft.renderer.object;

import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.container.BaseObjectContainer;
import com.wizardlybump17.physics.three.object.BaseObject;
import com.wizardlybump17.physics.three.shape.Cube;
import com.wizardlybump17.physics.three.shape.Shape;
import com.wizardlybump17.physics.three.shape.Sphere;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BasicObjectRenderer extends WorldObjectRenderer {

    public static final @NotNull Color CUBE_COLOR = Color.AQUA;
    public static final @NotNull Color CUBE_COLLIDING_COLOR = Color.BLUE;
    public static final @NotNull Color SPHERE_COLOR = Color.PURPLE;
    public static final @NotNull Color SPHERE_COLLIDING_COLOR = Color.RED;
    public static final float SIZE = 0.2F;

    public BasicObjectRenderer(@NotNull BaseObjectContainer container) {
        super(container);
    }

    @Override
    public void render(@NotNull Player viewer, @NotNull BaseObject object) {
        Shape shape = object.getShape();
        boolean hasCollisions = !object.getCollidingWith().isEmpty();

        switch (shape) {
            case Cube cube -> {
                Color color = hasCollisions ? CUBE_COLLIDING_COLOR : CUBE_COLOR;

                Vector3D min = cube.getMin();
                Vector3D max = cube.getMax();
                for (double x = min.x(); x <= max.x(); x += 0.1) {
                    for (double y = min.y(); y <= max.y(); y += 0.1) {
                        for (double z = min.z(); z <= max.z(); z += 0.1) {
                            drawPoint(viewer, x, y, z, color, SIZE);
                        }
                    }
                }
            }
            case Sphere sphere -> {
                Color color = hasCollisions ? SPHERE_COLLIDING_COLOR : SPHERE_COLOR;

                double radius = sphere.getRadius();
                Vector3D position = sphere.getPosition();
                Vector3D min = position.subtract(radius, radius, radius);
                Vector3D max = position.add(radius, radius, radius);
                for (double x = min.x(); x <= max.x(); x += 0.1) {
                    for (double y = min.y(); y <= max.y(); y += 0.1) {
                        for (double z = min.z(); z <= max.z(); z += 0.1) {
                            if (sphere.hasPoint(new Vector3D(x, y, z)))
                                drawPoint(viewer, x, y, z, color, SIZE);
                        }
                    }
                }
            }
            default -> {
            }
        }
    }

    @Override
    public @NotNull Class<BaseObject> getObjectType() {
        return BaseObject.class;
    }

    @Override
    public boolean isValidObject(@NotNull BaseObject object) {
        return true;
    }
}
