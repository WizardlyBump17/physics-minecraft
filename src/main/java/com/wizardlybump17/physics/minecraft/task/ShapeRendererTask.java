package com.wizardlybump17.physics.minecraft.task;

import com.wizardlybump17.physics.minecraft.renderer.ShapeRenderer;
import com.wizardlybump17.physics.minecraft.renderer.WorldShapeRenderer;
import com.wizardlybump17.physics.three.object.BaseObject;
import com.wizardlybump17.physics.three.registry.BaseObjectContainerRegistry;
import com.wizardlybump17.physics.three.shape.Shape;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class ShapeRendererTask extends BukkitRunnable {

    private final @NotNull Map<UUID, ShapeRenderer<? super Shape>> renderers = new HashMap<>();
    private final @NotNull BaseObjectContainerRegistry registry;

    public ShapeRendererTask(@NotNull BaseObjectContainerRegistry registry) {
        this.registry = registry;
    }

    public void addRenderer(@NotNull ShapeRenderer<? super Shape> renderer) {
        renderers.put(renderer.getRendererId(), renderer);
    }

    public @NotNull Optional<ShapeRenderer<? super Shape>> getRenderer(@NotNull UUID id) {
        return Optional.ofNullable(renderers.get(id));
    }

    public @NotNull ShapeRenderer<? super Shape> getRendererOrAdd(@NotNull UUID id, @NotNull Supplier<ShapeRenderer<? super Shape>> supplier) {
        return renderers.computeIfAbsent(id, $ -> supplier.get());
    }

    public @NotNull Optional<ShapeRenderer<? super Shape>> removeRenderer(@NotNull UUID id) {
        return Optional.ofNullable(renderers.remove(id));
    }

    public boolean hasRenderer(@NotNull UUID id) {
        return renderers.containsKey(id);
    }

    public @NotNull Map<UUID, ShapeRenderer<? super Shape>> getRenderers() {
        return Collections.unmodifiableMap(renderers);
    }

    public void clear() {
        renderers.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        for (ShapeRenderer<? super Shape> renderer : renderers.values()) {
            if (renderer instanceof WorldShapeRenderer<?> worldShapeRenderer)
                renderWorldRenderer((WorldShapeRenderer<? super Shape>) worldShapeRenderer);
        }
    }

    protected void renderWorldRenderer(@NotNull WorldShapeRenderer<? super Shape> renderer) {
        Class<? extends Shape> shapeType = renderer.getShapeType();
        registry.get(renderer.getWorld().getUID()).ifPresent(container -> {
            for (BaseObject object : container.getLoadedObjects()) {
                if (object.getClass().equals(shapeType))
                    renderer.render(shapeType.cast(object));
            }
        });
    }

    public @NotNull BaseObjectContainerRegistry getRegistry() {
        return registry;
    }
}
