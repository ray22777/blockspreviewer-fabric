package net.ray.blockpreview.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.RenderShape;

public class RenderHelper {
    public static void renderGhostBlock(PoseStack poseStack, BlockState state, BlockPos pos, Minecraft mc) {
        if (state == null || mc.level == null || mc.player == null) return;

        // Check if the block has a model to render
        if (state.getRenderShape() == RenderShape.INVISIBLE) return;

        // Set alpha to 62% - THIS IS KEY
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.62f);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        try {
            // Get light level at position
            int light = LevelRenderer.getLightColor(mc.level, pos);

            // Create a custom MultiBufferSource that uses translucent rendering
            MultiBufferSource translucentBufferSource = new MultiBufferSource() {
                @Override
                public VertexConsumer getBuffer(RenderType renderType) {
                    // Force translucent render type
                    return bufferSource.getBuffer(RenderType.translucent());
                }
            };

            // Render the block with our translucent buffer source
            mc.getBlockRenderer().renderSingleBlock(
                    state,
                    poseStack,
                    translucentBufferSource,
                    light,
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        // End the batch
        bufferSource.endBatch();

        // Reset render states
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}