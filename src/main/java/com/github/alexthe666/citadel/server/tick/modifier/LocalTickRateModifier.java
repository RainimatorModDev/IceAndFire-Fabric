package com.github.alexthe666.citadel.server.tick.modifier;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


public abstract class LocalTickRateModifier extends TickRateModifier {

    private double range;
    private final RegistryKey<World> dimension;

    public LocalTickRateModifier(TickRateModifierType localPosition, double range, RegistryKey<World> dimension, int durationInMasterTicks, float tickRateMultiplier) {
        super(localPosition, durationInMasterTicks, tickRateMultiplier);
        this.range = range;
        this.dimension = dimension;
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = super.toTag();
        tag.putDouble("Range", range);
        tag.putString("Dimension", dimension.getValue().toString());
        return tag;
    }

    public LocalTickRateModifier(NbtCompound tag) {
        super(tag);
        this.range = tag.getDouble("Range");
        RegistryKey<World> dimFromTag = World.OVERWORLD;
        if(tag.contains("Dimension")){
            dimFromTag = RegistryKey.of(RegistryKeys.WORLD, new Identifier(tag.getString("dimension")));
        }
        this.dimension = dimFromTag;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public abstract Vec3d getCenter(World level);

    @Override
    public boolean appliesTo(World level, double x, double y, double z) {
        Vec3d center = getCenter(level);
        return center.squaredDistanceTo(x, y, z) < range * range;
    }
}
