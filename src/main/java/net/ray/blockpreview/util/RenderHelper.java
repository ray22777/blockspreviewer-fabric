package net.ray.blockpreview.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RenderHelper {
    public static void renderGhostBlock(PoseStack poseStack, BlockState state, BlockPos pos, Minecraft mc) {
        if (state == null || mc.level == null || mc.player == null) return;

        // Setup transparency
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        // Set global alpha
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.62f);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        try {
            // Method 1: Try with translucent render type
            int light = LevelRenderer.getLightColor(mc.level, pos);

            // Use tryRenderBlock instead of renderSingleBlock
            mc.getBlockRenderer().renderBatched(
                    state,
                    pos,
                    mc.level,
                    poseStack,
                    bufferSource.getBuffer(RenderType.translucent()),
                    false,
                    mc.level.random
            );

        } catch (Exception e) {
            // Method 2: Try without ModelData
            try {
                int light = LevelRenderer.getLightColor(mc.level, pos);

                // Older method that might work
                mc.getBlockRenderer().renderSingleBlock(
                        state,
                        poseStack,
                        bufferSource,
                        light,
                        net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
                );
            } catch (Exception e2) {
                // Method 3: Direct model rendering
                e2.printStackTrace();
            }
        }

        // Flush buffer
        bufferSource.endBatch();

        // Clean up
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}