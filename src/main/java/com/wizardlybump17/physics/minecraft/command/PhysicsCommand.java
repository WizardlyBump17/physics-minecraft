package com.wizardlybump17.physics.minecraft.command;

import com.wizardlybump17.physics.minecraft.Converter;
import com.wizardlybump17.physics.minecraft.debug.DebugObjectContainer;
import com.wizardlybump17.physics.minecraft.debug.object.DebugObjectGroup;
import com.wizardlybump17.physics.minecraft.renderer.shape.CubeRenderer;
import com.wizardlybump17.physics.minecraft.renderer.shape.RotatingCubeRenderer;
import com.wizardlybump17.physics.minecraft.renderer.shape.SphereRenderer;
import com.wizardlybump17.physics.minecraft.task.ShapeRendererTask;
import com.wizardlybump17.physics.three.Engine;
import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.group.PhysicsShapesGroup;
import com.wizardlybump17.physics.three.group.ShapesGroup;
import com.wizardlybump17.physics.three.registry.BaseObjectContainerRegistry;
import com.wizardlybump17.physics.three.shape.Cube;
import com.wizardlybump17.physics.three.shape.Shape;
import com.wizardlybump17.physics.three.shape.Sphere;
import com.wizardlybump17.physics.three.shape.rotating.RotatingCube;
import net.kyori.adventure.text.Component;
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
import java.util.UUID;

public class PhysicsCommand implements CommandExecutor, TabCompleter {

    private final @NotNull ShapeRendererTask shapeRendererTask;
    private final @NotNull Engine engine;

    public PhysicsCommand(@NotNull ShapeRendererTask shapeRendererTask, @NotNull Engine engine) {
        this.shapeRendererTask = shapeRendererTask;
        this.engine = engine;
    }

    public @NotNull ShapeRendererTask getShapeRendererTask() {
        return shapeRendererTask;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || !(sender instanceof Player player))
            return false;

        BaseObjectContainerRegistry containerRegistry = engine.getObjectContainerRegistry();
        World world = player.getWorld();

        DebugObjectContainer container = (DebugObjectContainer) containerRegistry.get(DebugObjectContainer.getId(world))
                .orElseGet(() -> {
                    DebugObjectContainer newContainer = new DebugObjectContainer(world);
                    containerRegistry.register(newContainer);

                    shapeRendererTask.addRenderer(new CubeRenderer(newContainer));
                    shapeRendererTask.addRenderer(new SphereRenderer(newContainer));
                    shapeRendererTask.addRenderer(new RotatingCubeRenderer(newContainer));
                    return newContainer;
                });

