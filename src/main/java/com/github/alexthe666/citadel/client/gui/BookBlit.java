package com.github.alexthe666.citadel.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class BookBlit {
    public static void blitWithColor(DrawContext guiGraphics, Identifier p_283377_, int p_281970_, int p_282111_, int p_283134_, int p_282778_, int p_281478_, int p_281821_, int r, int g, int b, int a) {
        blitWithColor(guiGraphics, p_283377_, p_281970_, p_282111_, 0, (float) p_283134_, (float) p_282778_, p_281478_, p_281821_, 256, 256, r, g, b, a);
    }

    public static void blitWithColor(DrawContext guiGraphics, Identifier p_283573_, int p_283574_, int p_283670_, int p_283545_, float p_283029_, float p_283061_, int p_282845_, int p_282558_, int p_282832_, int p_281851_, int r, int g, int b, int a) {
        blitWithColor(guiGraphics, p_283573_, p_283574_, p_283574_ + p_282845_, p_283670_, p_283670_ + p_282558_, p_283545_, p_282845_, p_282558_, p_283029_, p_283061_, p_282832_, p_281851_, r, g, b, a);
    }

    public static void blitWithColor(DrawContext guiGraphics, Identifier p_282034_, int p_283671_, int p_282377_, int p_282058_, int p_281939_, float p_282285_, float p_283199_, int p_282186_, int p_282322_, int p_282481_, int p_281887_, int r, int g, int b, int a) {
        blitWithColor(guiGraphics, p_282034_, p_283671_, p_283671_ + p_282058_, p_282377_, p_282377_ + p_281939_, 0, p_282186_, p_282322_, p_282285_, p_283199_, p_282481_, p_281887_, r, g, b, a);
    }

    public static void blitWithColor(DrawContext guiGraphics, Identifier p_283272_, int p_283605_, int p_281879_, float p_282809_, float p_282942_, int p_281922_, int p_282385_, int p_282596_, int p_281699_, int r, int g, int b, int a) {
        blitWithColor(guiGraphics, p_283272_, p_283605_, p_281879_, p_281922_, p_282385_, p_282809_, p_282942_, p_281922_, p_282385_, p_282596_, p_281699_, r, g, b, a);
    }

    private static void blitWithColor(DrawContext guiGraphics, Identifier p_282639_, int p_282732_, int p_283541_, int p_281760_, int p_283298_, int p_283429_, int p_282193_, int p_281980_, float p_282660_, float p_281522_, int p_282315_, int p_281436_, int r, int g, int b, int a) {
        blitWithColor(guiGraphics, p_282639_, p_282732_, p_283541_, p_281760_, p_283298_, p_283429_, (p_282660_ + 0.0F) / (float) p_282315_, (p_282660_ + (float) p_282193_) / (float) p_282315_, (p_281522_ + 0.0F) / (float) p_281436_, (p_281522_ + (float) p_281980_) / (float) p_281436_, r, g, b, a);
    }

    private static void blitWithColor(DrawContext guiGraphics, Identifier texture, int startX, int endX, int startY, int endY, int zLevel, float u0, float u1, float v0, float v1, int r, int g, int b, int a) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = guiGraphics.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferbuilder.vertex(matrix4f, (float) startX, (float) startY, (float) zLevel).color(r, g, b, a).texture(u0, v0).next();
        bufferbuilder.vertex(matrix4f, (float) startX, (float) endY, (float) zLevel).color(r, g, b, a).texture(u0, v1).next();
        bufferbuilder.vertex(matrix4f, (float) endX, (float) endY, (float) zLevel).color(r, g, b, a).texture(u1, v1).next();
        bufferbuilder.vertex(matrix4f, (float) endX, (float) startY, (float) zLevel).color(r, g, b, a).texture(u1, v0).next();
        BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
        RenderSystem.disableBlend();
    }
}
