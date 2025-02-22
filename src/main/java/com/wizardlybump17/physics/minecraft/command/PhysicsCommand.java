package com.wizardlybump17.physics.minecraft.command;

import com.wizardlybump17.physics.minecraft.Converter;
import com.wizardlybump17.physics.minecraft.renderer.PlayerShapeRenderer;
import com.wizardlybump17.physics.minecraft.renderer.ShapeRenderer;
import com.wizardlybump17.physics.minecraft.task.ShapeRendererTask;
import com.wizardlybump17.physics.three.container.BaseObjectContainer;
import com.wizardlybump17.physics.three.object.BaseObject;
import com.wizardlybump17.physics.three.object.BasicObject;
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
import java.util.function.BooleanSupplier;

public class PhysicsCommand implements CommandExecutor, TabCompleter {

    private final @NotNull ShapeRendererTask shapeRendererTask;
    private boolean paused;

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

        switch (args[0].toLowerCase()) {
            case "debug" -> {
                if (args.length == 1) {
                    shapeRendererTask.getRenderers(Cube.class).stream().findFirst().ifPresent(renderer -> {
                        BaseObjectContainer container = ((PlayerShapeRenderer) renderer).getContainer();

                        BaseObject object = getDebugCube(player, container, () -> paused);
                        container.addObject(object);

                        renderer.addViewer(player);
                    });
                    shapeRendererTask.getRenderers(Sphere.class).stream().findFirst().ifPresent(renderer -> {
                        BaseObjectContainer container = ((PlayerShapeRenderer) renderer).getContainer();

                        BaseObject object = getDebugSphere(player, container, () -> paused);
                        container.addObject(object);

                        renderer.addViewer(player);
                    });
                    return false;
                }

                if (args.length == 2) {
                    switch (args[1].toLowerCase()) {
                        case "stop" -> {
                            for (Set<ShapeRenderer> renderers : shapeRendererTask.getRenderers().values())
                                for (ShapeRenderer renderer : renderers)
                                    renderer.removeViewer(player);
                        }
                        case "pause" -> paused = !paused;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }

    public static @NotNull BaseObject getDebugCube(@NotNull Player player, @NotNull BaseObjectContainer container, @NotNull BooleanSupplier pausedSupplier) {
        Location location = player.getLocation();
        World world = player.getWorld();
        return new BasicObject(new Cube(Converter.convert(location.toVector()), Converter.convert(location.toVector()).add(1, 1, 1)), container) {
            @Override
            public void tick() {
                tickDebugObject(this, player, world, pausedSupplier);
            }
        };
    }

    public static @NotNull BaseObject getDebugSphere(@NotNull Player player, @NotNull BaseObjectContainer container, @NotNull BooleanSupplier pausedSupplier) {
        Location location = player.getLocation();
        World world = player.getWorld();
        return new BasicObject(new Sphere(Converter.convert(location.toVector()), 0.5), container) {
            @Override
            public void tick() {
                tickDebugObject(this, player, world, pausedSupplier);
            }
        };
    }

    public static void tickDebugObject(@NotNull BaseObject object, @NotNull Player player, @NotNull World originalWorld, @NotNull BooleanSupplier pausedSupplier) {
        if (pausedSupplier.getAsBoolean())
            return;

        Location currentLocation = player.getEyeLocation();
        if (!currentLocation.getWorld().equals(originalWorld))
            return;

        object.teleport(Converter.convert(currentLocation.add(currentLocation.getDirection().multiply(3)).toVector()));
    }
}
