package ace.actually.ccdrones.blocks;

import ace.actually.ccdrones.entities.DroneAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDrivePeripheral;
import dan200.computercraft.shared.peripheral.modem.ModemPeripheral;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DroneWorkbenchPeripheral implements IPeripheral {
    BlockEntity blockEntity;

    public DroneWorkbenchPeripheral(BlockEntity entity)
    {
        this.blockEntity=entity;
    }

    @Override
    public String getType() {

        return "droneworkbench";
    }

    @Override
    public boolean equals(IPeripheral other) {
        return other instanceof DroneWorkbenchBlock;
    }

    @LuaFunction
    public final void debug(){}
    @LuaFunction
    public final void engineOn(boolean on) {}
    @LuaFunction
    public final void hoverOn(boolean on) {}
    @LuaFunction
    public final void left(int deg) {}
    @LuaFunction
    public final void right(int deg) {left(-deg);}
    @LuaFunction
    public final boolean isColliding() {return false;}
    @LuaFunction
    public final void up(int amount) {}
    @LuaFunction
    public final void down(int amount) {up(-amount);}


}
