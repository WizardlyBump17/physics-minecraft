package com.wizardlybump17.physics.minecraft.group;

import com.wizardlybump17.physics.Id;
import com.wizardlybump17.physics.minecraft.container.MinecraftMapObjectContainer;
import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.group.ContainerShapesGroup;
import com.wizardlybump17.physics.three.shape.Shape;
import com.wizardlybump17.physics.util.MapUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MinecraftPhysicsShapesGroup extends ContainerShapesGroup {

    public MinecraftPhysicsShapesGroup(@NotNull MinecraftMapObjectContainer container, @NotNull List<Shape> shapes) {
        this(container, shapes, Vector3D.ZERO, Vector3D.ZERO, Vector3D.ZERO);
    }

    public MinecraftPhysicsShapesGroup(@NotNull MinecraftMapObjectContainer container, @NotNull List<Shape> shapes, @NotNull Vector3D acceleration, @NotNull Vector3D velocity, @NotNull Vector3D rotation) {
        super(
                container,
                Map.of(Id.GENERIC, acceleration),
                velocity,
                shapes.stream()
                        .map(Shape::getPosition)
                        .reduce(Vector3D::add)
                        .map(position -> position.divide(shapes.size()))
                        .orElseThrow(),
                shapes.stream()
                        .map(Shape::getPosition)
                        .reduce(Vector3D::add)
                        .map(position -> position.divide(shapes.size()))
                        .orElseThrow(),
                rotation,
                MapUtil.fromCollection(shapes, shape -> new Id("Shape", String.valueOf(shape.hashCode())))
        );
    }
}
