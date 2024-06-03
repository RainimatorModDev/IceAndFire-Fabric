package com.github.alexthe666.citadel.client.render.pathfinding;

import DepthTestStateShard;
import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

public class WorldRenderMacros extends UiRenderMacros
{
    private static final int MAX_DEBUG_TEXT_RENDER_DIST_SQUARED = 8 * 8 * 16;
    public static final RenderLayer LINES = RenderTypes.LINES;
    public static final RenderLayer LINES_WITH_WIDTH = RenderTypes.LINES_WITH_WIDTH;
    public static final RenderLayer GLINT_LINES = RenderTypes.GLINT_LINES;
    public static final RenderLayer GLINT_LINES_WITH_WIDTH = RenderTypes.GLINT_LINES_WITH_WIDTH;
    public static final RenderLayer COLORED_TRIANGLES = RenderTypes.COLORED_TRIANGLES;
    public static final RenderLayer COLORED_TRIANGLES_NC_ND = RenderTypes.COLORED_TRIANGLES_NC_ND;

    private static final LinkedList<RenderLayer> buffers = new LinkedList<>();
    /**
     * Always use {@link #getBufferSource} when actually using the buffer source
     */
    private static Immediate bufferSource;

    /**
     * Put type at the first position.
     *
     * @param bufferType type to put in
     */
    public static void putBufferHead(final RenderLayer bufferType)
    {
        buffers.addFirst(bufferType);
        bufferSource = null;
    }

    /**
     * Put type at the last position.
     *
     * @param bufferType type to put in
     */
    public static void putBufferTail(final RenderLayer bufferType)
    {
        buffers.addLast(bufferType);
        bufferSource = null;
    }

    /**
     * Put type before the given buffer or if not found then at first position.
     *
     * @param bufferType type to put in
     * @param putBefore  search for type to put before
     */
    public static void putBufferBefore(final RenderLayer bufferType, final RenderLayer putBefore)
    {
        buffers.add(Math.max(0, buffers.indexOf(putBefore)), bufferType);
        bufferSource = null;
    }

    /**
     * Put type after the given buffer or if not found then at last position.
     *
     * @param bufferType type to put in
     * @param putAfter   search for type to put after
     */
    public static void putBufferAfter(final RenderLayer bufferType, final RenderLayer putAfter)
    {
        final int index = buffers.indexOf(putAfter);
        if (index == -1)
        {
            buffers.add(bufferType);
        }
        else
        {
            buffers.add(index + 1, bufferType);
        }
        bufferSource = null;
    }

    static
    {
        putBufferTail(WorldRenderMacros.COLORED_TRIANGLES);
        putBufferTail(WorldRenderMacros.LINES);
        putBufferTail(WorldRenderMacros.LINES_WITH_WIDTH);
        putBufferTail(WorldRenderMacros.GLINT_LINES);
        putBufferTail(WorldRenderMacros.GLINT_LINES_WITH_WIDTH);
        putBufferTail(WorldRenderMacros.COLORED_TRIANGLES_NC_ND);
    }

    public static Immediate getBufferSource()
    {
        if (bufferSource == null)
        {
            bufferSource = VertexConsumerProvider.immediate(Util.make(new Object2ObjectLinkedOpenHashMap<>(), map -> {
                buffers.forEach(type -> map.put(type, new BufferBuilder(type.getExpectedBufferSize())));
            }), Tessellator.getInstance().getBuffer());
        }
        return bufferSource;
    }

    /**
     * Render a black box around two positions
     *
     * @param posA The first Position
     * @param posB The second Position
     */
    public static void renderBlackLineBox(final Immediate buffer,
                                          final MatrixStack ps,
                                          final BlockPos posA,
                                          final BlockPos posB,
                                          final float lineWidth)
    {
        renderLineBox(buffer.getBuffer(LINES_WITH_WIDTH), ps, posA, posB, 0x00, 0x00, 0x00, 0xff, lineWidth);
    }

    /**
     * Render a red glint box around two positions
     *
     * @param posA The first Position
     * @param posB The second Position
     */
    public static void renderRedGlintLineBox(final Immediate buffer,
                                             final MatrixStack ps,
                                             final BlockPos posA,
                                             final BlockPos posB,
                                             final float lineWidth)
    {
        renderLineBox(buffer.getBuffer(GLINT_LINES_WITH_WIDTH), ps, posA, posB, 0xff, 0x0, 0x0, 0xff, lineWidth);
    }

    /**
     * Render a white box around two positions
     *
     * @param posA The first Position
     * @param posB The second Position
     */
    public static void renderWhiteLineBox(final Immediate buffer,
                                          final MatrixStack ps,
                                          final BlockPos posA,
                                          final BlockPos posB,
                                          final float lineWidth)
    {
        renderLineBox(buffer.getBuffer(LINES_WITH_WIDTH), ps, posA, posB, 0xff, 0xff, 0xff, 0xff, lineWidth);
    }

