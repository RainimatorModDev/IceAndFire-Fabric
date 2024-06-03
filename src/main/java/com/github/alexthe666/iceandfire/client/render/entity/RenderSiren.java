package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.model.ModelSiren;
import com.github.alexthe666.iceandfire.entity.EntitySiren;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class RenderSiren extends MobEntityRenderer<EntitySiren, ModelSiren> {

    public static final Identifier TEXTURE_0 = new Identifier(IceAndFire.MOD_ID,"textures/models/siren/siren_0.png");
    public static final Identifier TEXTURE_0_AGGRESSIVE = new Identifier(IceAndFire.MOD_ID,"textures/models/siren/siren_0_aggressive.png");
    public static final Identifier TEXTURE_1 = new Identifier(IceAndFire.MOD_ID,"textures/models/siren/siren_1.png");
    public static final Identifier TEXTURE_1_AGGRESSIVE = new Identifier(IceAndFire.MOD_ID,"textures/models/siren/siren_1_aggressive.png");
    public static final Identifier TEXTURE_2 = new Identifier(IceAndFire.MOD_ID,"textures/models/siren/siren_2.png");
    public static final Identifier TEXTURE_2_AGGRESSIVE = new Identifier(IceAndFire.MOD_ID,"textures/models/siren/siren_2_aggressive.png");

    public RenderSiren(EntityRendererFactory.Context context) {
        super(context, new ModelSiren(), 0.8F);
    }

    @Override
    public void scale(@NotNull EntitySiren LivingEntityIn, MatrixStack stack, float partialTickTime) {
        stack.translate(0, 0, -0.5F);

    }

    @Override
    public @NotNull Identifier getTexture(EntitySiren siren) {
        return switch (siren.getHairColor()) {
            default -> siren.isAgressive() ? TEXTURE_0_AGGRESSIVE : TEXTURE_0;
            case 1 -> siren.isAgressive() ? TEXTURE_1_AGGRESSIVE : TEXTURE_1;
            case 2 -> siren.isAgressive() ? TEXTURE_2_AGGRESSIVE : TEXTURE_2;
        };
    }

    public static Identifier getSirenOverlayTexture(int siren) {
        return switch (siren) {
            default -> TEXTURE_0;
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
        };
    }

}
