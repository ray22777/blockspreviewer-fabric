package net.ray.fullbright.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;

@Environment(EnvType.CLIENT)
@Mixin(LightmapTextureManager.class)
public class FullBrightMixin {

    @Shadow private NativeImage image;
    @Shadow private boolean dirty;
    @Shadow private net.minecraft.client.texture.NativeImageBackedTexture texture;

    /**
     * Replace the entire lightmap update logic with a full-white lightmap.
     */
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void fullbright_update(float delta, CallbackInfo ci) {
        if (!dirty) {
            return; // nothing to do
        }

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                image.setColor(x, y, 0xFFFFFFFF);
            }
        }

        texture.upload();
        dirty = false;
        ci.cancel();
    }
}