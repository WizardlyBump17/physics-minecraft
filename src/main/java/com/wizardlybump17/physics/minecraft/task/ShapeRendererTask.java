package com.wizardlybump17.physics.minecraft.task;

import com.wizardlybump17.physics.minecraft.renderer.PlayerShapeRenderer;
import com.wizardlybump17.physics.minecraft.renderer.ShapeRenderer;
import com.wizardlybump17.physics.three.container.BaseObjectContainer;
import com.wizardlybump17.physics.three.object.BaseObject;
import com.wizardlybump17.physics.three.shape.Shape;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShapeRendererTask extends BukkitRunnable {

    private final @NotNull Map<Class<? extends Shape>, Set<ShapeRenderer>> renderers = new HashMap<>();

    public void addRenderer(@NotNull ShapeRenderer renderer) {
        renderers.computeIfAbsent(renderer.getShapeType(), $ -> new HashSet<>()).add(renderer);
    }

    public void removeRenderer(@NotNull ShapeRenderer renderer) {
        Set<ShapeRenderer> renderers = this.renderers.get(renderer.getShapeType());
        if (renderers != null)
            renderers.remove(renderer);
    }

    public boolean hasRenderer(@NotNull ShapeRenderer renderer) {
        return renderers.getOrDefault(renderer.getShapeType(), Collections.emptySet()).contains(renderer);
    }

    public @NotNull Map<Class<? extends Shape>, Set<ShapeRenderer>> getRenderers() {
        return Collections.unmodifiableMap(renderers);
    }

    public @NotNull Map<Class<? extends Shape>, Set<ShapeRenderer>> getRenderers(@NotNull UUID containerId) {
        Map<Class<? extends Shape>, Set<ShapeRenderer>> renderers = new HashMap<>();
        this.renderers.forEach((type, renderersSet) -> {
            for (ShapeRenderer renderer : renderersSet) {
                if (renderer instanceof PlayerShapeRenderer playerShapeRenderer && playerShapeRenderer.getContainer().getId().equals(containerId))
                    renderers.computeIfAbsent(type, $ -> new HashSet<>()).add(renderer);
            }
        });
        return renderers;
    }

    public @NotNull Set<ShapeRenderer> getRenderers(@NotNull Class<? extends Shape> type) {
        return renderers.getOrDefault(type, Set.of());
    }

    public @NotNull Set<ShapeRenderer> getRenderers(@NotNull Class<? extends Shape> type, @NotNull UUID containerId) {
        Set<ShapeRenderer> renderers = new HashSet<>();
        for (ShapeRenderer renderer : this.renderers.getOrDefault(type, Set.of()))
            if (renderer instanceof PlayerShapeRenderer playerShapeRenderer && playerShapeRenderer.getContainer().getId().equals(containerId))
                renderers.add(renderer);
        return renderers;
    }

    public void clear() {
        renderers.clear();
    }

    @Override
    public void run() {
        for (Set<ShapeRenderer> renderers : renderers.values()) {
            for (ShapeRenderer renderer : renderers) {
                if (renderer instanceof PlayerShapeRenderer worldShapeRenderer)
                    renderWorldRenderer(worldShapeRenderer);
            }
        }
    }

    protected void renderWorldRenderer(@NotNull PlayerShapeRenderer renderer) {
        for (Player viewer : renderer.getViewers()) {
            BaseObjectContainer container = renderer.getContainer();
            for (BaseObject object : container.getLoadedObjects()) {
                Shape shape = object.getShape();
                if (renderer.isValidShape(shape))
                    renderer.render(viewer, shape);
            }
        }
    }
}
