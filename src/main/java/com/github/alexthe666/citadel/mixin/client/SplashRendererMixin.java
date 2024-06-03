package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventRenderSplashText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashTextRenderer.class)
public class SplashRendererMixin {

    @Mutable
    @Shadow
    @Final
    private String splash;

    private int splashTextColor = -1;

    @Inject(
            method = {"Lnet/minecraft/client/gui/components/SplashRenderer;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                    shift = At.Shift.BEFORE
            ))
    protected void citadel_preRenderSplashText(DrawContext guiGraphics, int width, TextRenderer font, int loadProgress, CallbackInfo ci) {
        guiGraphics.getMatrices().push();
        EventRenderSplashText.Pre event = new EventRenderSplashText.Pre(splash, guiGraphics, MinecraftClient.getInstance().getPartialTick(), 16776960);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.getResult() == Event.Result.ALLOW) {
            splash = event.getSplashText();
            splashTextColor = event.getSplashTextColor();
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/gui/components/SplashRenderer;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V",
                    shift = At.Shift.AFTER
            )
    )
    protected void citadel_postRenderSplashText(DrawContext guiGraphics, int width, TextRenderer font, int loadProgress, CallbackInfo ci) {
        EventRenderSplashText.Post event = new EventRenderSplashText.Post(splash, guiGraphics, MinecraftClient.getInstance().getPartialTick());
        MinecraftForge.EVENT_BUS.post(event);
        guiGraphics.getMatrices().pop();
    }

    @ModifyConstant(
            method = {"Lnet/minecraft/client/gui/components/SplashRenderer;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V"},
            constant = @Constant(intValue = 16776960))
    private int citadel_splashTextColor(int value) {
        return splashTextColor == -1 ? value : splashTextColor;
    }
}
