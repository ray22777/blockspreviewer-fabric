package net.ray.blockpreview.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LightLayer;

public class RenderHelper {
    public static void renderGhostBlock(PoseStack poseStack, BlockState state, BlockPos pos, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        if (state == null || mc.level == null) return;

        int blockLight = mc.level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = mc.level.getBrightness(LightLayer.SKY, pos);
        int light = LevelRenderer.getLightColor(mc.level, pos);

        VertexConsumer originalConsumer = mc.renderBuffers().bufferSource().getBuffer(RenderTypes.translucentMovingBlock());

        VertexConsumer alphaConsumer = new VertexConsumer() {
            @Override
            public VertexConsumer addVertex(float x, float y, float z) {
                return originalConsumer.addVertex(x, y, z);
            }

            @Override
            public VertexConsumer addVertex(PoseStack.Pose pose, float x, float y, float z) {
                return originalConsumer.addVertex(pose, x, y, z);
            }

            @Override
            public VertexConsumer setColor(int red, int green, int blue, int alphaValue) {
                int newAlpha = Math.max(0, Math.min(255, (int)(alphaValue * alpha)));
                return originalConsumer.setColor(red, green, blue, newAlpha);
            }

            @Override
            public VertexConsumer setColor(int color) {
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;
                int a = (color >> 24) & 0xFF;
                int newAlpha = Math.max(0, Math.min(255, (int)(a * alpha)));
                return originalConsumer.setColor(
                        (newAlpha << 24) | (r << 16) | (g << 8) | b
                );
            }

            @Override
            public VertexConsumer setColor(float red, float green, float blue, float alphaValue) {
                int newAlpha = Math.max(0, Math.min(255, (int)(alphaValue * 255 * alpha)));
                return originalConsumer.setColor(
                        (int)(red * 255),
                        (int)(green * 255),
                        (int)(blue * 255),
                        newAlpha
                );
            }

            @Override
            public VertexConsumer setUv(float u, float v) {
                return originalConsumer.setUv(u, v);
            }

            @Override
            public VertexConsumer setUv1(int u, int v) {
                return originalConsumer.setUv1(u, v);
            }

            @Override
            public VertexConsumer setUv2(int u, int v) {
                return originalConsumer.setUv2(u, v);
            }

            @Override
            public VertexConsumer setNormal(PoseStack.Pose pose, float x, float y, float z) {
                return originalConsumer.setNormal(pose, x, y, z);
            }

            @Override
            public VertexConsumer setNormal(float x, float y, float z) {
                return originalConsumer.setNormal(x, y, z);
            }


            @Override
            public VertexConsumer setLineWidth(float f) {
                return originalConsumer.setLineWidth(f);
            }
        };

        poseStack.pushPose();
        try {
            mc.getBlockRenderer().renderSingleBlock(
                    state,
                    poseStack,
                    new MultiBufferSource() {
                        @Override
                        public VertexConsumer getBuffer(net.minecraft.client.renderer.rendertype.RenderType renderType) {
                            return alphaConsumer;
                        }

                    },
                    light,
                    OverlayTexture.NO_OVERLAY
            );
        } finally {
            poseStack.popPose();
        }

        mc.renderBuffers().bufferSource().endBatch(RenderTypes.translucentMovingBlock());
    }
}