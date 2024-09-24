package net.telemort.ae2cellpartitioner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CellPartitionerBlock extends Block implements EntityBlock {
    public CellPartitionerBlock(Block.Properties properties) {
        super(properties);
    }
    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new CellPartitionerBlockEntity(pos, state);
    }
}
