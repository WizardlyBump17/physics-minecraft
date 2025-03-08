package com.wizardlybump17.physics.minecraft.command;

import com.wizardlybump17.physics.minecraft.Converter;
import com.wizardlybump17.physics.minecraft.debug.DebugObjectContainer;
import com.wizardlybump17.physics.minecraft.debug.object.DebugObject;
import com.wizardlybump17.physics.minecraft.renderer.CubeRenderer;
import com.wizardlybump17.physics.minecraft.renderer.ShapeRenderer;
import com.wizardlybump17.physics.minecraft.renderer.SphereRenderer;
import com.wizardlybump17.physics.minecraft.task.ShapeRendererTask;
import com.wizardlybump17.physics.three.Engine;
import com.wizardlybump17.physics.three.object.BaseObject;
import com.wizardlybump17.physics.three.registry.BaseObjectContainerRegistry;
import com.wizardlybump17.physics.three.shape.Cube;
import com.wizardlybump17.physics.three.shape.Sphere;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PhysicsCommand implements CommandExecutor, TabCompleter {

    private final @NotNull ShapeRendererTask shapeRendererTask;

    public PhysicsCommand(@NotNull ShapeRendererTask shapeRendererTask) {
        this.shapeRendererTask = shapeRendererTask;
    }

    public @NotNull ShapeRendererTask getShapeRendererTask() {
        return shapeRendererTask;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || !(sender instanceof Player player))
            return false;

        BaseObjectContainerRegistry containerRegistry = Engine.getObjectContainerRegistry();
        World world = player.getWorld();

        DebugObjectContainer container = (DebugObjectContainer) containerRegistry.get(DebugObjectContainer.getId(world))
                .orElseGet(() -> {
                    DebugObjectContainer newContainer = new DebugObjectContainer(world);
                    containerRegistry.register(newContainer);

                    shapeRendererTask.addRenderer(new CubeRenderer(newContainer));
                    shapeRendererTask.addRenderer(new SphereRenderer(newContainer));
                    return newContainer;
                });

        switch (args[0].toLowerCase()) {
            case "debug" -> {
                if (args.length == 1)
                    return false;

                switch (args[1].toLowerCase()) {
                    case "clear" -> {
                        for (Set<ShapeRenderer> renderers : shapeRendererTask.getRenderers(container.getId()).values()) {
                            for (ShapeRenderer renderer : renderers) {
                                renderer.removeViewer(player);
                                for (BaseObject object : container.getLoadedObjects())
                                    container.removeObject(object.getId());
                            }
                        }
                    }
                    case "follow" -> {
                        if (args.length == 2)
                            return false;

                        switch (args[2].toLowerCase()) {
                            case "start" -> spawnDebugObjects(player, container, true);
                            case "toggle" -> {
                                for (BaseObject object : container.getLoadedObjects()) {
                                    if (object instanceof DebugObject debugObject)
                                        debugObject.setFollowing(!debugObject.isFollowing());
                                }
                            }
                        }
                    }
                    case "static" -> spawnDebugObjects(player, container, false);
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }

    public static @NotNull DebugObject getDebugCube(@NotNull Player player, @NotNull DebugObjectContainer container, boolean follow) {
        Location location = player.getLocation();
        return new DebugObject(new Cube(Converter.convert(location.toVector()), Converter.convert(location.toVector()).add(1, 1, 1)), container, player, follow);
    }

    public static @NotNull DebugObject getDebugSphere(@NotNull Player player, @NotNull DebugObjectContainer container, boolean follow) {
        Location location = player.getLocation();
        return new DebugObject(new Sphere(Converter.convert(location.toVector()), 0.5), container, player, follow);
    }

    public void spawnDebugObjects(@NotNull Player player, @NotNull DebugObjectContainer container, boolean follow) {
        UUID containerId = container.getId();

        shapeRendererTask.getRenderers(Cube.class, containerId).stream().findFirst().ifPresent(renderer -> {
            DebugObject object = getDebugCube(player, container, follow);
            container.addObject(object);

            renderer.addViewer(player);
        });
        shapeRendererTask.getRenderers(Sphere.class, containerId).stream().findFirst().ifPresent(renderer -> {
            DebugObject object = getDebugSphere(player, container, follow);
            container.addObject(object);

            renderer.addViewer(player);
        });
    }
}
