package net.ray.blockpreview;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.ray.blockpreview.config.SimpleConfig;

public class PreviewManager {
    private static boolean toggleState = true;
    private static boolean holdKeyPressed = false;
    private static boolean wasToggleKeyDown = false;
    private static boolean wasHoldKeyDown = false;
    private static boolean wasFilterKeyDown = false;

    public static void onClientTick() {
        // Input handling moved to handleInput()
    }

    public static void handleInput() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Handle toggle key
        boolean isToggleKeyDown = ModKeybinds.TOGGLE_PREVIEW.isDown();
        if (isToggleKeyDown && !wasToggleKeyDown) {
            togglePreview();
        }
        wasToggleKeyDown = isToggleKeyDown;

        // Handle hold key
        boolean isHoldKeyDown = ModKeybinds.HOLD_PREVIEW.isDown();
        holdKeyPressed = isHoldKeyDown;
        wasHoldKeyDown = isHoldKeyDown;

        // Handle filter key
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

    public static boolean isDoorUpper(Block block) {
        return block instanceof DoorBlock;
    }

    public static boolean isTallPlantUpper(Block block) {
        return block instanceof DoublePlantBlock;
    }

    public static DoubleBlockHalf getUpperHalf() {
        return DoubleBlockHalf.UPPER;
    }

    private static boolean hasFacingProperty(BlockState state) {
        if (state.hasProperty(BlockStateProperties.FACING)) return true;
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return true;
        if (state.hasProperty(BlockStateProperties.AXIS)) return true;
        return false;
    }
}