    /**
     * Render a colored box around from aabb
     *
     * @param aabb the box
     */
    public static void renderLineAABB(final VertexConsumer buffer,
                                      final MatrixStack ps,
                                      final Box aabb,
                                      final int argbColor,
                                      final float lineWidth)
    {
        renderLineAABB(buffer,
                ps,
                aabb,
                (argbColor >> 16) & 0xff,
                (argbColor >> 8) & 0xff,
                argbColor & 0xff,
                (argbColor >> 24) & 0xff,
                lineWidth);
    }

    /**
     * Render a colored box around from aabb
     *
     * @param aabb the box
     */
    public static void renderLineAABB(final VertexConsumer buffer,
                                      final MatrixStack ps,
                                      final Box aabb,
                                      final int red,
                                      final int green,
                                      final int blue,
                                      final int alpha,
                                      final float lineWidth)
    {
        renderLineBox(buffer,
                ps,
                (float) aabb.minX,
                (float) aabb.minY,
                (float) aabb.minZ,
                (float) aabb.maxX,
                (float) aabb.maxY,
                (float) aabb.maxZ,
                red,
                green,
                blue,
                alpha,
                lineWidth);
    }

    /**
     * Render a colored box around position
     *
     * @param pos The Position
     */
    public static void renderLineBox(final VertexConsumer buffer,
                                     final MatrixStack ps,
                                     final BlockPos pos,
                                     final int argbColor,
                                     final float lineWidth)
    {
        renderLineBox(buffer,
                ps,
                pos,
                pos,
                (argbColor >> 16) & 0xff,
                (argbColor >> 8) & 0xff,
                argbColor & 0xff,
                (argbColor >> 24) & 0xff,
                lineWidth);
    }

    /**
     * Render a colored box around two positions
     *
     * @param posA The first Position
     * @param posB The second Position
     */
    public static void renderLineBox(final VertexConsumer buffer,
                                     final MatrixStack ps,
                                     final BlockPos posA,
                                     final BlockPos posB,
                                     final int argbColor,
                                     final float lineWidth)
    {
        renderLineBox(buffer,
                ps,
                posA,
                posB,
                (argbColor >> 16) & 0xff,
                (argbColor >> 8) & 0xff,
                argbColor & 0xff,
                (argbColor >> 24) & 0xff,
                lineWidth);
    }

    /**
     * Render a box around two positions
     *
     * @param posA First position
     * @param posB Second position
     */
    public static void renderLineBox(final VertexConsumer buffer,
                                     final MatrixStack ps,
                                     final BlockPos posA,
                                     final BlockPos posB,
                                     final int red,
                                     final int green,
                                     final int blue,
                                     final int alpha,
                                     final float lineWidth)
    {
        renderLineBox(buffer,
                ps,
                Math.min(posA.getX(), posB.getX()),
                Math.min(posA.getY(), posB.getY()),
                Math.min(posA.getZ(), posB.getZ()),
                Math.max(posA.getX(), posB.getX()) + 1,
                Math.max(posA.getY(), posB.getY()) + 1,
                Math.max(posA.getZ(), posB.getZ()) + 1,
                red,
                green,
                blue,
                alpha,
                lineWidth);
    }

    /**
     * Render a box around two positions
     *
     * @param posA First position
     * @param posB Second position
     */
    public static void renderLineBox(final VertexConsumer buffer,
                                     final MatrixStack ps,
                                     float minX,
                                     float minY,
                                     float minZ,
                                     float maxX,
                                     float maxY,
                                     float maxZ,
                                     final int red,
                                     final int green,
                                     final int blue,
                                     final int alpha,
                                     final float lineWidth)
    {
        if (alpha == 0)
        {
            return;
        }

        final float halfLine = lineWidth / 2.0f;
        minX -= halfLine;
        minY -= halfLine;
        minZ -= halfLine;
        final float minX2 = minX + lineWidth;
        final float minY2 = minY + lineWidth;
        final float minZ2 = minZ + lineWidth;

        maxX += halfLine;
        maxY += halfLine;
        maxZ += halfLine;
        final float maxX2 = maxX - lineWidth;
        final float maxY2 = maxY - lineWidth;
        final float maxZ2 = maxZ - lineWidth;

        final Matrix4f m = ps.peek().getPositionMatrix();
        buffer.fixedColor(red, green, blue, alpha);

        populateRenderLineBox(minX, minY, minZ, minX2, minY2, minZ2, maxX, maxY, maxZ, maxX2, maxY2, maxZ2, m, buffer);

        buffer.unfixColor();
    }

