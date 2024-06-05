package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.inventory.ContainerPodium;
import com.github.alexthe666.iceandfire.item.ItemDragonEgg;
import com.github.alexthe666.iceandfire.item.ItemMyrmexEgg;
import com.github.alexthe666.iceandfire.message.MessageUpdatePodium;
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
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;

public class TileEntityPodium extends LockableContainerBlockEntity implements SidedInventory {

    private static final int[] slotsTop = new int[]{0};
    public int ticksExisted;
    public int prevTicksExisted;
    IItemHandler handlerUp = new SidedInvWrapper(this, Direction.UP);
    IItemHandler handlerDown = new SidedInvWrapper(this, Direction.DOWN);
    net.minecraftforge.common.util.LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper
        .create(this, Direction.UP, Direction.DOWN);
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public TileEntityPodium(BlockPos pos, BlockState state) {
        super(IafTileEntityRegistry.PODIUM.get(), pos, state);
    }

    //TODO: This must be easier to do
    public static void tick(World level, BlockPos pos, BlockState state, TileEntityPodium entityPodium) {
        entityPodium.prevTicksExisted = entityPodium.ticksExisted;
        entityPodium.ticksExisted++;
    }

    @Override
    public net.minecraft.util.math.Box getRenderBoundingBox() {
        return new net.minecraft.util.math.Box(this.pos, this.pos.add(1, 3, 1));
    }

    @Override
    public int size() {
        return this.stacks.size();
    }

    @Override
    public @NotNull ItemStack getStack(int index) {
        return this.stacks.get(index);
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

                if (this.stacks.get(index).isEmpty()) {
                    this.stacks.set(index, ItemStack.EMPTY);
                }

                return itemstack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    public ItemStack getStackInSlotOnClosing(int index) {
        if (!this.stacks.get(index).isEmpty()) {
            ItemStack itemstack = this.stacks.get(index);
            this.stacks.set(index, itemstack);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setStack(int index, @NotNull ItemStack stack) {
        this.stacks.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.writeNbt(this.toInitialChunkDataNbt());
        if (!this.world.isClient) {
            IceAndFire.sendMSGToAll(new MessageUpdatePodium(this.getPos().asLong(), this.stacks.get(0)));
        }
    }

    @Override
    public void readNbt(@NotNull NbtCompound compound) {
        super.readNbt(compound);
        this.stacks = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(compound, this.stacks);
    }

    @Override
    public void writeNbt(@NotNull NbtCompound compound) {
        Inventories.writeNbt(compound, this.stacks);
    }

    @Override
    public void onOpen(@NotNull PlayerEntity player) {
    }

    @Override
    public void onClose(@NotNull PlayerEntity player) {
    }

    @Override
    public boolean canInsert(int index, @NotNull ItemStack stack, Direction direction) {
        return index != 0 || (stack.getItem() instanceof ItemDragonEgg || stack.getItem() instanceof ItemMyrmexEgg);
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
    public int @NotNull [] getAvailableSlots(@NotNull Direction side) {
        return slotsTop;
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
    public boolean isValid(int index, @NotNull ItemStack stack) {
        return false;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void onDataPacket(ClientConnection net, BlockEntityUpdateS2CPacket packet) {
        this.readNbt(packet.getNbt());
    }

    @Override
    public @NotNull NbtCompound toInitialChunkDataNbt() {
        return this.createNbtWithIdentifyingData();
    }

    @Override
    public @NotNull ItemStack removeStack(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull Text getDisplayName() {
        return this.getContainerName();
    }

    @Override
    protected @NotNull Text getContainerName() {
        return Text.translatable("block.iceandfire.podium");
    }

    @Override
    protected @NotNull ScreenHandler createScreenHandler(int id, @NotNull PlayerInventory player) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.size(); i++) {
            if (!this.getStack(i).isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public <T> net.minecraftforge.common.util.@NotNull LazyOptional<T> getCapability(
        net.minecraftforge.common.capabilities.@NotNull Capability<T> capability, Direction facing) {
        if (!this.removed && facing != null
            && capability == ForgeCapabilities.ITEM_HANDLER) {
            if (facing == Direction.DOWN)
                return this.handlers[1].cast();
            else
                return this.handlers[0].cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public ScreenHandler createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity player) {
        return new ContainerPodium(id, this, playerInventory, new ArrayPropertyDelegate(0));
    }
}