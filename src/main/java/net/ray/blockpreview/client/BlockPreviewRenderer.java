package net.ray.blockpreview.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
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
    private static InteractionHand lastUsedHand = InteractionHand.MAIN_HAND;

    public static void onRenderWorld() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (lastPreviewStates.isEmpty()) return;
        PoseStack poseStack = new PoseStack();
        var camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.position();

        for (int i = 0; i < lastPreviewStates.size(); i++) {
            BlockState state = lastPreviewStates.get(i);
            BlockPos pos = lastPreviewPositions.get(i);

            poseStack.pushPose();
            double x = pos.getX() - cameraPos.x;
            double y = pos.getY() - cameraPos.y;
            double z = pos.getZ() - cameraPos.z;
            poseStack.translate(x, y, z);

            RenderHelper.renderGhostBlock(poseStack, state, pos, mc);
            poseStack.popPose();
        }
    }

    public static void updatePreviewPosition() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            clearPreview();
            return;
        }

        ItemStack mainHandItem = mc.player.getMainHandItem();
        ItemStack offHandItem = mc.player.getOffhandItem();
        ItemStack previewItem = getPreviewItemFromHands(mainHandItem, offHandItem);

        if (previewItem.isEmpty()) {
            clearPreview();
            return;
        }

        HitResult hitResult = mc.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            clearPreview();
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos hitPos = blockHit.getBlockPos();
        BlockState hitBlockState = mc.level.getBlockState(hitPos);

        BlockPos placementPos = getPlacementPosition(hitPos, hitBlockState, blockHit, mc);
        if (placementPos == null || !mc.level.getBlockState(placementPos).canBeReplaced()) {
            clearPreview();
            return;
        }

        if (!(previewItem.getItem() instanceof BlockItem blockItem)) {
            clearPreview();
            return;
        }

        BlockPlaceContext placeContext = new BlockPlaceContext(
                mc.player,
                lastUsedHand,
                previewItem,
                blockHit
        );

        BlockState previewState = blockItem.getBlock().getStateForPlacement(placeContext);
        if (previewState == null) {
            previewState = blockItem.getBlock().defaultBlockState();
        }

        updatePreviewData(placementPos, previewState, blockItem.getBlock(), mc);
    }

    private static ItemStack getPreviewItemFromHands(ItemStack mainHand, ItemStack offHand) {
        if (PreviewManager.shouldShowPreview(mainHand)) {
            lastUsedHand = InteractionHand.MAIN_HAND;
            return mainHand;
        } else if (PreviewManager.shouldShowPreview(offHand)) {
            lastUsedHand = InteractionHand.OFF_HAND;
            return offHand;
        }
        return ItemStack.EMPTY;
    }

    private static BlockPos getPlacementPosition(BlockPos hitPos, BlockState hitBlockState,
                                                 BlockHitResult blockHit, Minecraft mc) {
        if (hitBlockState.canBeReplaced()) {
            return hitPos;
        }

        BlockPos adjacentPos = hitPos.relative(blockHit.getDirection());
        return mc.level.getBlockState(adjacentPos).canBeReplaced() ? adjacentPos : null;
    }

    private static void updatePreviewData(BlockPos placementPos, BlockState previewState,
                                          Block block, Minecraft mc) {
        lastPreviewPos = placementPos;
        lastPreviewStates.clear();
        lastPreviewPositions.clear();
        lastPreviewStates.add(previewState);
        lastPreviewPositions.add(placementPos);
        BlockPos upperPos = placementPos.above();
        if (mc.level.getBlockState(upperPos).canBeReplaced()) {
            handleMultiBlockPlacement(block, previewState, upperPos);
        }
    }

    private static void handleMultiBlockPlacement(Block block, BlockState previewState, BlockPos upperPos) {
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

    private static void clearPreview() {
        lastPreviewPos = null;
        lastPreviewStates.clear();
        lastPreviewPositions.clear();
    }
}