package net.ray.blockpreview;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static final String CATEGORY = "key.category.blockpreview";

    public static KeyMapping TOGGLE_PREVIEW;
    public static KeyMapping HOLD_PREVIEW;
    public static KeyMapping TOGGLE_BLOCK_FILTER;

    public static void register() {
        TOGGLE_PREVIEW = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.blockpreview.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                CATEGORY
        ));

        HOLD_PREVIEW = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.blockpreview.hold",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                CATEGORY
        ));

        TOGGLE_BLOCK_FILTER = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.blockpreview.toggle_filter",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                CATEGORY
        ));
    }
}