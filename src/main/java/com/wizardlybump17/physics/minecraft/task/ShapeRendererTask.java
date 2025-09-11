package com.wizardlybump17.physics.minecraft.task;

import com.wizardlybump17.physics.minecraft.renderer.shape.ShapeRenderer;
import com.wizardlybump17.physics.minecraft.renderer.shape.WorldShapeRenderer;
import com.wizardlybump17.physics.three.container.ShapesGroupsContainer;
import com.wizardlybump17.physics.three.group.ContainerShapesGroup;
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
                if (renderer instanceof WorldShapeRenderer playerShapeRenderer && playerShapeRenderer.getContainer().getId().equals(containerId))
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
            if (renderer instanceof WorldShapeRenderer playerShapeRenderer && playerShapeRenderer.getContainer().getId().equals(containerId))
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
                if (renderer instanceof WorldShapeRenderer worldShapeRenderer)
                    renderWorldRenderer(worldShapeRenderer);
            }
        }
    }

    protected void renderWorldRenderer(@NotNull WorldShapeRenderer renderer) {
        for (Player viewer : renderer.getViewers()) {
            ShapesGroupsContainer container = renderer.getContainer();
            for (ContainerShapesGroup group : container.getGroups()) {
                for (Shape shape : group.getShapes().values()) {
                    if (renderer.isValidShape(shape))
                        renderer.render(viewer, shape);
                }
            }
        }
    }


    public void addViewer(@NotNull Player viewer) {
        renderers.values().forEach(renderers -> renderers.forEach(renderer -> renderer.addViewer(viewer)));
    }

    public void removeViewer(@NotNull Player viewer) {
        renderers.values().forEach(renderers -> renderers.forEach(renderer -> renderer.removeViewer(viewer)));
    }

    public boolean hasViewer(@NotNull Player viewer) {
        return renderers.values().stream().anyMatch(renderers -> renderers.stream().anyMatch(renderer -> renderer.hasViewer(viewer)));
    }
}
