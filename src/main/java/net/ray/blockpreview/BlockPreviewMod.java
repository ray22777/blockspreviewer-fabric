package net.ray.blockpreview;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.ray.blockpreview.client.BlockPreviewRenderer;
import net.ray.blockpreview.config.SimpleConfig;

public class BlockPreviewMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SimpleConfig.load();
        ModKeybinds.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PreviewManager.handleInput();
            BlockPreviewRenderer.updatePreviewPosition();
        });
        WorldRenderEvents.BEFORE_TRANSLUCENT.register( context-> {
            BlockPreviewRenderer.onRenderWorld();
        });

        System.out.println("Block Preview Mod (Fabric 1.21.11) initialized!");
    }
}