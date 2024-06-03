package com.github.alexthe666.iceandfire.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.world.World;
import net.minecraftforge.network.PlayMessages;

public class EntityCyclopsEye extends EntityMutlipartPart {

    public EntityCyclopsEye(EntityType<?> t, World world) {
        super(t, world);
    }

    public EntityCyclopsEye(PlayMessages.SpawnEntity spawnEntity, World worldIn) {
        this(IafEntityRegistry.CYCLOPS_MULTIPART.get(), worldIn);
    }

    public EntityCyclopsEye(LivingEntity parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(IafEntityRegistry.CYCLOPS_MULTIPART.get(), parent, radius, angleYaw, offsetY, sizeX, sizeY,
            damageMultiplier);
    }

    @Override
    public boolean damage(DamageSource source, float damage) {
        Entity parent = this.getParent();
        if (parent instanceof EntityCyclops && source.isOf(DamageTypes.ARROW)) {
            ((EntityCyclops) parent).onHitEye(source, damage);
            return true;
        } else {
            return parent != null && parent.damage(source, damage);
        }
    }
}
