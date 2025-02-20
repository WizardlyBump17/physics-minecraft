package com.wizardlybump17.physics.minecraft.renderer;

import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.shape.Cube;
import org.bukkit.Color;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class CubeRenderer extends WorldShapeRenderer<Cube> {

    public static final @NotNull Color COLOR = Color.AQUA;
    public static final float SIZE = 0.1F;

    public CubeRenderer(@NotNull World world) {
        super(world);
    }

    @Override
    public void render(@NotNull Cube shape) {
        Vector3D min = shape.getMin();
        Vector3D max = shape.getMax();
        for (double x = min.x(); x <= max.x(); x += 0.1) {
            for (double y = min.y(); y <= max.y(); y += 0.1) {
                for (double z = min.z(); z <= max.z(); z += 0.1) {
                    drawPoint(x, y, z, COLOR, SIZE);
                }
            }
        }
    }
}
