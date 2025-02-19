package com.wizardlybump17.physics.minecraft;

import com.wizardlybump17.physics.three.Vector3D;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class Converter {

    private Converter() {
    }

    public static @NotNull Vector convert(@NotNull Vector3D original) {
        return new Vector(original.x(), original.y(), original.z());
    }

    public static @NotNull Vector3D convert(@NotNull Vector original) {
        return new Vector3D(original.getX(), original.getY(), original.getZ());
    }
}
