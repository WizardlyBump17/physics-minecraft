package com.wizardlybump17.physics.minecraft.renderer;

import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.container.BaseObjectContainer;
import com.wizardlybump17.physics.three.shape.Shape;
import com.wizardlybump17.physics.three.shape.Sphere;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SphereRenderer extends PlayerShapeRenderer {

    public static final @NotNull Color COLOR = Color.PURPLE;
    public static final float SIZE = 0.1F;

    public SphereRenderer(@NotNull BaseObjectContainer container) {
        super(container);
    }

    @Override
    public void render(@NotNull Player viewer, @NotNull Shape shape) {
        Sphere sphere = (Sphere) shape;
        double radius = sphere.getRadius();
        Vector3D position = sphere.getPosition();
        Vector3D min = position.subtract(radius, radius, radius);
        Vector3D max = position.add(radius, radius, radius);
        for (double x = min.x(); x <= max.x(); x += 0.1) {
            for (double y = min.y(); y <= max.y(); y += 0.1) {
                for (double z = min.z(); z <= max.z(); z += 0.1) {
                    if (sphere.hasPoint(new Vector3D(x, y, z)))
                        drawPoint(viewer, x, y, z, COLOR, SIZE);
                }
            }
        }
    }

    @Override
    public @NotNull Class<Sphere> getShapeType() {
        return Sphere.class;
    }
}
