package com.iafenvoy.iceandfire.block;

import com.iafenvoy.iceandfire.block.util.IDragonProof;
import com.iafenvoy.iceandfire.enums.EnumDragonEgg;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.world.BlockView;

import java.util.List;

public class BlockDragonScales extends Block implements IDragonProof {
    final EnumDragonEgg type;

    public BlockDragonScales(EnumDragonEgg type) {
        super(Settings.create().mapColor(MapColor.STONE_GRAY).instrument(Instrument.BASEDRUM).dynamicBounds().strength(30F, 500).sounds(BlockSoundGroup.STONE).requiresTool());
        this.type = type;
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("dragon." + this.type.toString().toLowerCase()).formatted(this.type.color));
    }
}