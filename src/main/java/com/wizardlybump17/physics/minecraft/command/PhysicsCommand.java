package com.wizardlybump17.physics.minecraft.command;

import com.wizardlybump17.physics.minecraft.Converter;
import com.wizardlybump17.physics.minecraft.debug.DebugObjectContainer;
import com.wizardlybump17.physics.minecraft.debug.object.DebugObject;
import com.wizardlybump17.physics.minecraft.renderer.object.BasicObjectRenderer;
import com.wizardlybump17.physics.minecraft.renderer.object.ObjectRenderer;
import com.wizardlybump17.physics.minecraft.task.ShapeRendererTask;
import com.wizardlybump17.physics.three.Engine;
import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.object.BaseObject;
import com.wizardlybump17.physics.three.object.PhysicsObject;
import com.wizardlybump17.physics.three.registry.BaseObjectContainerRegistry;
import com.wizardlybump17.physics.three.shape.Cube;
import com.wizardlybump17.physics.three.shape.Sphere;
import com.wizardlybump17.physics.three.shape.rotating.RotatingCube;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

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

                    shapeRendererTask.addRenderer(new BasicObjectRenderer(newContainer));
                    return newContainer;
                });

        switch (args[0].toLowerCase()) {
            case "debug" -> {
                if (args.length == 1)
                    return false;

                switch (args[1].toLowerCase()) {
                    case "clear" -> {
                        for (Set<ObjectRenderer> renderers : shapeRendererTask.getRenderers(container.getId()).values()) {
                            for (ObjectRenderer renderer : renderers) {
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
                            case "start" -> {
                                String type = args.length == 4 ? args[3] : "cube";
                                spawnDebugObjects(player, container, true, type);
                            }
                            case "toggle" -> {
                                for (BaseObject object : container.getLoadedObjects()) {
                                    if (object instanceof DebugObject debugObject)
                                        debugObject.setFollowing(!debugObject.isFollowing());
                                }
                            }
                        }
                    }
                    case "static" -> {
                        String type = args.length == 3 ? args[2] : "cube";
                        spawnDebugObjects(player, container, false, type);
                    }
                    case "physics" -> {
                        container.addObject(new PhysicsObject(
                                new Sphere(
                                        Converter.convert(player.getEyeLocation().toVector()),
                                        1
                                ),
                                container,
                                new Vector3D(0, -9.8, 0),
                                Vector3D.ZERO
                        ));
                        shapeRendererTask.getRenderers(BaseObject.class, container.getId()).stream().findFirst().ifPresent(renderer -> {
                            renderer.addViewer(player);
                        });
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

    public static @NotNull DebugObject getDebugCube(@NotNull Player player, @NotNull DebugObjectContainer container, boolean follow) {
        Location location = player.getLocation();
        return new DebugObject(
                new Cube(
                        Converter.convert(location.toVector()),
                        follow ? Converter.convert(location.toVector()).add(0.3, 0.3, 0.3) : Converter.convert(location.toVector()).add(1, 1, 1)
                ),
                container,
                player,
                follow);
    }

    public static @NotNull DebugObject getDebugSphere(@NotNull Player player, @NotNull DebugObjectContainer container, boolean follow) {
        Location location = player.getLocation();
        return new DebugObject(
                new Sphere(
                        Converter.convert(location.toVector()),
                        0.5
                ),
                container,
                player,
                follow
        );
    }

    public static @NotNull DebugObject getDebugRotatingCube(@NotNull Player player, @NotNull DebugObjectContainer container, boolean follow) {
        Location location = player.getLocation();
        return new DebugObject(
                new RotatingCube(
                        Converter.convert(location.toVector()),
                        sortPolygonPoints(List.of(
                                new Vector3D(-2, 1, -1),
                                new Vector3D(-2, 1, 1),
                                new Vector3D(2, 1, -1),
                                new Vector3D(2, 1, 1),
                                new Vector3D(-2, -1, -1),
                                new Vector3D(-2, -1, 1),
                                new Vector3D(2, -1, -1),
                                new Vector3D(2, -1, 1),
                                new Vector3D(2, -3, 1)
                        ), false),
                        Vector3D.ZERO
                ),
                container,
                player,
                follow
        );
    }

    public static List<Vector3D> sortPolygonPoints(List<Vector3D> points, boolean reversed) {
        // Step 1: Centroid
        Vector3D centroid = points.stream()
                .reduce(new Vector3D(0, 0, 0), Vector3D::add)
                .multiply(1.0 / points.size());

        // Step 2: Normal (use cross product of two edges)
        Vector3D edge1 = points.get(1).subtract(points.get(0));
        Vector3D edge2 = points.get(2).subtract(points.get(0));
        Vector3D normal = crossProduct(edge1, edge2).normalize();

        // Step 3: Basis vectors (u, v) on the plane
        Vector3D u = edge1.normalize();
        Vector3D v = crossProduct(normal, u); // perpendicular to both u and normal

        // Step 4: Compute angles from centroid
        List<PointWithAngle> angles = new ArrayList<>();
        for (Vector3D p : points) {
            Vector3D rel = p.subtract(centroid);
            double x = dotProduct(rel, u);
            double y = dotProduct(rel, v);
            double angle = Math.atan2(y, x);
            angles.add(new PointWithAngle(p, angle));
        }

        // Step 5: Sort by angle
        angles.sort(reversed
                ? Comparator.comparingDouble(a -> ((PointWithAngle) a).angle).reversed()
                : Comparator.comparingDouble(a -> a.angle)
        );

        return angles.stream().map(pa -> pa.point).collect(Collectors.toList());
    }

    public static double dotProduct(@NotNull Vector3D a, @NotNull Vector3D b) {
        return a.x() * b.x() + a.y() * b.y() + a.z() * b.z();
    }

    public static @NotNull Vector3D crossProduct(@NotNull Vector3D a, @NotNull Vector3D b) {
        return new Vector3D(
                a.y() * b.z() - a.z() * b.y(),
                a.z() * b.x() - a.x() * b.z(),
                a.x() * b.y() - a.y() * b.x()
        );
    }

    private static class PointWithAngle {
        Vector3D point;
        double angle;

        PointWithAngle(Vector3D point, double angle) {
            this.point = point;
            this.angle = angle;
        }
    }

    public void spawnDebugObjects(@NotNull Player player, @NotNull DebugObjectContainer container, boolean follow, @NotNull String type) {
        UUID containerId = container.getId();

        shapeRendererTask.getRenderers(BaseObject.class, containerId).stream().findFirst().ifPresent(renderer -> {
            BaseObject object = switch (type.toLowerCase()) {
                case "cube" -> getDebugCube(player, container, follow);
                case "sphere" -> getDebugSphere(player, container, follow);
                case "rotating-cube" -> getDebugRotatingCube(player, container, follow);
                default -> throw new IllegalArgumentException("Invalid type: " + type);
            };

            container.addObject(object);

            renderer.addViewer(player);
        });
    }
}
