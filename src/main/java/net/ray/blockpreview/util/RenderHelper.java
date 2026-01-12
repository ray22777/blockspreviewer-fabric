package net.ray.blockpreview.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RenderHelper {
    public static void renderGhostBlock(PoseStack poseStack, BlockState state, BlockPos pos, Minecraft mc) {
        if (state == null || mc.level == null || mc.player == null) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.62f);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();


        var buffer = bufferSource.getBuffer(RenderType.translucent());
        mc.getBlockRenderer().getModelRenderer().tesselateBlock(
                mc.level,
                mc.getBlockRenderer().getBlockModel(state),
                state,
                pos,
                poseStack,
                buffer,
                false,
                mc.level.random,
                state.getSeed(pos),
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
        );
        bufferSource.endBatch();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}