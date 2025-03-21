package com.wizardlybump17.physics.minecraft.task;

import com.wizardlybump17.physics.minecraft.renderer.object.ObjectRenderer;
import com.wizardlybump17.physics.minecraft.renderer.object.WorldObjectRenderer;
import com.wizardlybump17.physics.three.container.BaseObjectContainer;
import com.wizardlybump17.physics.three.object.BaseObject;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShapeRendererTask extends BukkitRunnable {

    private final @NotNull Map<Class<? extends BaseObject>, Set<ObjectRenderer>> renderers = new HashMap<>();

    public void addRenderer(@NotNull ObjectRenderer renderer) {
        renderers.computeIfAbsent(renderer.getObjectType(), $ -> new HashSet<>()).add(renderer);
    }

    public void removeRenderer(@NotNull ObjectRenderer renderer) {
        Set<ObjectRenderer> renderers = this.renderers.get(renderer.getObjectType());
        if (renderers != null)
            renderers.remove(renderer);
    }

    public boolean hasRenderer(@NotNull ObjectRenderer renderer) {
        return renderers.getOrDefault(renderer.getObjectType(), Collections.emptySet()).contains(renderer);
    }

    public @NotNull Map<Class<? extends BaseObject>, Set<ObjectRenderer>> getRenderers() {
        return Collections.unmodifiableMap(renderers);
    }

    public @NotNull Map<Class<? extends BaseObject>, Set<ObjectRenderer>> getRenderers(@NotNull UUID containerId) {
        Map<Class<? extends BaseObject>, Set<ObjectRenderer>> renderers = new HashMap<>();
        this.renderers.forEach((type, renderersSet) -> {
            for (ObjectRenderer renderer : renderersSet) {
                if (renderer instanceof WorldObjectRenderer playerShapeRenderer && playerShapeRenderer.getContainer().getId().equals(containerId))
                    renderers.computeIfAbsent(type, $ -> new HashSet<>()).add(renderer);
            }
        });
        return renderers;
    }

    public @NotNull Set<ObjectRenderer> getRenderers(@NotNull Class<? extends BaseObject> type) {
        return renderers.getOrDefault(type, Set.of());
    }

    public @NotNull Set<ObjectRenderer> getRenderers(@NotNull Class<? extends BaseObject> type, @NotNull UUID containerId) {
        Set<ObjectRenderer> renderers = new HashSet<>();
        for (ObjectRenderer renderer : this.renderers.getOrDefault(type, Set.of()))
            if (renderer instanceof WorldObjectRenderer playerShapeRenderer && playerShapeRenderer.getContainer().getId().equals(containerId))
                renderers.add(renderer);
        return renderers;
    }

    public void clear() {
        renderers.clear();
    }

    @Override
    public void run() {
        for (Set<ObjectRenderer> renderers : renderers.values()) {
            for (ObjectRenderer renderer : renderers) {
                if (renderer instanceof WorldObjectRenderer worldShapeRenderer)
                    renderWorldRenderer(worldShapeRenderer);
            }
        }
    }

    protected void renderWorldRenderer(@NotNull WorldObjectRenderer renderer) {
        for (Player viewer : renderer.getViewers()) {
            BaseObjectContainer container = renderer.getContainer();
            for (BaseObject object : container.getLoadedObjects()) {
                if (renderer.isValidObject(object))
                    renderer.render(viewer, object);
            }
        }
    }
}
