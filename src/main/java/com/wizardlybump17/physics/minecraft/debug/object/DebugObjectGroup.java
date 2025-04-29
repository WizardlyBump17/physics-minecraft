package com.wizardlybump17.physics.minecraft.debug.object;

import com.wizardlybump17.physics.minecraft.Converter;
import com.wizardlybump17.physics.minecraft.debug.DebugObjectContainer;
import com.wizardlybump17.physics.three.Vector3D;
import com.wizardlybump17.physics.three.group.PhysicsObjectsGroup;
import com.wizardlybump17.physics.three.object.BaseObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DebugObjectGroup extends PhysicsObjectsGroup {

    private final @NotNull Player player;
    private boolean following = true;
    private boolean canFollow;
    private boolean checkMaxMovement;

    public DebugObjectGroup(@NotNull BaseObject object, @NotNull DebugObjectContainer container, @NotNull Player player, boolean canFollow, boolean checkMaxMovement) {
        super(container, List.of(object));
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

        super.tick();

        if (!canFollow || !following)
            return;

        Location playerEyeLocation = player.getEyeLocation();
        if (!playerEyeLocation.getWorld().equals(container.getWorld()))
            return;

        Vector3D targetPosition = Converter.convert(playerEyeLocation.add(playerEyeLocation.getDirection().multiply(3)).toVector());
        Vector3D currentPosition = getCenter();

        if (checkMaxMovement) {
//            Vector3D movement = targetPosition.subtract(currentPosition);
//            Vector3D maxMovement = getContainer().getMaxMovement(this, movement);
//            targetPosition = currentPosition.add(maxMovement);
        }

        setCenter(targetPosition);
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
}
