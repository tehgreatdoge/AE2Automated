package net.telemort.ae2cellpartitioner;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import appeng.api.storage.StorageCells;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;;

public class CellPartitionerBlockEntity extends BlockEntity {
    public final String INVENTORY_TAG = "Inventory";
    protected ItemStackHandler inventory;
    private LazyOptional<IItemHandler> inventoryHandlerLazyOptional = LazyOptional.of(() -> this.inventory);
    
    public CellPartitionerBlockEntity(BlockPos pos, BlockState state) {
        super(AE2Automated.PARTITIONER_BLOCK_ENTITY_TYPE.get(), pos, state);
        var e = this;
        inventory = new ItemStackHandler(1) {
            @Override
            public void deserializeNBT(CompoundTag nbt) {
                super.deserializeNBT(nbt);
                this.setSize(1);
            }
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return slot == 0 && StorageCells.getCellInventory(stack, null) != null;
            }
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (stack.isEmpty())
                    return ItemStack.EMPTY;
                if (StorageCells.getCellInventory(stack, null) != null) {
                    var result = super.insertItem(slot, stack, simulate);
                    if (!simulate) {
                        ItemStack toConvert = this.stacks.get(slot);
                        var nbt = toConvert.getTag();
                        if (nbt == null)
                            return result;
                        if (!nbt.contains("keys", Tag.TAG_LIST)) {
                            return result;
                        }
                        ListTag partitions = new ListTag();
                        ListTag contents = nbt.getList("keys", Tag.TAG_COMPOUND);
                        contents.forEach((Tag tag) -> {
                            if (tag instanceof CompoundTag item) {
                                if (!item.contains("#c", Tag.TAG_STRING)) {
                                    return;
                                }
                                if (!item.contains("id", Tag.TAG_STRING)) {
                                    return;
                                }
                                CompoundTag newTag = new CompoundTag();
                                newTag.putString("#c", item.getString("#c"));
                                newTag.putString("id", item.getString("id"));
                                newTag.putLong("#", 0L);
                                partitions.add(newTag);
                            }
                        });
                        nbt.put("list", partitions);
                    }
                    return result;
                }
                return stack;
            }
            @Override
            protected void onContentsChanged(int slot) {
                e.setChanged();
                super.onContentsChanged(slot);
            }
        };
    }
    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        tag.put(INVENTORY_TAG, inventory.serializeNBT());
        super.saveAdditional(tag);
    }
    @Override
    public void load(@Nonnull CompoundTag tag) {
        if (tag.contains(INVENTORY_TAG, Tag.TAG_COMPOUND)) {
            inventory.deserializeNBT(tag.getCompound(INVENTORY_TAG));
        }
        super.load(tag);
    }
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryHandlerLazyOptional.invalidate();
    }
}
