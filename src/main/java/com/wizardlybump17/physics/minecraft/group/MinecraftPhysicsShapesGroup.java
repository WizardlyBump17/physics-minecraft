package com.wizardlybump17.physics.minecraft.group;

import com.wizardlybump17.physics.minecraft.container.MinecraftMapObjectContainer;
import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.group.PhysicsShapesGroup;
import com.wizardlybump17.physics.three.shape.Shape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MinecraftPhysicsShapesGroup extends PhysicsShapesGroup {

    private boolean passable;

    public MinecraftPhysicsShapesGroup(@NotNull MinecraftMapObjectContainer container, @NotNull List<Shape> shapes) {
        this(container, shapes, Vector3D.ZERO, Vector3D.ZERO);
    }

    public MinecraftPhysicsShapesGroup(@NotNull MinecraftMapObjectContainer container, @NotNull List<Shape> shapes, @NotNull Vector3D acceleration, @NotNull Vector3D velocity) {
        super(container, shapes, acceleration, velocity);
    }

    @Override
    public boolean isPassable() {
        return passable;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }
}
