package com.wizardlybump17.physics.minecraft.renderer.shape;

import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.container.ShapesGroupsContainer;
import com.wizardlybump17.physics.three.shape.Shape;
import com.wizardlybump17.physics.three.shape.Sphere;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SphereRenderer extends WorldShapeRenderer {

    public static final @NotNull Color COLOR = Color.PURPLE;

    public static final float SIZE = 0.2F;

    public SphereRenderer(@NotNull ShapesGroupsContainer container) {
        super(container);
    }

    @Override
    public void render(@NotNull Player viewer, @NotNull Shape shape) {
        Sphere sphere = (Sphere) shape;

        double radius = sphere.getRadius();
        Vector3D position = sphere.getPosition();

        int segments = (int) (Math.PI * radius * 2 * 10);

        for (int i = 0; i < segments; i++) {
            double angle = (double) i / segments * Math.PI * 2;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            double z = 0;
            drawPoint(viewer, position.add(x, y, z), COLOR, SIZE);
        }

        for (int i = 0; i < segments; i++) {
            double angle = (double) i / segments * Math.PI * 2;
            double x = radius * Math.sin(angle);
            double y = 0;
            double z = radius * Math.cos(angle);
            drawPoint(viewer, position.add(x, y, z), COLOR, SIZE);
        }

        for (int i = 0; i < segments; i++) {
            double angle = (double) i / segments * Math.PI * 2;
            double x = 0;
            double y = radius * Math.sin(angle);
            double z = radius * Math.cos(angle);
            drawPoint(viewer, position.add(x, y, z), COLOR, SIZE);
        }

        for (int i = 0; i <= segments; i++) {
            double angle = (double) i / segments * Math.PI * 2;
            double x = radius * Math.sin(angle) * Math.cos(Math.PI / 4);
            double y = radius * Math.sin(angle) * Math.sin(Math.PI / 4);
            double z = radius * Math.cos(angle);
            drawPoint(viewer, position.add(x, y, z), COLOR, SIZE);
        }

        for (int i = 0; i <= segments; i++) {
            double angle = (double) i / segments * Math.PI * 2;
            double x = radius * Math.sin(angle) * Math.cos(-Math.PI / 4);
            double y = radius * Math.sin(angle) * Math.sin(-Math.PI / 4);
            double z = radius * Math.cos(angle);
            drawPoint(viewer, position.add(x, y, z), COLOR, SIZE);
        }
    }

    @Override
    public @NotNull Class<Sphere> getShapeType() {
        return Sphere.class;
    }
}
