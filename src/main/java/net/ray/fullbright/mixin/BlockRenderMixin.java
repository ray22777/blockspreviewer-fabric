package net.ray.fullbright.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class BlockRenderMixin {

    /**
     * First method in WorldRenderer
     */
    @Overwrite
    public static int getLightmapCoordinates(BlockRenderView world, BlockPos pos) {
        return LightmapTextureManager.MAX_LIGHT_COORDINATE;
    }

    /**
     * Second method in WorldRenderer
     */
    @Overwrite
    public static int getLightmapCoordinates(BlockRenderView world, BlockState state, BlockPos pos) {
        return LightmapTextureManager.MAX_LIGHT_COORDINATE;
    }
}