package com.wizardlybump17.physics.minecraft.task;

import com.wizardlybump17.physics.minecraft.renderer.ShapeRenderer;
import com.wizardlybump17.physics.minecraft.renderer.WorldShapeRenderer;
import com.wizardlybump17.physics.three.Engine;
import com.wizardlybump17.physics.three.object.BaseObject;
import com.wizardlybump17.physics.three.registry.BaseObjectContainerRegistry;
import com.wizardlybump17.physics.three.shape.Shape;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ShapeRendererTask extends BukkitRunnable {

    private final @NotNull Set<ShapeRenderer<? super Shape>> renderers = new HashSet<>();

    public void addRenderer(@NotNull ShapeRenderer<? super Shape> renderer) {
        renderers.add(renderer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        for (ShapeRenderer<? super Shape> renderer : renderers) {
            if (renderer instanceof WorldShapeRenderer<?> worldShapeRenderer)
                renderWorldRenderer((WorldShapeRenderer<? super Shape>) worldShapeRenderer);
        }
    }

    protected void renderWorldRenderer(@NotNull WorldShapeRenderer<? super Shape> renderer) {
        Class<? extends Shape> shapeType = renderer.getShapeType();
        BaseObjectContainerRegistry registry = Engine.getObjectContainerRegistry();
        registry.get(renderer.getWorld().getUID()).ifPresent(container -> {
            for (BaseObject object : container.getLoadedObjects()) {
                if (object.getClass().equals(shapeType))
                    renderer.render(shapeType.cast(object));
            }
        });
    }
}
