package net.ray.blockpreview;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.ray.blockpreview.config.SimpleConfig;

public class PreviewManager {
    private static boolean toggleState = true;
    private static boolean holdKeyPressed = false;
    private static boolean wasToggleKeyDown = false;
    private static boolean wasHoldKeyDown = false;
    private static boolean wasFilterKeyDown = false;

    public static void handleInput() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean isToggleKeyDown = ModKeybinds.TOGGLE_PREVIEW.isDown();
        if (isToggleKeyDown && !wasToggleKeyDown) {
            togglePreview();
        }
        wasToggleKeyDown = isToggleKeyDown;

        boolean isHoldKeyDown = ModKeybinds.HOLD_PREVIEW.isDown();
        holdKeyPressed = isHoldKeyDown;
        wasHoldKeyDown = isHoldKeyDown;

        boolean isFilterKeyDown = ModKeybinds.TOGGLE_BLOCK_FILTER.isDown();
        if (isFilterKeyDown && !wasFilterKeyDown) {
            toggleBlockFilter();
        }
        wasFilterKeyDown = isFilterKeyDown;
    }

    public static void togglePreview() {
        toggleState = !toggleState;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            String status = toggleState ? "§aON" : "§cOFF";
            mc.player.displayClientMessage(Component.literal("§bToggle Preview §7- " + status), true);
        }
    }

    public static void toggleBlockFilter() {
        SimpleConfig.toggleBlockFilter();

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            String mode = SimpleConfig.showAllBlocks() ? "§aALL BLOCKS" : "§6FACING ONLY";
            mc.player.displayClientMessage(Component.literal("§bBlock Filter §7- " + mode), true);
        }
    }

    public static boolean shouldShowPreview(ItemStack heldItem) {
        boolean shouldShow = toggleState || holdKeyPressed;

        if (!shouldShow) return false;

        if (!(heldItem.getItem() instanceof BlockItem blockItem)) {
            return false;
        }

        if (SimpleConfig.showAllBlocks()) {
            return true;
        } else {
            BlockState state = blockItem.getBlock().defaultBlockState();
            return hasFacingProperty(state);
        }
    }


    private static boolean hasFacingProperty(BlockState state) {
        if (state.hasProperty(BlockStateProperties.FACING)) return true;
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return true;
        if (state.hasProperty(BlockStateProperties.AXIS)) return true;
        return false;
    }
}