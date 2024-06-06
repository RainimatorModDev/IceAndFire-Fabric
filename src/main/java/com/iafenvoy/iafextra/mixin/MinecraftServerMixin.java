package com.iafenvoy.iafextra.mixin;

import com.iafenvoy.iafextra.StaticVariables;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci, @Local(ordinal = 1) long m) {
        StaticVariables.MSPT = (double) m / 1_000_000;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onServerCreate(CallbackInfo ci) {
        StaticVariables.server = (MinecraftServer) (Object) this;
    }
}
