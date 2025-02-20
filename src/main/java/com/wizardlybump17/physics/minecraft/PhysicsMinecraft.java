package com.wizardlybump17.physics.minecraft;

import org.bukkit.plugin.java.JavaPlugin;

public class PhysicsMinecraft extends JavaPlugin {

    @Override
    public void onLoad() {
        startEngine();
    }

    private void startEngine() {
        Engine.setObjectContainerRegistry(new BaseObjectContainerRegistry());
    }
}
