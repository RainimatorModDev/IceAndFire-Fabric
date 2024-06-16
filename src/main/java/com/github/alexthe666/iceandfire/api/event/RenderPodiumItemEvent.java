package com.github.alexthe666.iceandfire.api.event;

import com.github.alexthe666.iceandfire.client.render.block.RenderPodium;
import com.github.alexthe666.iceandfire.entity.block.BlockEntityPodium;
import dev.arktechmc.iafextra.event.Event;
import net.minecraft.item.ItemStack;

/*
    Called before an item is rendered on a podium. Cancel to remove default render of item
 */
public class RenderPodiumItemEvent extends Event {
    final float partialTicks;
    final double x;
    final double y;
    final double z;
    private final RenderPodium<?> render;
    private final BlockEntityPodium podium;

    public RenderPodiumItemEvent(RenderPodium<?> renderPodium, BlockEntityPodium podium, float partialTicks, double x,
                                 double y, double z) {
        this.render = renderPodium;
        this.podium = podium;
        this.partialTicks = partialTicks;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RenderPodium<?> getRender() {
        return this.render;
    }

    public ItemStack getItemStack() {
        return this.podium.getStack(0);
    }

    public BlockEntityPodium getPodium() {
        return this.podium;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}
