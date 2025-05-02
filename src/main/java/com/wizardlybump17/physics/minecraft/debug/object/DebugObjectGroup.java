package com.wizardlybump17.physics.minecraft.debug.object;

import com.wizardlybump17.physics.minecraft.Converter;
import com.wizardlybump17.physics.minecraft.debug.DebugObjectContainer;
import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.group.PhysicsObjectsGroup;
import com.wizardlybump17.physics.three.object.BaseObject;
import com.wizardlybump17.physics.three.shape.Cube;
import io.papermc.paper.util.CollisionUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DebugObjectGroup extends PhysicsObjectsGroup {

    private final @NotNull Player player;
    private boolean following = true;
    private boolean canFollow;
    private boolean checkMaxMovement;

    public DebugObjectGroup(@NotNull List<BaseObject> objects, @NotNull DebugObjectContainer container, @NotNull Player player, boolean canFollow, boolean checkMaxMovement) {
        super(container, objects);
        this.player = player;
        this.canFollow = canFollow;
        this.checkMaxMovement = checkMaxMovement;
    }

    @Override
    public boolean isPassable() {
        return true;
    }

    @Override
    public void tick() {
        DebugObjectContainer container = (DebugObjectContainer) getContainer();

        if (!checkMaxMovement)
            super.tick();

        if (!canFollow || !following)
            return;

        Location playerEyeLocation = player.getEyeLocation();
        if (!playerEyeLocation.getWorld().equals(container.getWorld()))
            return;

        Vector3D targetPosition = Converter.convert(playerEyeLocation.add(playerEyeLocation.getDirection().multiply(3)).toVector());
        Vector3D currentPosition = getCenter();

        if (checkMaxMovement) {
            setVelocity(targetPosition.subtract(currentPosition));
            super.tick();
        } else {
            setVelocity(Vector3D.ZERO);
            setCenter(targetPosition);
        }
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isCanFollow() {
        return canFollow;
    }

    public void setCanFollow(boolean canFollow) {
        this.canFollow = canFollow;
    }

    public boolean isCheckMaxMovement() {
        return checkMaxMovement;
    }

    public void setCheckMaxMovement(boolean checkMaxMovement) {
        this.checkMaxMovement = checkMaxMovement;
    }

    @Override
    public @NotNull Vector3D getMaxMovement(@NotNull Vector3D movement) {
        if (movement.lengthSquared() == 0)
            return movement;

        Vector3D center = getCenter();
        Vector3D closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (BaseObject object : getObjects().values()) {
            if (!(object.getShape() instanceof Cube cube))
                continue;

            Vector3D maxMovement = convert(collide(convert(movement), cube));
            Vector3D candidate = center.add(maxMovement);

            double distance = candidate.distanceSquared(center);

            if (distance < closestDistance) {
                closest = maxMovement;
                closestDistance = distance;
            }
        }

        return closest == null ? movement : closest;
    }

    public static @NotNull Vec3 convert(@NotNull Vector3D original) {
        return new Vec3(original.x(), original.y(), original.z());
    }

    public static @NotNull Vector3D convert(@NotNull Vec3 original) {
        return new Vector3D(original.x, original.y, original.z);
    }

    //taken from the nms from paper
    private Vec3 collide(Vec3 movement, @NotNull Cube cube) {
        // Paper start - optimise collisions
        final boolean xZero = movement.x == 0.0;
        final boolean yZero = movement.y == 0.0;
        final boolean zZero = movement.z == 0.0;
        if (xZero & yZero & zZero) {
            return movement;
        }

        final Level world = ((CraftWorld) ((DebugObjectContainer) getContainer()).getWorld()).getHandle();
        final AABB currBoundingBox = new AABB(convert(cube.getMin()), convert(cube.getMax()));

        if (CollisionUtil.isEmpty(currBoundingBox)) {
            return movement;
        }

        final List<AABB> potentialCollisionsBB = new ArrayList<>();
        final List<VoxelShape> potentialCollisionsVoxel = new ArrayList<>();
//        final double stepHeight = (double)this.maxUpStep();
        double stepHeight = 1;
        final AABB collisionBox;
//        final boolean onGround = this.onGround;
        boolean onGround = true;

        if (xZero & zZero) {
            if (movement.y > 0.0) {
                collisionBox = CollisionUtil.cutUpwards(currBoundingBox, movement.y);
            } else {
                collisionBox = CollisionUtil.cutDownwards(currBoundingBox, movement.y);
            }
        } else {
            // note: xZero == false or zZero == false
            if (stepHeight > 0.0 && (onGround || (movement.y < 0.0))) {
                // don't bother getting the collisions if we don't need them.
                if (movement.y <= 0.0) {
                    collisionBox = CollisionUtil.expandUpwards(currBoundingBox.expandTowards(movement.x, movement.y, movement.z), stepHeight);
                } else {
                    collisionBox = currBoundingBox.expandTowards(movement.x, Math.max(stepHeight, movement.y), movement.z);
                }
            } else {
                collisionBox = currBoundingBox.expandTowards(movement.x, movement.y, movement.z);
            }
        }

        CollisionUtil.getCollisions(
                world, null, collisionBox, potentialCollisionsVoxel, potentialCollisionsBB,
                CollisionUtil.COLLISION_FLAG_CHECK_BORDER,
                null, null
        );

        if (potentialCollisionsVoxel.isEmpty() && potentialCollisionsBB.isEmpty()) {
            return movement;
        }

        final Vec3 limitedMoveVector = CollisionUtil.performCollisions(movement, currBoundingBox, potentialCollisionsVoxel, potentialCollisionsBB);

        if (stepHeight > 0.0
                && (onGround || (limitedMoveVector.y != movement.y && movement.y < 0.0))
                && (limitedMoveVector.x != movement.x || limitedMoveVector.z != movement.z)) {
            Vec3 vec3d2 = CollisionUtil.performCollisions(new Vec3(movement.x, stepHeight, movement.z), currBoundingBox, potentialCollisionsVoxel, potentialCollisionsBB);
            final Vec3 vec3d3 = CollisionUtil.performCollisions(new Vec3(0.0, stepHeight, 0.0), currBoundingBox.expandTowards(movement.x, 0.0, movement.z), potentialCollisionsVoxel, potentialCollisionsBB);

            if (vec3d3.y < stepHeight) {
                final Vec3 vec3d4 = CollisionUtil.performCollisions(new Vec3(movement.x, 0.0D, movement.z), currBoundingBox.move(vec3d3), potentialCollisionsVoxel, potentialCollisionsBB).add(vec3d3);

                if (vec3d4.horizontalDistanceSqr() > vec3d2.horizontalDistanceSqr()) {
                    vec3d2 = vec3d4;
                }
            }

            if (vec3d2.horizontalDistanceSqr() > limitedMoveVector.horizontalDistanceSqr()) {
                return vec3d2.add(CollisionUtil.performCollisions(new Vec3(0.0D, -vec3d2.y + movement.y, 0.0D), currBoundingBox.move(vec3d2), potentialCollisionsVoxel, potentialCollisionsBB));
            }

            return limitedMoveVector;
        } else {
            return limitedMoveVector;
        }
        // Paper end - optimise collisions
    }
}
