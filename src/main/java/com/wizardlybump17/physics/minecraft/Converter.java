package com.wizardlybump17.physics.minecraft;

import com.wizardlybump17.physics.Id;
import com.wizardlybump17.physics.three.Vector3D;
import org.bukkit.NamespacedKey;
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

    public static @NotNull NamespacedKey convert(@NotNull Id id) {
        StringBuilder namespace = new StringBuilder();
        id.namespace().chars()
                .filter(value -> isValidNamespaceChar((char) value))
                .forEach(namespace::append);

        StringBuilder key = new StringBuilder();
        id.key().chars()
                .filter(value -> isValidKeyChar((char) value))
                .forEach(key::append);

        return new NamespacedKey(namespace.toString(), key.toString());
    }

    public static @NotNull Id convert(@NotNull NamespacedKey key) {
        return new Id(key.getNamespace(), key.getKey());
    }

    private static boolean isValidNamespaceChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.' || c == '_' || c == '-';
    }

    private static boolean isValidKeyChar(char c) {
        return isValidNamespaceChar(c) || c == '/';
    }
}
