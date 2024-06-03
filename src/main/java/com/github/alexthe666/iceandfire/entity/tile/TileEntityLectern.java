package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import com.github.alexthe666.iceandfire.inventory.ContainerLectern;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemBestiary;
import com.github.alexthe666.iceandfire.message.MessageUpdateLectern;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TileEntityLectern extends LockableContainerBlockEntity implements SidedInventory {
    private static final int[] slotsTop = new int[]{0};
    private static final int[] slotsSides = new int[]{1};
    private static final int[] slotsBottom = new int[]{0};
    private static final Random RANDOM = new Random();
    private static final ArrayList<EnumBestiaryPages> EMPTY_LIST = new ArrayList<>();
    public final PropertyDelegate furnaceData = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return 0;
        }

        @Override
        public void set(int index, int value) {

        }

        @Override
        public int size() {
            return 0;
        }
    };
    public float pageFlip;
    public float pageFlipPrev;
    public float pageHelp1;
    public float pageHelp2;
    public EnumBestiaryPages[] selectedPages = new EnumBestiaryPages[3];
    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN);
    private final Random localRand = new Random();
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(3, ItemStack.EMPTY);

    public TileEntityLectern(BlockPos pos, BlockState state) {
        super(IafTileEntityRegistry.IAF_LECTERN.get(), pos, state);
    }

    public static void bookAnimationTick(World p_155504_, BlockPos p_155505_, BlockState p_155506_, TileEntityLectern p_155507_) {
        float f1 = p_155507_.pageHelp1;
        do {
            p_155507_.pageHelp1 += RANDOM.nextInt(4) - RANDOM.nextInt(4);
        } while (f1 == p_155507_.pageHelp1);
        p_155507_.pageFlipPrev = p_155507_.pageFlip;
        float f = (p_155507_.pageHelp1 - p_155507_.pageFlip) * 0.04F;
        float f3 = 0.02F;
        f = MathHelper.clamp(f, -f3, f3);
        p_155507_.pageHelp2 += (f - p_155507_.pageHelp2) * 0.9F;
        p_155507_.pageFlip += p_155507_.pageHelp2;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public @NotNull ItemStack getStack(int index) {
        return this.stacks.get(index);
    }

    private List<EnumBestiaryPages> getPossiblePages() {
        final List<EnumBestiaryPages> list = EnumBestiaryPages.possiblePages(this.stacks.get(0));
        if (list != null && !list.isEmpty()) {
            return list;
        }
        return EMPTY_LIST;
    }

    @Override
    public @NotNull ItemStack removeStack(int index, int count) {
        if (!this.stacks.get(index).isEmpty()) {
            ItemStack itemstack;

            if (this.stacks.get(index).getCount() <= count) {
                itemstack = this.stacks.get(index);
                this.stacks.set(index, ItemStack.EMPTY);
                return itemstack;
            } else {
                itemstack = this.stacks.get(index).split(count);

                if (this.stacks.get(index).getCount() == 0) {
                    this.stacks.set(index, ItemStack.EMPTY);
                }

                return itemstack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setStack(int index, ItemStack stack) {
        boolean isSame = !stack.isEmpty() && ItemStack.areItemsEqual(stack, this.stacks.get(index)) && ItemStack.areEqual(stack, this.stacks.get(index));
        this.stacks.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        if (!isSame) {
            this.markDirty();

            if (/* Manuscripts */ this.stacks.get(1).isEmpty()) {
                selectedPages[0] = null;
                selectedPages[1] = null;
                selectedPages[2] = null;
                IceAndFire.sendMSGToAll(new MessageUpdateLectern(pos.asLong(), -1, -1, -1, false, 0));
            } else {
                selectedPages = randomizePages(getStack(0), getStack(1));
            }
        }
    }

    public EnumBestiaryPages[] randomizePages(ItemStack bestiary, ItemStack manuscript) {
        if (!world.isClient) {
            if (bestiary.getItem() == IafItemRegistry.BESTIARY.get()) {
                List<EnumBestiaryPages> possibleList = getPossiblePages();
                localRand.setSeed(this.world.getTime());
                Collections.shuffle(possibleList, localRand);
                if (!possibleList.isEmpty()) {
                    selectedPages[0] = possibleList.get(0);
                } else {
                    selectedPages[0] = null;
                }
                if (possibleList.size() > 1) {
                    selectedPages[1] = possibleList.get(1);
                } else {
                    selectedPages[1] = null;
                }
                if (possibleList.size() > 2) {
                    selectedPages[2] = possibleList.get(2);
                } else {
                    selectedPages[2] = null;
                }
            }
            int page1 = selectedPages[0] == null ? -1 : selectedPages[0].ordinal();
            int page2 = selectedPages[1] == null ? -1 : selectedPages[1].ordinal();
            int page3 = selectedPages[2] == null ? -1 : selectedPages[2].ordinal();
            IceAndFire.sendMSGToAll(new MessageUpdateLectern(pos.asLong(), page1, page2, page3, false, 0));
        }
        return selectedPages;
    }

    @Override
    public void readNbt(@NotNull NbtCompound compound) {
        super.readNbt(compound);
        this.stacks = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(compound, this.stacks);

    }

    @Override
    public void writeNbt(@NotNull NbtCompound compound) {
        super.writeNbt(compound);
        Inventories.writeNbt(compound, this.stacks);
    }

    @Override
    public void onOpen(@NotNull PlayerEntity player) {
    }

    @Override
    public void onClose(@NotNull PlayerEntity player) {
    }

    @Override
    public boolean isValid(int index, ItemStack stack) {
        if (stack.isEmpty())
            return false;
        if (index == 0)
            return stack.getItem() instanceof ItemBestiary;
        if (index == 1)
            return stack.getItem() == IafItemRegistry.MANUSCRIPT.get();
        return false;
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public boolean canPlayerUse(@NotNull PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stacks.clear();
    }

    @Override
    public @NotNull Text getName() {
        return Text.translatable("block.iceandfire.lectern");
    }

    @Override
    public boolean canExtract(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return false;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int @NotNull [] getAvailableSlots(@NotNull Direction side) {
        return side == Direction.DOWN ? slotsBottom : (side == Direction.UP ? slotsTop : slotsSides);
    }

    @Override
    public boolean canInsert(int index, @NotNull ItemStack itemStackIn, Direction direction) {
        return this.isValid(index, itemStackIn);
    }

    @Override
    public @NotNull ItemStack removeStack(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void onDataPacket(ClientConnection net, BlockEntityUpdateS2CPacket packet) {
        readNbt(packet.getNbt());
    }

    @Override
    public @NotNull NbtCompound toInitialChunkDataNbt() {
        return this.createNbtWithIdentifyingData();
    }

    @Override
    protected @NotNull Text getContainerName() {
        return getName();
    }

    @Override
    protected @NotNull ScreenHandler createScreenHandler(int id, @NotNull PlayerInventory player) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.stacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public <T> net.minecraftforge.common.util.@NotNull LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.@NotNull Capability<T> capability, Direction facing) {
        if (!this.removed && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            if (facing == Direction.DOWN)
                return handlers[1].cast();
            else
                return handlers[0].cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public ScreenHandler createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity player) {
        return new ContainerLectern(id, this, playerInventory, furnaceData);
    }


}