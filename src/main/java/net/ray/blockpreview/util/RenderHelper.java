package net.ray.blockpreview.util;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class RenderHelper {
    public static void renderGhostBlock(PoseStack poseStack, BlockState state, BlockPos pos, Minecraft mc) {
        float alpha = 0.62f;
        if (state == null || mc.level == null) return;

        MultiBufferSource translucentBuffer = new MultiBufferSource() {
            @Override
            public com.mojang.blaze3d.vertex.VertexConsumer getBuffer(RenderType renderType) {
                var original = mc.renderBuffers().bufferSource().getBuffer(RenderType.translucentMovingBlock());

                return new com.mojang.blaze3d.vertex.VertexConsumer() {
                    @Override
                    public com.mojang.blaze3d.vertex.VertexConsumer addVertex(float x, float y, float z) {
                        return original.addVertex(x, y, z);
                    }

                    @Override
                    public com.mojang.blaze3d.vertex.VertexConsumer addVertex(PoseStack.Pose pose, float x, float y, float z) {
                        return original.addVertex(pose, x, y, z);
                    }

                    @Override
                    public com.mojang.blaze3d.vertex.VertexConsumer setColor(int red, int green, int blue, int alphaValue) {
                        return original.setColor(red, green, blue, (int)(alphaValue * alpha));
                    }

                    @Override
                    public com.mojang.blaze3d.vertex.VertexConsumer setColor(float red, float green, float blue, float alphaValue) {
                        return setColor(
                                (int)(red * 255),
                                (int)(green * 255),
                                (int)(blue * 255),
                                (int)(alphaValue * 255)
                        );
                    }

                    @Override
                    public com.mojang.blaze3d.vertex.VertexConsumer setUv(float u, float v) {
                        return original.setUv(u, v);
                    }

                    @Override
                    public com.mojang.blaze3d.vertex.VertexConsumer setUv1(int u, int v) {
                        return original.setUv1(u, v);
                    }

                    @Override
                    public com.mojang.blaze3d.vertex.VertexConsumer setUv2(int u, int v) {
                        return original.setUv2(u, v);
                    }
                    @Override
                    public com.mojang.blaze3d.vertex.VertexConsumer setNormal(PoseStack.Pose pose, float x, float y, float z) {
                        return original.setNormal(pose, x, y, z);
                    }

                    @Override
                    public com.mojang.blaze3d.vertex.VertexConsumer setNormal(float x, float y, float z) {
                        return original.setNormal(x, y, z);
                    }

                };
            }
        };

        int light = LevelRenderer.getLightColor(mc.level, pos);

        mc.getBlockRenderer().renderSingleBlock(
                state,
                poseStack,
                translucentBuffer,
                light,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
        );

        mc.renderBuffers().bufferSource().endBatch();
    }
}