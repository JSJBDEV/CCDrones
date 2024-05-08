package ace.actually.ccdrones.blocks;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DroneWorkbenchBlock extends BaseEntityBlock {
    public DroneWorkbenchBlock(Properties properties) {
        super(properties);

    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DroneWorkbenchBlockEntity(blockPos,blockState);
    }
}