    // TODO: ebo this, does vanilla have any ebo things?
    public static void populateRenderLineBox(final float minX,
                                             final float minY,
                                             final float minZ,
                                             final float minX2,
                                             final float minY2,
                                             final float minZ2,
                                             final float maxX,
                                             final float maxY,
                                             final float maxZ,
                                             final float maxX2,
                                             final float maxY2,
                                             final float maxZ2,
                                             final Matrix4f m,
                                             final VertexConsumer buf)
    {
        // z plane

        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, maxX2, minY2, minZ).next();
        buf.vertex(m, maxX, minY, minZ).next();

        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, minX2, minY2, minZ).next();
        buf.vertex(m, maxX2, minY2, minZ).next();

        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, minX2, maxY2, minZ).next();
        buf.vertex(m, minX2, minY2, minZ).next();

        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, minX, maxY, minZ).next();
        buf.vertex(m, minX2, maxY2, minZ).next();

        buf.vertex(m, maxX, maxY, minZ).next();
        buf.vertex(m, minX2, maxY2, minZ).next();
        buf.vertex(m, minX, maxY, minZ).next();

        buf.vertex(m, maxX, maxY, minZ).next();
        buf.vertex(m, maxX2, maxY2, minZ).next();
        buf.vertex(m, minX2, maxY2, minZ).next();

        buf.vertex(m, maxX, maxY, minZ).next();
        buf.vertex(m, maxX2, minY2, minZ).next();
        buf.vertex(m, maxX2, maxY2, minZ).next();

        buf.vertex(m, maxX, maxY, minZ).next();
        buf.vertex(m, maxX, minY, minZ).next();
        buf.vertex(m, maxX2, minY2, minZ).next();

        //

        buf.vertex(m, minX, maxY2, minZ2).next();
        buf.vertex(m, minX2, minY2, minZ2).next();
        buf.vertex(m, minX2, maxY2, minZ2).next();

        buf.vertex(m, minX, maxY2, minZ2).next();
        buf.vertex(m, minX, minY2, minZ2).next();
        buf.vertex(m, minX2, minY2, minZ2).next();

        buf.vertex(m, minX2, minY2, minZ2).next();
        buf.vertex(m, minX2, minY, minZ2).next();
        buf.vertex(m, maxX2, minY, minZ2).next();

        buf.vertex(m, minX2, minY2, minZ2).next();
        buf.vertex(m, maxX2, minY, minZ2).next();
        buf.vertex(m, maxX2, minY2, minZ2).next();

        buf.vertex(m, maxX, maxY2, minZ2).next();
        buf.vertex(m, maxX2, maxY2, minZ2).next();
        buf.vertex(m, maxX2, minY2, minZ2).next();

        buf.vertex(m, maxX, maxY2, minZ2).next();
        buf.vertex(m, maxX2, minY2, minZ2).next();
        buf.vertex(m, maxX, minY2, minZ2).next();

        buf.vertex(m, minX2, maxY2, minZ2).next();
        buf.vertex(m, maxX2, maxY, minZ2).next();
        buf.vertex(m, minX2, maxY, minZ2).next();

        buf.vertex(m, minX2, maxY2, minZ2).next();
        buf.vertex(m, maxX2, maxY2, minZ2).next();
        buf.vertex(m, maxX2, maxY, minZ2).next();

        //

        buf.vertex(m, minX, maxY2, maxZ2).next();
        buf.vertex(m, minX2, maxY2, maxZ2).next();
        buf.vertex(m, minX2, minY2, maxZ2).next();

        buf.vertex(m, minX, maxY2, maxZ2).next();
        buf.vertex(m, minX2, minY2, maxZ2).next();
        buf.vertex(m, minX, minY2, maxZ2).next();

        buf.vertex(m, minX2, minY2, maxZ2).next();
        buf.vertex(m, maxX2, minY, maxZ2).next();
        buf.vertex(m, minX2, minY, maxZ2).next();

        buf.vertex(m, minX2, minY2, maxZ2).next();
        buf.vertex(m, maxX2, minY2, maxZ2).next();
        buf.vertex(m, maxX2, minY, maxZ2).next();

        buf.vertex(m, maxX, maxY2, maxZ2).next();
        buf.vertex(m, maxX2, minY2, maxZ2).next();
        buf.vertex(m, maxX2, maxY2, maxZ2).next();

        buf.vertex(m, maxX, maxY2, maxZ2).next();
        buf.vertex(m, maxX, minY2, maxZ2).next();
        buf.vertex(m, maxX2, minY2, maxZ2).next();

        buf.vertex(m, minX2, maxY2, maxZ2).next();
        buf.vertex(m, minX2, maxY, maxZ2).next();
        buf.vertex(m, maxX2, maxY, maxZ2).next();

        buf.vertex(m, minX2, maxY2, maxZ2).next();
        buf.vertex(m, maxX2, maxY, maxZ2).next();
        buf.vertex(m, maxX2, maxY2, maxZ2).next();

        //

        buf.vertex(m, minX, minY, maxZ).next();
        buf.vertex(m, maxX, minY, maxZ).next();
        buf.vertex(m, maxX2, minY2, maxZ).next();

        buf.vertex(m, minX, minY, maxZ).next();
        buf.vertex(m, maxX2, minY2, maxZ).next();
        buf.vertex(m, minX2, minY2, maxZ).next();

        buf.vertex(m, minX, minY, maxZ).next();
        buf.vertex(m, minX2, minY2, maxZ).next();
        buf.vertex(m, minX2, maxY2, maxZ).next();

        buf.vertex(m, minX, minY, maxZ).next();
        buf.vertex(m, minX2, maxY2, maxZ).next();
        buf.vertex(m, minX, maxY, maxZ).next();

        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, minX, maxY, maxZ).next();
        buf.vertex(m, minX2, maxY2, maxZ).next();

        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, minX2, maxY2, maxZ).next();
        buf.vertex(m, maxX2, maxY2, maxZ).next();

        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, maxX2, maxY2, maxZ).next();
        buf.vertex(m, maxX2, minY2, maxZ).next();

        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, maxX2, minY2, maxZ).next();
        buf.vertex(m, maxX, minY, maxZ).next();

        // x plane

        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, minX, minY, maxZ).next();
        buf.vertex(m, minX, minY2, maxZ2).next();

        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, minX, minY2, maxZ2).next();
        buf.vertex(m, minX, minY2, minZ2).next();

        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, minX, minY2, minZ2).next();
        buf.vertex(m, minX, maxY2, minZ2).next();

        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, minX, maxY2, minZ2).next();
        buf.vertex(m, minX, maxY, minZ).next();

        buf.vertex(m, minX, maxY, maxZ).next();
        buf.vertex(m, minX, maxY, minZ).next();
        buf.vertex(m, minX, maxY2, minZ2).next();

        buf.vertex(m, minX, maxY, maxZ).next();
        buf.vertex(m, minX, maxY2, minZ2).next();
        buf.vertex(m, minX, maxY2, maxZ2).next();

        buf.vertex(m, minX, maxY, maxZ).next();
        buf.vertex(m, minX, maxY2, maxZ2).next();
        buf.vertex(m, minX, minY2, maxZ2).next();

        buf.vertex(m, minX, maxY, maxZ).next();
        buf.vertex(m, minX, minY2, maxZ2).next();
        buf.vertex(m, minX, minY, maxZ).next();

        //

        buf.vertex(m, minX2, maxY2, minZ).next();
        buf.vertex(m, minX2, maxY2, minZ2).next();
        buf.vertex(m, minX2, minY2, minZ2).next();

        buf.vertex(m, minX2, maxY2, minZ).next();
        buf.vertex(m, minX2, minY2, minZ2).next();
        buf.vertex(m, minX2, minY2, minZ).next();

        buf.vertex(m, minX2, minY2, minZ2).next();
        buf.vertex(m, minX2, minY, maxZ2).next();
        buf.vertex(m, minX2, minY, minZ2).next();

        buf.vertex(m, minX2, minY2, minZ2).next();
        buf.vertex(m, minX2, minY2, maxZ2).next();
        buf.vertex(m, minX2, minY, maxZ2).next();

        buf.vertex(m, minX2, maxY2, maxZ).next();
        buf.vertex(m, minX2, minY2, maxZ2).next();
        buf.vertex(m, minX2, maxY2, maxZ2).next();

        buf.vertex(m, minX2, maxY2, maxZ).next();
        buf.vertex(m, minX2, minY2, maxZ).next();
        buf.vertex(m, minX2, minY2, maxZ2).next();

        buf.vertex(m, minX2, maxY2, minZ2).next();
        buf.vertex(m, minX2, maxY, minZ2).next();
        buf.vertex(m, minX2, maxY, maxZ2).next();

        buf.vertex(m, minX2, maxY2, minZ2).next();
        buf.vertex(m, minX2, maxY, maxZ2).next();
        buf.vertex(m, minX2, maxY2, maxZ2).next();

        //

        buf.vertex(m, maxX2, maxY2, minZ).next();
        buf.vertex(m, maxX2, minY2, minZ2).next();
        buf.vertex(m, maxX2, maxY2, minZ2).next();

        buf.vertex(m, maxX2, maxY2, minZ).next();
        buf.vertex(m, maxX2, minY2, minZ).next();
        buf.vertex(m, maxX2, minY2, minZ2).next();

        buf.vertex(m, maxX2, minY2, minZ2).next();
        buf.vertex(m, maxX2, minY, minZ2).next();
        buf.vertex(m, maxX2, minY, maxZ2).next();

        buf.vertex(m, maxX2, minY2, minZ2).next();
        buf.vertex(m, maxX2, minY, maxZ2).next();
        buf.vertex(m, maxX2, minY2, maxZ2).next();

        buf.vertex(m, maxX2, maxY2, maxZ).next();
        buf.vertex(m, maxX2, maxY2, maxZ2).next();
        buf.vertex(m, maxX2, minY2, maxZ2).next();

        buf.vertex(m, maxX2, maxY2, maxZ).next();
        buf.vertex(m, maxX2, minY2, maxZ2).next();
        buf.vertex(m, maxX2, minY2, maxZ).next();

        buf.vertex(m, maxX2, maxY2, minZ2).next();
        buf.vertex(m, maxX2, maxY, maxZ2).next();
        buf.vertex(m, maxX2, maxY, minZ2).next();

        buf.vertex(m, maxX2, maxY2, minZ2).next();
        buf.vertex(m, maxX2, maxY2, maxZ2).next();
        buf.vertex(m, maxX2, maxY, maxZ2).next();

        //

        buf.vertex(m, maxX, minY, minZ).next();
        buf.vertex(m, maxX, minY2, maxZ2).next();
        buf.vertex(m, maxX, minY, maxZ).next();

        buf.vertex(m, maxX, minY, minZ).next();
        buf.vertex(m, maxX, minY2, minZ2).next();
        buf.vertex(m, maxX, minY2, maxZ2).next();

        buf.vertex(m, maxX, minY, minZ).next();
        buf.vertex(m, maxX, maxY2, minZ2).next();
        buf.vertex(m, maxX, minY2, minZ2).next();

        buf.vertex(m, maxX, minY, minZ).next();
        buf.vertex(m, maxX, maxY, minZ).next();
        buf.vertex(m, maxX, maxY2, minZ2).next();

        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, maxX, maxY2, minZ2).next();
        buf.vertex(m, maxX, maxY, minZ).next();

        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, maxX, maxY2, maxZ2).next();
        buf.vertex(m, maxX, maxY2, minZ2).next();

        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, maxX, minY2, maxZ2).next();
        buf.vertex(m, maxX, maxY2, maxZ2).next();

        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, maxX, minY, maxZ).next();
        buf.vertex(m, maxX, minY2, maxZ2).next();

        // y plane

        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, minX2, minY, maxZ2).next();
        buf.vertex(m, minX, minY, maxZ).next();

        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, minX2, minY, minZ2).next();
        buf.vertex(m, minX2, minY, maxZ2).next();

        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, maxX2, minY, minZ2).next();
        buf.vertex(m, minX2, minY, minZ2).next();

        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, maxX, minY, minZ).next();
        buf.vertex(m, maxX2, minY, minZ2).next();

        buf.vertex(m, maxX, minY, maxZ).next();
        buf.vertex(m, maxX2, minY, minZ2).next();
        buf.vertex(m, maxX, minY, minZ).next();

        buf.vertex(m, maxX, minY, maxZ).next();
        buf.vertex(m, maxX2, minY, maxZ2).next();
        buf.vertex(m, maxX2, minY, minZ2).next();

        buf.vertex(m, maxX, minY, maxZ).next();
        buf.vertex(m, minX2, minY, maxZ2).next();
        buf.vertex(m, maxX2, minY, maxZ2).next();

        buf.vertex(m, maxX, minY, maxZ).next();
        buf.vertex(m, minX, minY, maxZ).next();
        buf.vertex(m, minX2, minY, maxZ2).next();

        //

        buf.vertex(m, maxX2, minY2, minZ).next();
        buf.vertex(m, minX2, minY2, minZ2).next();
        buf.vertex(m, maxX2, minY2, minZ2).next();

        buf.vertex(m, maxX2, minY2, minZ).next();
        buf.vertex(m, minX2, minY2, minZ).next();
        buf.vertex(m, minX2, minY2, minZ2).next();

        buf.vertex(m, minX2, minY2, minZ2).next();
        buf.vertex(m, minX, minY2, minZ2).next();
        buf.vertex(m, minX, minY2, maxZ2).next();

        buf.vertex(m, minX2, minY2, minZ2).next();
        buf.vertex(m, minX, minY2, maxZ2).next();
        buf.vertex(m, minX2, minY2, maxZ2).next();

        buf.vertex(m, maxX2, minY2, maxZ).next();
        buf.vertex(m, maxX2, minY2, maxZ2).next();
        buf.vertex(m, minX2, minY2, maxZ2).next();

        buf.vertex(m, maxX2, minY2, maxZ).next();
        buf.vertex(m, minX2, minY2, maxZ2).next();
        buf.vertex(m, minX2, minY2, maxZ).next();

        buf.vertex(m, maxX2, minY2, minZ2).next();
        buf.vertex(m, maxX, minY2, maxZ2).next();
        buf.vertex(m, maxX, minY2, minZ2).next();

        buf.vertex(m, maxX2, minY2, minZ2).next();
        buf.vertex(m, maxX2, minY2, maxZ2).next();
        buf.vertex(m, maxX, minY2, maxZ2).next();

        //

        buf.vertex(m, maxX2, maxY2, minZ).next();
        buf.vertex(m, maxX2, maxY2, minZ2).next();
        buf.vertex(m, minX2, maxY2, minZ2).next();

        buf.vertex(m, maxX2, maxY2, minZ).next();
        buf.vertex(m, minX2, maxY2, minZ2).next();
        buf.vertex(m, minX2, maxY2, minZ).next();

        buf.vertex(m, minX2, maxY2, minZ2).next();
        buf.vertex(m, minX, maxY2, maxZ2).next();
        buf.vertex(m, minX, maxY2, minZ2).next();

        buf.vertex(m, minX2, maxY2, minZ2).next();
        buf.vertex(m, minX2, maxY2, maxZ2).next();
        buf.vertex(m, minX, maxY2, maxZ2).next();

        buf.vertex(m, maxX2, maxY2, maxZ).next();
        buf.vertex(m, minX2, maxY2, maxZ2).next();
        buf.vertex(m, maxX2, maxY2, maxZ2).next();

        buf.vertex(m, maxX2, maxY2, maxZ).next();
        buf.vertex(m, minX2, maxY2, maxZ).next();
        buf.vertex(m, minX2, maxY2, maxZ2).next();

        buf.vertex(m, maxX2, maxY2, minZ2).next();
        buf.vertex(m, maxX, maxY2, minZ2).next();
        buf.vertex(m, maxX, maxY2, maxZ2).next();

        buf.vertex(m, maxX2, maxY2, minZ2).next();
        buf.vertex(m, maxX, maxY2, maxZ2).next();
        buf.vertex(m, maxX2, maxY2, maxZ2).next();

        //

        buf.vertex(m, minX, maxY, minZ).next();
        buf.vertex(m, minX, maxY, maxZ).next();
        buf.vertex(m, minX2, maxY, maxZ2).next();

        buf.vertex(m, minX, maxY, minZ).next();
        buf.vertex(m, minX2, maxY, maxZ2).next();
        buf.vertex(m, minX2, maxY, minZ2).next();

        buf.vertex(m, minX, maxY, minZ).next();
        buf.vertex(m, minX2, maxY, minZ2).next();
        buf.vertex(m, maxX2, maxY, minZ2).next();

        buf.vertex(m, minX, maxY, minZ).next();
        buf.vertex(m, maxX2, maxY, minZ2).next();
        buf.vertex(m, maxX, maxY, minZ).next();

        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, maxX, maxY, minZ).next();
        buf.vertex(m, maxX2, maxY, minZ2).next();

        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, maxX2, maxY, minZ2).next();
        buf.vertex(m, maxX2, maxY, maxZ2).next();

        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, maxX2, maxY, maxZ2).next();
        buf.vertex(m, minX2, maxY, maxZ2).next();

        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, minX2, maxY, maxZ2).next();
        buf.vertex(m, minX, maxY, maxZ).next();
    }

    public static void renderBox(final Immediate buffer,
                                 final MatrixStack ps,
                                 final BlockPos posA,
                                 final BlockPos posB,
                                 final int argbColor)
    {
        renderBox(buffer.getBuffer(COLORED_TRIANGLES),
                ps,
                posA,
                posB,
                (argbColor >> 16) & 0xff,
                (argbColor >> 8) & 0xff,
                argbColor & 0xff,
                (argbColor >> 24) & 0xff);
    }

    public static void renderBox(final VertexConsumer buffer,
                                 final MatrixStack ps,
                                 final BlockPos posA,
                                 final BlockPos posB,
                                 final int red,
                                 final int green,
                                 final int blue,
                                 final int alpha)
    {
        if (alpha == 0)
        {
            return;
        }

        final float minX = Math.min(posA.getX(), posB.getX());
        final float minY = Math.min(posA.getY(), posB.getY());
        final float minZ = Math.min(posA.getZ(), posB.getZ());

        final float maxX = Math.max(posA.getX(), posB.getX()) + 1;
        final float maxY = Math.max(posA.getY(), posB.getY()) + 1;
        final float maxZ = Math.max(posA.getZ(), posB.getZ()) + 1;

        final Matrix4f m = ps.peek().getPositionMatrix();
        buffer.fixedColor(red, green, blue, alpha);

        populateCuboid(minX, minY, minZ, maxX, maxY, maxZ, m, buffer);

        buffer.unfixColor();
    }

    public static void populateCuboid(final float minX,
                                      final float minY,
                                      final float minZ,
                                      final float maxX,
                                      final float maxY,
                                      final float maxZ,
                                      final Matrix4f m,
                                      final VertexConsumer buf)
    {
        // z plane

        buf.vertex(m, minX, maxY, minZ).next();
        buf.vertex(m, maxX, minY, minZ).next();
        buf.vertex(m, minX, minY, minZ).next();

        buf.vertex(m, minX, maxY, minZ).next();
        buf.vertex(m, maxX, maxY, minZ).next();
        buf.vertex(m, maxX, minY, minZ).next();

        buf.vertex(m, minX, maxY, maxZ).next();
        buf.vertex(m, minX, minY, maxZ).next();
        buf.vertex(m, maxX, minY, maxZ).next();

        buf.vertex(m, minX, maxY, maxZ).next();
        buf.vertex(m, maxX, minY, maxZ).next();
        buf.vertex(m, maxX, maxY, maxZ).next();

        // y plane

        buf.vertex(m, minX, minY, maxZ).next();
        buf.vertex(m, minX, minY, minZ).next();
        buf.vertex(m, maxX, minY, minZ).next();

        buf.vertex(m, minX, minY, maxZ).next();
        buf.vertex(m, maxX, minY, minZ).next();
        buf.vertex(m, maxX, minY, maxZ).next();

        buf.vertex(m, minX, maxY, maxZ).next();
        buf.vertex(m, maxX, maxY, minZ).next();
        buf.vertex(m, minX, maxY, minZ).next();

        buf.vertex(m, minX, maxY, maxZ).next();
        buf.vertex(m, maxX, maxY, maxZ).next();
        buf.vertex(m, maxX, maxY, minZ).next();

        // x plane

        buf.vertex(m, minX, minY, maxZ).next();
        buf.vertex(m, minX, maxY, minZ).next();
        buf.vertex(m, minX, minY, minZ).next();

        buf.vertex(m, minX, minY, maxZ).next();
        buf.vertex(m, minX, maxY, maxZ).next();
        buf.vertex(m, minX, maxY, minZ).next();

        buf.vertex(m, maxX, minY, maxZ).next();
        buf.vertex(m, maxX, minY, minZ).next();
        buf.vertex(m, maxX, maxY, minZ).next();

        buf.vertex(m, maxX, minY, maxZ).next();
        buf.vertex(m, maxX, maxY, minZ).next();
        buf.vertex(m, maxX, maxY, maxZ).next();
    }

    public static void renderFillRectangle(final Immediate buffer,
                                           final MatrixStack ps,
                                           final int x,
                                           final int y,
                                           final int z,
                                           final int w,
                                           final int h,
                                           final int argbColor)
    {
        populateRectangle(x,
                y,
                z,
                w,
                h,
                (argbColor >> 16) & 0xff,
                (argbColor >> 8) & 0xff,
                argbColor & 0xff,
                (argbColor >> 24) & 0xff,
                buffer.getBuffer(COLORED_TRIANGLES_NC_ND),
                ps.peek().getPositionMatrix());
    }

    public static void populateRectangle(final int x,
                                         final int y,
                                         final int z,
                                         final int w,
                                         final int h,
                                         final int red,
                                         final int green,
                                         final int blue,
                                         final int alpha,
                                         final VertexConsumer buffer,
                                         final Matrix4f m)
    {
        if (alpha == 0)
        {
            return;
        }

        buffer.vertex(m, x, y, z).color(red, green, blue, alpha).next();
        buffer.vertex(m, x, y + h, z).color(red, green, blue, alpha).next();
        buffer.vertex(m, x + w, y + h, z).color(red, green, blue, alpha).next();

        buffer.vertex(m, x, y, z).color(red, green, blue, alpha).next();
        buffer.vertex(m, x + w, y + h, z).color(red, green, blue, alpha).next();
        buffer.vertex(m, x + w, y, z).color(red, green, blue, alpha).next();
    }

    /**
     * Renders the given list of strings, 3 elements a row.
     *
     * @param pos                     position to render at
     * @param text                    text list
     * @param matrixStack             stack to use
     * @param buffer                  render buffer
     * @param forceWhite              force white for no depth rendering
     * @param mergeEveryXListElements merge every X elements of text list using a tostring call
     */
    @SuppressWarnings("resource")
    public static void renderDebugText(final BlockPos pos,
                                       final List<String> text,
                                       final MatrixStack matrixStack,
                                       final boolean forceWhite,
                                       final int mergeEveryXListElements,
                                       final VertexConsumerProvider buffer)
    {
        if (mergeEveryXListElements < 1)
        {
            throw new IllegalArgumentException("mergeEveryXListElements is less than 1");
        }

        final EntityRenderDispatcher erm = MinecraftClient.getInstance().getEntityRenderDispatcher();
        final int cap = text.size();
        if (cap > 0 && erm.getSquaredDistanceToCamera(pos.getX(), pos.getY(), pos.getZ()) <= MAX_DEBUG_TEXT_RENDER_DIST_SQUARED)
        {
            final TextRenderer fontrenderer = MinecraftClient.getInstance().textRenderer;

            matrixStack.push();
            matrixStack.translate(pos.getX() + 0.5d, pos.getY() + 0.75d, pos.getZ() + 0.5d);
            matrixStack.multiply(erm.getRotation());
            matrixStack.scale(-0.014f, -0.014f, 0.014f);
            matrixStack.translate(0.0d, 18.0d, 0.0d);

            final float backgroundTextOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            final int alphaMask = (int) (backgroundTextOpacity * 255.0F) << 24;

            final Matrix4f rawPosMatrix = matrixStack.peek().getPositionMatrix();

            for (int i = 0; i < cap; i += mergeEveryXListElements)
            {
                final MutableText renderText = Text.literal(
                        mergeEveryXListElements == 1 ? text.get(i) : text.subList(i, Math.min(i + mergeEveryXListElements, cap)).toString());
                final float textCenterShift = (float) (-fontrenderer.getWidth(renderText) / 2);

                fontrenderer.draw(renderText,
                        textCenterShift,
                        0,
                        forceWhite ? 0xffffffff : 0x20ffffff,
                        false,
                        rawPosMatrix,
                        buffer,
                        TextRenderer.TextLayerType.SEE_THROUGH,
                        alphaMask,
                        0x00f000f0);
                if (!forceWhite)
                {
                    fontrenderer.draw(renderText, textCenterShift, 0, 0xffffffff, false, rawPosMatrix, buffer, TextRenderer.TextLayerType.NORMAL, 0, 0x00f000f0);
                }
                matrixStack.translate(0.0d, fontrenderer.fontHeight + 1, 0.0d);
            }

            matrixStack.pop();
        }
    }

    private static final class RenderTypes extends RenderLayer
    {
        private RenderTypes(final String nameIn,
                            final VertexFormat formatIn,
                            final VertexFormat.DrawMode drawModeIn,
                            final int bufferSizeIn,
                            final boolean useDelegateIn,
                            final boolean needsSortingIn,
                            final Runnable setupTaskIn,
                            final Runnable clearTaskIn)
        {
            super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
            throw new IllegalStateException();
        }

        private static final RenderLayer GLINT_LINES = of("structurize_glint_lines",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.DEBUG_LINES,
                1 << 12,
                false,
                false,
                MultiPhaseParameters.builder()
                        .texture(NO_TEXTURE)
                        .program(COLOR_PROGRAM)
                        .transparency(GLINT_TRANSPARENCY)
                        .depthTest(ALWAYS_DEPTH_TEST)
                        .cull(DISABLE_CULLING)
                        .lightmap(DISABLE_LIGHTMAP)
                        .overlay(DISABLE_OVERLAY_COLOR)
                        .layering(NO_LAYERING)
                        .target(MAIN_TARGET)
                        .texturing(DEFAULT_TEXTURING)
                        .writeMaskState(COLOR_MASK)
                        .build(false));

        private static final RenderLayer GLINT_LINES_WITH_WIDTH = of("structurize_glint_lines_with_width",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.TRIANGLES,
                1 << 13,
                false,
                false,
                MultiPhaseParameters.builder()
                        .texture(NO_TEXTURE)
                        .program(COLOR_PROGRAM)
                        .transparency(GLINT_TRANSPARENCY)
                        .depthTest(AlwaysDepthTestStateShard.ALWAYS_DEPTH_TEST)
                        .setCullState(ENABLE_CULLING)
                        .setLightmapState(DISABLE_LIGHTMAP)
                        .setOverlayState(DISABLE_OVERLAY_COLOR)
                        .setLayeringState(NO_LAYERING)
                        .setOutputState(MAIN_TARGET)
                        .setTexturingState(DEFAULT_TEXTURING)
                        .setWriteMaskState(ALL_MASK)
                        .createCompositeState(false));

        private static final RenderLayer LINES = of("structurize_lines",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.DEBUG_LINES,
                1 << 14,
                false,
                false,
                MultiPhaseParameters.builder()
                        .texture(NO_TEXTURE)
                        .program(COLOR_PROGRAM)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .depthTest(LEQUAL_DEPTH_TEST)
                        .cull(DISABLE_CULLING)
                        .lightmap(DISABLE_LIGHTMAP)
                        .overlay(DISABLE_OVERLAY_COLOR)
                        .layering(NO_LAYERING)
                        .target(MAIN_TARGET)
                        .texturing(DEFAULT_TEXTURING)
                        .writeMaskState(COLOR_MASK)
                        .build(false));

        private static final RenderLayer LINES_WITH_WIDTH = of("structurize_lines_with_width",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.TRIANGLES,
                1 << 13,
                false,
                false,
                MultiPhaseParameters.builder()
                        .texture(NO_TEXTURE)
                        .program(COLOR_PROGRAM)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .depthTest(LEQUAL_DEPTH_TEST)
                        .cull(ENABLE_CULLING)
                        .lightmap(DISABLE_LIGHTMAP)
                        .overlay(DISABLE_OVERLAY_COLOR)
                        .layering(NO_LAYERING)
                        .target(MAIN_TARGET)
                        .texturing(DEFAULT_TEXTURING)
                        .writeMaskState(ALL_MASK)
                        .build(false));

        private static final RenderLayer COLORED_TRIANGLES = of("structurize_colored_triangles",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.TRIANGLES,
                1 << 13,
                false,
                false,
                MultiPhaseParameters.builder()
                        .texture(NO_TEXTURE)
                        .program(COLOR_PROGRAM)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .depthTest(LEQUAL_DEPTH_TEST)
                        .cull(ENABLE_CULLING)
                        .lightmap(DISABLE_LIGHTMAP)
                        .overlay(DISABLE_OVERLAY_COLOR)
                        .layering(NO_LAYERING)
                        .target(MAIN_TARGET)
                        .texturing(DEFAULT_TEXTURING)
                        .writeMaskState(ALL_MASK)
                        .build(false));

        private static final RenderLayer COLORED_TRIANGLES_NC_ND = of("structurize_colored_triangles_nc_nd",
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.TRIANGLES,
                1 << 12,
                false,
                false,
                MultiPhaseParameters.builder()
                        .texture(NO_TEXTURE)
                        .program(COLOR_PROGRAM)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .depthTest(ALWAYS_DEPTH_TEST)
                        .cull(DISABLE_CULLING)
                        .lightmap(DISABLE_LIGHTMAP)
                        .overlay(DISABLE_OVERLAY_COLOR)
                        .layering(NO_LAYERING)
                        .target(MAIN_TARGET)
                        .texturing(DEFAULT_TEXTURING)
                        .writeMaskState(COLOR_MASK)
                        .build(false));
    }

    public static class AlwaysDepthTestStateShard extends DepthTestStateShard
    {
        public static final DepthTestStateShard ALWAYS_DEPTH_TEST = new AlwaysDepthTestStateShard();

        private AlwaysDepthTestStateShard()
        {
            super("true_always", -1);
            setupState = () -> {
                RenderSystem.enableDepthTest();
                RenderSystem.depthFunc(GL11.GL_ALWAYS);
            };
        }
    }
}
