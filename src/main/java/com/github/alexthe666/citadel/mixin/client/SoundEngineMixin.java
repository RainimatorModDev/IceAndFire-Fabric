package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.tick.ClientTickRateTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundSystem.class)
public class SoundEngineMixin {

    @Inject(
            method = {"Lnet/minecraft/client/sounds/SoundEngine;calculatePitch(Lnet/minecraft/client/resources/sounds/SoundInstance;)F"},
            remap = CitadelConstants.REMAPREFS,
            cancellable = true,
            at = @At(value = "RETURN")
    )
    protected void citadel_setupRotations(SoundInstance soundInstance, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValue() * ClientTickRateTracker.getForClient(MinecraftClient.getInstance()).modifySoundPitch(soundInstance));
    }
}
