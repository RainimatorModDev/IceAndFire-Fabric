package com.github.alexthe666.iceandfire.recipe;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.*;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class IafRecipeRegistry {
    public static final LazyRegistrar<RecipeType<?>> RECIPE_TYPE = LazyRegistrar.create(RegistryKeys.RECIPE_TYPE, IceAndFire.MOD_ID);
    public static final RegistryObject<RecipeType<DragonForgeRecipe>> DRAGON_FORGE_TYPE = RECIPE_TYPE.register("dragonforge", () -> RecipeType.register("dragonforge"));

    @SubscribeEvent
    public static void preInit(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(IafItemRegistry.STYMPHALIAN_ARROW.get(), new ProjectileDispenserBehavior() {
                /**
                 * Return the projectile entity spawned by this dispense behavior.
                 */
                @Override
                protected @NotNull ProjectileEntity createProjectile(@NotNull World worldIn, @NotNull Position position, @NotNull ItemStack stackIn) {
                    EntityStymphalianArrow entityarrow = new EntityStymphalianArrow(IafEntityRegistry.STYMPHALIAN_ARROW.get(), worldIn, position.getX(), position.getY(), position.getZ());
                    entityarrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                    return entityarrow;
                }
            });
            DispenserBlock.registerBehavior(IafItemRegistry.AMPHITHERE_ARROW.get(), new ProjectileDispenserBehavior() {
                /**
                 * Return the projectile entity spawned by this dispense behavior.
                 */
                @Override
                protected @NotNull ProjectileEntity createProjectile(@NotNull World worldIn, @NotNull Position position, @NotNull ItemStack stackIn) {
                    EntityAmphithereArrow entityarrow = new EntityAmphithereArrow(IafEntityRegistry.AMPHITHERE_ARROW.get(), worldIn, position.getX(), position.getY(), position.getZ());
                    entityarrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                    return entityarrow;
                }
            });
            DispenserBlock.registerBehavior(IafItemRegistry.SEA_SERPENT_ARROW.get(), new ProjectileDispenserBehavior() {
                /**
                 * Return the projectile entity spawned by this dispense behavior.
                 */
                @Override
                protected @NotNull ProjectileEntity createProjectile(@NotNull World worldIn, @NotNull Position position, @NotNull ItemStack stackIn) {
                    EntitySeaSerpentArrow entityarrow = new EntitySeaSerpentArrow(IafEntityRegistry.SEA_SERPENT_ARROW.get(), worldIn, position.getX(), position.getY(), position.getZ());
                    entityarrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                    return entityarrow;
                }
            });
            DispenserBlock.registerBehavior(IafItemRegistry.DRAGONBONE_ARROW.get(), new ProjectileDispenserBehavior() {
                /**
                 * Return the projectile entity spawned by this dispense behavior.
                 */
                @Override
                protected @NotNull ProjectileEntity createProjectile(@NotNull World worldIn, @NotNull Position position, @NotNull ItemStack stackIn) {
                    EntityDragonArrow entityarrow = new EntityDragonArrow(IafEntityRegistry.DRAGON_ARROW.get(), position.getX(), position.getY(), position.getZ(), worldIn);
                    entityarrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                    return entityarrow;
                }
            });
            DispenserBlock.registerBehavior(IafItemRegistry.HYDRA_ARROW.get(), new ProjectileDispenserBehavior() {
                /**
                 * Return the projectile entity spawned by this dispense behavior.
                 */
                @Override
                protected @NotNull ProjectileEntity createProjectile(@NotNull World worldIn, @NotNull Position position, @NotNull ItemStack stackIn) {
                    EntityHydraArrow entityarrow = new EntityHydraArrow(IafEntityRegistry.HYDRA_ARROW.get(), worldIn, position.getX(), position.getY(), position.getZ());
                    entityarrow.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                    return entityarrow;
                }
            });
            DispenserBlock.registerBehavior(IafItemRegistry.HIPPOGRYPH_EGG.get(), new ProjectileDispenserBehavior() {
                /**
                 * Return the projectile entity spawned by this dispense behavior.
                 */
                @Override
                protected @NotNull ProjectileEntity createProjectile(@NotNull World worldIn, @NotNull Position position, @NotNull ItemStack stackIn) {
                    return new EntityHippogryphEgg(IafEntityRegistry.HIPPOGRYPH_EGG.get(), worldIn, position.getX(), position.getY(), position.getZ(), stackIn);
                }
            });
            DispenserBlock.registerBehavior(IafItemRegistry.ROTTEN_EGG.get(), new ProjectileDispenserBehavior() {
                /**
                 * Return the projectile entity spawned by this dispense behavior.
                 */
                @Override
                protected @NotNull ProjectileEntity createProjectile(@NotNull World worldIn, @NotNull Position position, @NotNull ItemStack stackIn) {
                    return new EntityCockatriceEgg(IafEntityRegistry.COCKATRICE_EGG.get(), position.getX(), position.getY(), position.getZ(), worldIn);
                }
            });
            DispenserBlock.registerBehavior(IafItemRegistry.DEATHWORM_EGG.get(), new ProjectileDispenserBehavior() {
                /**
                 * Return the projectile entity spawned by this dispense behavior.
                 */
                @Override
                protected @NotNull ProjectileEntity createProjectile(@NotNull World worldIn, @NotNull Position position, @NotNull ItemStack stackIn) {
                    return new EntityDeathWormEgg(IafEntityRegistry.DEATH_WORM_EGG.get(), position.getX(), position.getY(), position.getZ(), worldIn, false);
                }
            });
            DispenserBlock.registerBehavior(IafItemRegistry.DEATHWORM_EGG_GIGANTIC.get(), new ProjectileDispenserBehavior() {
                /**
                 * Return the projectile entity spawned by this dispense behavior.
                 */
                @Override
                protected @NotNull ProjectileEntity createProjectile(@NotNull World worldIn, @NotNull Position position, @NotNull ItemStack stackIn) {
                    return new EntityDeathWormEgg(IafEntityRegistry.DEATH_WORM_EGG.get(), position.getX(), position.getY(), position.getZ(), worldIn, true);
                }
            });
            BrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, IafItemRegistry.SHINY_SCALES.get(), Potions.WATER_BREATHING);
        });
    }

    public static ItemStack createPotion(Potion potion) {
        return PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
    }
}
