package net.ray.blockpreview;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static final KeyMapping.Category BLOCK_PREVIEW_CATEGORY =
            KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath("blockpreview", "main"));

    public static KeyMapping TOGGLE_PREVIEW;
    public static KeyMapping HOLD_PREVIEW;
    public static KeyMapping TOGGLE_BLOCK_FILTER;

    public static void register() {
        TOGGLE_PREVIEW = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.blockpreview.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                BLOCK_PREVIEW_CATEGORY
        ));

        HOLD_PREVIEW = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.blockpreview.hold",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                BLOCK_PREVIEW_CATEGORY
        ));

        TOGGLE_BLOCK_FILTER = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.blockpreview.toggle_filter",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                BLOCK_PREVIEW_CATEGORY
        ));
    }
}