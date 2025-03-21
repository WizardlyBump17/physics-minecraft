package com.wizardlybump17.physics.minecraft.debug.object;

import com.wizardlybump17.physics.minecraft.Converter;
import com.wizardlybump17.physics.minecraft.debug.DebugObjectContainer;
import com.wizardlybump17.physics.three.object.BasicObject;
import com.wizardlybump17.physics.three.shape.Shape;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DebugObject extends BasicObject {

    private final @NotNull Player player;
    private boolean following = true;
    private boolean canFollow;

    public DebugObject(@NotNull Shape shape, @NotNull DebugObjectContainer container, @NotNull Player player, boolean canFollow) {
        super(shape, container);
        this.player = player;
        this.canFollow = canFollow;
    }

    @Override
    public @NotNull DebugObjectContainer getContainer() {
        return (DebugObjectContainer) super.getContainer();
    }

    @Override
    public void tick() {
        super.tick();

        if (!canFollow || !following)
            return;

        Location currentLocation = player.getEyeLocation();
        if (!currentLocation.getWorld().equals(getContainer().getWorld()))
            return;

        teleport(Converter.convert(currentLocation.add(currentLocation.getDirection().multiply(3)).toVector()));
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
}
