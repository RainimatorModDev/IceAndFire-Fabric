package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityEggInIce;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.entity.util.IDeadMob;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class EntityDragonEgg extends LivingEntity implements IBlacklistedFromStatues, IDeadMob {

    protected static final TrackedData<Optional<UUID>> OWNER_UNIQUE_ID = DataTracker.registerData(EntityDragonEgg.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Integer> DRAGON_TYPE = DataTracker.registerData(EntityDragonEgg.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DRAGON_AGE = DataTracker.registerData(EntityDragonEgg.class, TrackedDataHandlerRegistry.INTEGER);

    public EntityDragonEgg(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0D);
    }

    @Override
    public void writeCustomDataToNbt(@NotNull NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putInt("Color", (byte) this.getEggType().ordinal());
        tag.putInt("DragonAge", this.getDragonAge());
        try {
            if (this.getOwnerId() == null) {
                tag.putString("OwnerUUID", "");
            } else {
                tag.putString("OwnerUUID", this.getOwnerId().toString());
            }
        } catch (Exception e) {
            IceAndFire.LOGGER.error("An error occurred while trying to read the NBT data of a dragon egg", e);
        }
    }

    @Override
    public void readCustomDataFromNbt(@NotNull NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        this.setEggType(EnumDragonEgg.values()[tag.getInt("Color")]);
        this.setDragonAge(tag.getInt("DragonAge"));
        String s;

        if (tag.contains("OwnerUUID", 8)) {
            s = tag.getString("OwnerUUID");
        } else {
            String s1 = tag.getString("Owner");
            UUID converedUUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), s1);
            s = converedUUID == null ? s1 : converedUUID.toString();
        }
        if (!s.isEmpty()) {
            this.setOwnerId(UUID.fromString(s));
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(DRAGON_TYPE, 0);
        this.getDataTracker().startTracking(DRAGON_AGE, 0);
        this.getDataTracker().startTracking(OWNER_UNIQUE_ID, Optional.empty());
    }

    public UUID getOwnerId() {
        return this.dataTracker.get(OWNER_UNIQUE_ID).orElse(null);
    }

    public void setOwnerId(UUID p_184754_1_) {
        this.dataTracker.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public EnumDragonEgg getEggType() {
        return EnumDragonEgg.values()[this.getDataTracker().get(DRAGON_TYPE)];
    }

    public void setEggType(EnumDragonEgg newtype) {
        this.getDataTracker().set(DRAGON_TYPE, newtype.ordinal());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource i) {
        return i.getAttacker() != null && super.isInvulnerableTo(i);
    }

    public int getDragonAge() {
        return this.getDataTracker().get(DRAGON_AGE);
    }

    public void setDragonAge(int i) {
        this.getDataTracker().set(DRAGON_AGE, i);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            this.setAir(200);
            updateEggCondition();
        }
    }

    public void updateEggCondition() {
        DragonType dragonType = getEggType().dragonType;

        if (dragonType == DragonType.FIRE) {
            if (getWorld().getBlockState(getBlockPos()).isBurning(getWorld(), getBlockPos())) {
                setDragonAge(getDragonAge() + 1);
            }
        } else if (dragonType == DragonType.ICE) {
            BlockState state = getWorld().getBlockState(getBlockPos());

            if (state.isOf(Blocks.WATER) && getRandom().nextInt(500) == 0) {
                getWorld().setBlockState(getBlockPos(), IafBlockRegistry.EGG_IN_ICE.get().getDefaultState());
                getWorld().playSound(getX(), getY() + getStandingEyeHeight(), getZ(), SoundEvents.BLOCK_GLASS_BREAK, getSoundCategory(), 2.5F, 1.0F, false);

                if (getWorld().getBlockEntity(getBlockPos()) instanceof TileEntityEggInIce eggInIce) {
                    eggInIce.type = getEggType();
                    eggInIce.ownerUUID = getOwnerId();
                }

                remove(RemovalReason.DISCARDED);
            }
        } else if (dragonType == DragonType.LIGHTNING) {
            BlockPos.Mutable mutablePosition = new BlockPos.Mutable(getX(), getY(), getZ());
            boolean isRainingAt = getWorld().hasRain(mutablePosition) || getWorld().hasRain(mutablePosition.set(getX(), getY() + (double) getHeight(), getZ()));

            if (getWorld().isSkyVisible(getBlockPos().up()) && isRainingAt) {
                setDragonAge(getDragonAge() + 1);
            }
        }

        if (getDragonAge() > IafConfig.dragonEggTime) {
            getWorld().setBlockState(getBlockPos(), Blocks.AIR.getDefaultState());
            EntityDragonBase dragon = dragonType.getEntity().create(getWorld());

            if (hasCustomName()) {
                dragon.setCustomName(getCustomName());
            }

            if (dragonType == DragonType.LIGHTNING) {
                dragon.setVariant(getEggType().ordinal() - 8);
            } else {
                dragon.setVariant(getEggType().ordinal());
            }

            dragon.setGender(getRandom().nextBoolean());
            dragon.setPosition(getBlockPos().getX() + 0.5, getBlockPos().getY() + 1, getBlockPos().getZ() + 0.5);
            dragon.setHunger(50);

            if (!getWorld().isClient()) {
                getWorld().spawnEntity(dragon);
            }

            if (hasCustomName()) { // FIXME :: Why do this again?
                dragon.setCustomName(getCustomName());
            }

            dragon.setTamed(true);
            dragon.setOwnerUuid(getOwnerId());

            if (dragonType == DragonType.LIGHTNING) {
                LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(getWorld());
                bolt.setPosition(getX(), getY(), getZ());
                bolt.setCosmetic(true);

                if (!getWorld().isClient()) {
                    getWorld().spawnEntity(bolt);
                }

                getWorld().playSound(getX(), getY() + getStandingEyeHeight(), getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, getSoundCategory(), 2.5F, 1.0F, false);
            } else if (dragonType == DragonType.FIRE) {
                getWorld().playSound(getX(), getY() + getStandingEyeHeight(), getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, getSoundCategory(), 2.5F, 1.0F, false);
            }

            getWorld().playSound(getX(), getY() + getStandingEyeHeight(), getZ(), IafSoundRegistry.EGG_HATCH, getSoundCategory(), 2.5F, 1.0F, false);
            remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return null;
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorItems() {
        return ImmutableList.of();
    }

    @Override
    public @NotNull ItemStack getEquippedStack(@NotNull EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(@NotNull EquipmentSlot slotIn, @NotNull ItemStack stack) {

    }

    @Override
    public boolean damage(@NotNull DamageSource var1, float var2) {
        if (var1.isIn(DamageTypeTags.IS_FIRE) && getEggType().dragonType == DragonType.FIRE)
            return false;
        if (!this.getWorld().isClient && !var1.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !isRemoved()) {
            this.dropItem(this.getItem().getItem(), 1);
        }
        this.remove(RemovalReason.KILLED);
        return true;
    }

    private ItemStack getItem() {
        return switch (getEggType().ordinal()) {
            default -> new ItemStack(IafItemRegistry.DRAGONEGG_RED.get());
            case 1 -> new ItemStack(IafItemRegistry.DRAGONEGG_GREEN.get());
            case 2 -> new ItemStack(IafItemRegistry.DRAGONEGG_BRONZE.get());
            case 3 -> new ItemStack(IafItemRegistry.DRAGONEGG_GRAY.get());
            case 4 -> new ItemStack(IafItemRegistry.DRAGONEGG_BLUE.get());
            case 5 -> new ItemStack(IafItemRegistry.DRAGONEGG_WHITE.get());
            case 6 -> new ItemStack(IafItemRegistry.DRAGONEGG_SAPPHIRE.get());
            case 7 -> new ItemStack(IafItemRegistry.DRAGONEGG_SILVER.get());
            case 8 -> new ItemStack(IafItemRegistry.DRAGONEGG_ELECTRIC.get());
            case 9 -> new ItemStack(IafItemRegistry.DRAGONEGG_AMYTHEST.get());
            case 10 -> new ItemStack(IafItemRegistry.DRAGONEGG_COPPER.get());
            case 11 -> new ItemStack(IafItemRegistry.DRAGONEGG_BLACK.get());
        };
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public @NotNull Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    protected void pushAway(@NotNull Entity entity) {
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    public void onPlayerPlace(PlayerEntity player) {
        this.setOwnerId(player.getUuid());
    }

    @Override
    public boolean isMobDead() {
        return true;
    }
}
