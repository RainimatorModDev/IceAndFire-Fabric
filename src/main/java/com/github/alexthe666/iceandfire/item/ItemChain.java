package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.entity.EntityChainTie;
import com.github.alexthe666.iceandfire.entity.props.EntityDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ItemChain extends Item {

    private final boolean sticky;

    public ItemChain(boolean sticky) {
        super(new Settings()/*.tab(IceAndFire.TAB_ITEMS)*/);
        this.sticky = sticky;
    }

    public static void attachToFence(PlayerEntity player, World worldIn, BlockPos fence) {
        double d0 = 30.0D;
        int i = fence.getX();
        int j = fence.getY();
        int k = fence.getZ();

        for (LivingEntity livingEntity : worldIn.getNonSpectatingEntities(LivingEntity.class, new Box((double) i - d0, (double) j - d0, (double) k - d0, (double) i + d0, (double) j + d0, (double) k + d0))) {
            EntityDataProvider.getCapability(livingEntity).ifPresent(data -> {
                if (data.chainData.isChainedTo(player)) {
                    EntityChainTie entityleashknot = EntityChainTie.getKnotForPosition(worldIn, fence);

                    if (entityleashknot == null) {
                        entityleashknot = EntityChainTie.createTie(worldIn, fence);
                    }

                    data.chainData.removeChain(player);
                    data.chainData.attachChain(entityleashknot);
                }
            });
        }
    }

    @Override
    public void appendTooltip(@NotNull ItemStack stack, World worldIn, List<Text> tooltip, @NotNull TooltipContext flagIn) {
        tooltip.add(Text.translatable("item.iceandfire.chain.desc_0").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.chain.desc_1").formatted(Formatting.GRAY));
        if (this.sticky) {
            tooltip.add(Text.translatable("item.iceandfire.chain_sticky.desc_2").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("item.iceandfire.chain_sticky.desc_3").formatted(Formatting.GREEN));
        }
    }

    @Override
    public @NotNull ActionResult useOnEntity(@NotNull ItemStack stack, @NotNull PlayerEntity playerIn, @NotNull LivingEntity target, @NotNull Hand hand) {
        EntityDataProvider.getCapability(target).ifPresent(targetData -> {
            if (targetData.chainData.isChainedTo(playerIn)) {
                return;
            }

            if (this.sticky) {
                double d0 = 60.0D;
                double i = playerIn.getX();
                double j = playerIn.getY();
                double k = playerIn.getZ();
                List<LivingEntity> nearbyEntities = playerIn.getWorld().getEntitiesByClass(LivingEntity.class, new Box(i - d0, j - d0, k - d0, i + d0, j + d0, k + d0),livingEntity -> true);

                if (playerIn.isSneaking()) {
                    targetData.chainData.clearChains();

                    for (LivingEntity livingEntity : nearbyEntities) {
                        EntityDataProvider.getCapability(livingEntity).ifPresent(nearbyData -> nearbyData.chainData.removeChain(target));
                    }

                    return;
                }

                AtomicBoolean flag = new AtomicBoolean(false);

                for (LivingEntity livingEntity : nearbyEntities) {
                    EntityDataProvider.getCapability(livingEntity).ifPresent(nearbyData -> {
                        if (nearbyData.chainData.isChainedTo(playerIn)) {
                            targetData.chainData.removeChain(playerIn);
                            nearbyData.chainData.removeChain(playerIn);
                            nearbyData.chainData.attachChain(target);

                            flag.set(true);
                        }
                    });
                }

                if (!flag.get()) {
                    targetData.chainData.attachChain(playerIn);
                }
            } else {
                targetData.chainData.attachChain(playerIn);
            }

            if (!playerIn.isCreative()) {
                stack.decrement(1);
            }
        });

        return ActionResult.SUCCESS;
    }

    @Override
    public @NotNull ActionResult useOnBlock(ItemUsageContext context) {
        Block block = context.getWorld().getBlockState(context.getBlockPos()).getBlock();

        if (!(block instanceof WallBlock)) {
            return ActionResult.PASS;
        } else {
            if (!context.getWorld().isClient) {
                attachToFence(context.getPlayer(), context.getWorld(), context.getBlockPos());
            }
            return ActionResult.SUCCESS;
        }
    }
}