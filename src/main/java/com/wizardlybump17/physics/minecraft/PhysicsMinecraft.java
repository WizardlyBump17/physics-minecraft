package com.wizardlybump17.physics.minecraft;

import com.wizardlybump17.physics.minecraft.command.PhysicsCommand;
import com.wizardlybump17.physics.minecraft.renderer.CubeRenderer;
import com.wizardlybump17.physics.minecraft.renderer.SphereRenderer;
import com.wizardlybump17.physics.minecraft.task.ShapeRendererTask;
import com.wizardlybump17.physics.task.factory.RegisteredTaskFactory;
import com.wizardlybump17.physics.task.scheduler.TaskScheduler;
import com.wizardlybump17.physics.three.Engine;
import com.wizardlybump17.physics.three.container.BaseObjectContainer;
import com.wizardlybump17.physics.three.container.MapBaseObjectContainer;
import com.wizardlybump17.physics.three.registry.BaseObjectContainerRegistry;
import com.wizardlybump17.physics.three.thread.EngineThread;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PhysicsMinecraft extends JavaPlugin {

    private ShapeRendererTask shapeRendererTask;

    @Override
    public void onLoad() {
        shapeRendererTask = new ShapeRendererTask();
        startEngine();
        initRenderers();
    }

    private void startEngine() {
        BaseObjectContainerRegistry containerRegistry = new BaseObjectContainerRegistry();
        Engine.setObjectContainerRegistry(containerRegistry);
        containerRegistry.register(new MapBaseObjectContainer(Bukkit.getWorld("world").getUID()));

        TaskScheduler scheduler = new TaskScheduler(new RegisteredTaskFactory());
        Engine.setScheduler(scheduler);

        EngineThread thread = new EngineThread(scheduler, containerRegistry);
        Engine.setThread(thread);
        thread.start();
    }

    private void initRenderers() {
        BaseObjectContainerRegistry registry = Engine.getObjectContainerRegistry();
        for (BaseObjectContainer container : registry.getValues()) {
            shapeRendererTask.addRenderer(new CubeRenderer(container));
            shapeRendererTask.addRenderer(new SphereRenderer(container));
        }
    }

    @Override
    public void onEnable() {
        shapeRendererTask.runTaskTimer(this, 0, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (BaseObjectContainer container : Engine.getObjectContainerRegistry().getValues())
                    container.tick();
            }
        }.runTaskTimer(this, 0, 1);

        PluginCommand physicsCommand = getCommand("physics");
        PhysicsCommand physicsExecutor = new PhysicsCommand(shapeRendererTask);
        physicsCommand.setExecutor(physicsExecutor);
        physicsCommand.setTabCompleter(physicsExecutor);
    }

    @Override
    public void onDisable() {
        if (shapeRendererTask != null)
            shapeRendererTask.clear();

        shutdownEngine();
    }

    private void shutdownEngine() {
        Thread thread = Engine.getThread();
        if (thread != null)
            thread.interrupt();
        if (thread instanceof EngineThread engineThread)
            engineThread.setRunning(false);
    }
}
