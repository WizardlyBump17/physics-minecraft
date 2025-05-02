package com.wizardlybump17.physics.minecraft;

import com.wizardlybump17.physics.minecraft.command.PhysicsCommand;
import com.wizardlybump17.physics.minecraft.task.ShapeRendererTask;
import com.wizardlybump17.physics.task.factory.RegisteredTaskFactory;
import com.wizardlybump17.physics.task.scheduler.TaskScheduler;
import com.wizardlybump17.physics.three.Engine;
import com.wizardlybump17.physics.three.container.BaseObjectContainer;
import com.wizardlybump17.physics.three.registry.BaseObjectContainerRegistry;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class PhysicsMinecraft extends JavaPlugin {

    private ShapeRendererTask shapeRendererTask;
    private Engine engine;

    @Override
    public void onLoad() {
        shapeRendererTask = new ShapeRendererTask();
        engine = Engine.start(new BaseObjectContainerRegistry(), new TaskScheduler(new RegisteredTaskFactory()));
    }

    @Override
    public void onEnable() {
        shapeRendererTask.runTaskTimer(this, 0, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (BaseObjectContainer container : engine.getObjectContainerRegistry().getValues())
                    container.tick();
            }
        }.runTaskTimer(this, 0, 1);

        PluginCommand physicsCommand = getCommand("physics");
        PhysicsCommand physicsExecutor = new PhysicsCommand(shapeRendererTask, engine);
        physicsCommand.setExecutor(physicsExecutor);
        physicsCommand.setTabCompleter(physicsExecutor);
    }

    @Override
    public void onDisable() {
        if (shapeRendererTask != null)
            shapeRendererTask.clear();
        shapeRendererTask = null;

        if (engine != null)
            engine.shutdown();
        engine = null;
    }

    public Engine getEngine() {
        return engine;
    }

    public static @NotNull PhysicsMinecraft getInstance() {
        return getPlugin(PhysicsMinecraft.class);
    }
}
