package com.wizardlybump17.physics.minecraft;

import com.wizardlybump17.physics.minecraft.command.PhysicsCommand;
import com.wizardlybump17.physics.minecraft.task.ShapeRendererTask;
import com.wizardlybump17.physics.three.Engine;
import com.wizardlybump17.physics.three.registry.BaseObjectContainerRegistry;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class PhysicsMinecraft extends JavaPlugin {

    private ShapeRendererTask shapeRendererTask;

    @Override
    public void onLoad() {
        startEngine();

        shapeRendererTask = new ShapeRendererTask();
    }

    private void startEngine() {
        Engine.setObjectContainerRegistry(new BaseObjectContainerRegistry());
    }

    @Override
    public void onEnable() {
        shapeRendererTask.runTaskTimer(this, 0, 1);

        PluginCommand physicsCommand = getCommand("physics");
        PhysicsCommand physicsExecutor = new PhysicsCommand(shapeRendererTask);
        physicsCommand.setExecutor(physicsExecutor);
        physicsCommand.setTabCompleter(physicsExecutor);
    }

    @Override
    public void onDisable() {
        if (shapeRendererTask != null)
            shapeRendererTask.clear();
    }
}
