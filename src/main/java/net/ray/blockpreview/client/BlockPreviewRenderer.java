package net.ray.blockpreview.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.ray.blockpreview.PreviewManager;
import net.ray.blockpreview.util.RenderHelper;

import java.util.ArrayList;
import java.util.List;

public class BlockPreviewRenderer {
    private static BlockPos lastPreviewPos = null;
    private static List<BlockState> lastPreviewStates = new ArrayList<>();
    private static List<BlockPos> lastPreviewPositions = new ArrayList<>();

    public static void onRenderWorld() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) return;
        if (lastPreviewStates.isEmpty()) return;
        PoseStack poseStack = new PoseStack();
        var camera = mc.gameRenderer.getMainCamera();
        var bufferSource = mc.renderBuffers().bufferSource();
        // Get camera position
        Vec3 cameraPos = camera.getPosition();

        for (int i = 0; i < lastPreviewStates.size(); i++) {
            BlockState state = lastPreviewStates.get(i);
            BlockPos pos = lastPreviewPositions.get(i);

            poseStack.pushPose();
            double x = pos.getX() - cameraPos.x;
            double y = pos.getY() - cameraPos.y;
            double z = pos.getZ() - cameraPos.z;
            poseStack.translate(x, y, z);
            RenderHelper.renderGhostBlock(poseStack, state, pos,0.62f);
            poseStack.popPose();
        }
    }

    public static void updatePreviewPosition() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        ItemStack heldItem = mc.player.getMainHandItem();
        if (!PreviewManager.shouldShowPreview(heldItem)) {
            lastPreviewPos = null;
            lastPreviewStates.clear();
            lastPreviewPositions.clear();
            return;
        }

        HitResult hitResult = mc.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            lastPreviewPos = null;
            lastPreviewStates.clear();
            lastPreviewPositions.clear();
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos hitPos = blockHit.getBlockPos();
        BlockState hitBlockState = mc.level.getBlockState(hitPos);

        BlockPos placementPos;
        if (hitBlockState.canBeReplaced()) {
            placementPos = hitPos;
        } else {
            placementPos = hitPos.relative(blockHit.getDirection());
        }

        if (!mc.level.getBlockState(placementPos).canBeReplaced()) {
            lastPreviewPos = null;
            lastPreviewStates.clear();
            lastPreviewPositions.clear();
            return;
        }

        if (!(heldItem.getItem() instanceof BlockItem blockItem)) {
            lastPreviewPos = null;
            lastPreviewStates.clear();
            lastPreviewPositions.clear();
            return;
        }

        BlockPlaceContext placeContext = new BlockPlaceContext(
                mc.player,
                mc.player.getUsedItemHand(),
                heldItem,
                blockHit
        );

        BlockState previewState = blockItem.getBlock().getStateForPlacement(placeContext);
        if (previewState == null) {
            previewState = blockItem.getBlock().defaultBlockState();
        }

        lastPreviewPos = placementPos;
        lastPreviewStates.clear();
        lastPreviewPositions.clear();

        lastPreviewStates.add(previewState);
        lastPreviewPositions.add(placementPos);

        Block block = blockItem.getBlock();
        BlockPos upperPos = placementPos.above();

        if (mc.level.getBlockState(upperPos).canBeReplaced()) {
            if (block instanceof DoorBlock) {
                BlockState upperDoorState = previewState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
                lastPreviewStates.add(upperDoorState);
                lastPreviewPositions.add(upperPos);
            } else if (block instanceof DoublePlantBlock) {
                BlockState upperPlantState = previewState.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
                lastPreviewStates.add(upperPlantState);
                lastPreviewPositions.add(upperPos);
            }
        }
    }
}