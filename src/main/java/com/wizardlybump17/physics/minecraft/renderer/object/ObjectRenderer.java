package com.wizardlybump17.physics.minecraft.renderer.object;

import com.wizardlybump17.physics.three.object.BaseObject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ObjectRenderer {

    void render(@NotNull Player viewer, @NotNull BaseObject object);

    @NotNull Class<? extends BaseObject> getObjectType();

    default boolean isValidObject(@NotNull BaseObject object) {
        return getObjectType().equals(object.getClass());
    }

    @NotNull Collection<Player> getViewers();

    void addViewer(@NotNull Player player);

    boolean hasViewer(@NotNull Player player);

    void removeViewer(@NotNull Player player);
}
