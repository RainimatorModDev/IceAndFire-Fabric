package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelTrollWeapon;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.github.alexthe666.iceandfire.item.ItemTrollWeapon;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenderTrollWeapon extends BuiltinModelItemRenderer {
    private static final ModelTrollWeapon MODEL = new ModelTrollWeapon();

    public RenderTrollWeapon(BlockEntityRenderDispatcher dispatcher, EntityModelLoader set) {
        super(dispatcher, set);
    }

    @Override
    public void render(ItemStack stack, @NotNull ModelTransformationMode type, @NotNull MatrixStack stackIn, @NotNull VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        EnumTroll.Weapon weapon = EnumTroll.Weapon.AXE;
        if (stack.getItem() instanceof ItemTrollWeapon)
            weapon = ((ItemTrollWeapon) stack.getItem()).weapon;

        stackIn.push();
        stackIn.translate(0.5F, -0.75F, 0.5F);
        MODEL.render(stackIn, bufferIn.getBuffer(RenderLayer.getEntityCutout(weapon.TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        stackIn.pop();
    }
}