        switch (args[0].toLowerCase()) {
            case "debug" -> {
                if (args.length == 1)
                    return false;

                switch (args[1].toLowerCase()) {
                    case "clear" -> {
                        List<ShapesGroup> groups = List.copyOf(container.getShapesGroups());
                        for (ShapesGroup group : groups)
                            container.removeGroup(group);
                    }
                    case "follow" -> {
                        if (args.length == 2)
                            return false;

                        switch (args[2].toLowerCase()) {
                            case "start" -> {
                                String type = args.length == 4 ? args[3] : "cube";
                                spawnDebugObjects(player, container, true, type);
                            }
                            case "toggle" -> {
                                if (args.length == 4) {
                                    try {
                                        ShapesGroup group = container.getGroup(Integer.parseInt(args[3]));
                                        if (group instanceof DebugObjectGroup debugGroup)
                                            debugGroup.setFollowing(!debugGroup.isFollowing());
                                        player.sendMessage(Component.text("Following: " + (group instanceof DebugObjectGroup debugGroup && debugGroup.isFollowing())));
                                    } catch (NumberFormatException e) {
                                        player.sendMessage(Component.text("Invalid id."));
                                    }
                                    return false;
                                }

                                for (ShapesGroup group : container.getShapesGroups()) {
                                    if (group instanceof DebugObjectGroup debugGroup)
                                        debugGroup.setFollowing(!debugGroup.isFollowing());
                                }
                            }
                        }
                    }
                    case "static" -> {
                        String type = args.length == 3 ? args[2] : "cube";
                        spawnDebugObjects(player, container, false, type);
                    }
                    case "physics" -> {
                        container.addGroup(new PhysicsShapesGroup(
                                container,
                                List.of(new Sphere(Converter.convert(player.getEyeLocation().toVector()), 1)),
                                new Vector3D(0, -9.8, 0).inMetersPerTick(),
                                Vector3D.ZERO
                        ) {
                            @Override
                            public boolean isPassable() {
                                return false;
                            }

                            @Override
                            public void tick() {
                                super.tick();

                                System.out.println(getCenter());
                            }
                        });
                        shapeRendererTask.getRenderers(Sphere.class, container.getId()).stream().findFirst().ifPresent(renderer -> {
                            renderer.addViewer(player);
                        });
                    }

                    case "movement-check" -> {
                        if (args.length == 2)
                            return false;

                        switch (args[2].toLowerCase()) {
                            case "toggle" -> {
                                if (args.length == 4) {
                                    try {
                                        ShapesGroup group = container.getGroup(Integer.parseInt(args[3]));
                                        if (group instanceof DebugObjectGroup debugGroup)
                                            debugGroup.setCheckMaxMovement(!debugGroup.isCheckMaxMovement());
                                        player.sendMessage(Component.text("Checking max movement: " + (group instanceof DebugObjectGroup debugGroup && debugGroup.isCheckMaxMovement())));
                                    } catch (NumberFormatException e) {
                                        player.sendMessage(Component.text("Invalid id."));
                                    }
                                    return false;
                                }

                                for (ShapesGroup group : container.getShapesGroups()) {
                                    if (group instanceof DebugObjectGroup debugGroup)
                                        debugGroup.setFollowing(!debugGroup.isCheckMaxMovement());
                                }
                            }
                        }
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

    public static @NotNull Cube getDebugCube(@NotNull Player player) {
        Location location = player.getLocation();
        return new Cube(
                Converter.convert(location.toVector()),
                Converter.convert(location.toVector()).add(1, 1, 1)
        );
    }

    public static @NotNull Sphere getDebugSphere(@NotNull Player player) {
        Location location = player.getLocation();
        return new Sphere(
                Converter.convert(location.toVector()),
                0.5
        );
    }

    public static @NotNull RotatingCube getDebugRotatingCube(@NotNull Player player) {
        Location location = player.getLocation();
        return new RotatingCube(
                Converter.convert(location.toVector()),
                List.of(
                        new Vector3D(-2, 1, -1),
                        new Vector3D(-2, 1, 1),
                        new Vector3D(2, 1, -1),
                        new Vector3D(2, 1, 1),
                        new Vector3D(-2, -1, -1),
                        new Vector3D(-2, -1, 1),
                        new Vector3D(2, -1, -1),
                        new Vector3D(2, -1, 1),
                        new Vector3D(2, -3, 1)
                ),
                Vector3D.ZERO
        );
    }

    public void spawnDebugObjects(@NotNull Player player, @NotNull DebugObjectContainer container, boolean follow, @NotNull String type) {
        UUID containerId = container.getId();

        Shape shape = switch (type.toLowerCase()) {
            case "cube" -> getDebugCube(player);
            case "sphere" -> getDebugSphere(player);
            case "rotating-cube" -> getDebugRotatingCube(player);
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };

        shapeRendererTask.getRenderers(shape.getClass(), containerId).stream().findFirst().ifPresent(renderer -> {
            DebugObjectGroup group = new DebugObjectGroup(List.of(shape, shape.at(shape.getPosition().add(3, 0, 0))), container, player, follow, false);
            engine.getScheduler().schedule(() -> container.addGroup(group));

            renderer.addViewer(player);

            player.sendMessage(Component.text(group.getId()));
        });
    }
}
