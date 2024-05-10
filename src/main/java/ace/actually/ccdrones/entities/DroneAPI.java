package ace.actually.ccdrones.entities;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.client.ComputerCraftAPIClient;
import dan200.computercraft.api.client.FabricComputerCraftAPIClient;
import dan200.computercraft.api.detail.BlockReference;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.filesystem.MountConstants;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.core.filesystem.FileSystemException;
import dan200.computercraft.core.filesystem.FileSystemWrapper;
import dan200.computercraft.core.util.PeripheralHelpers;
import dan200.computercraft.shared.computer.blocks.ComputerBlock;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.peripheral.modem.ModemPeripheral;
import dan200.computercraft.shared.turtle.upgrades.CraftingTablePeripheral;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DroneAPI implements ILuaAPI {
    DroneEntity drone;
    public DroneAPI(DroneEntity entity)
    {
        this.drone=entity;
    }

    @Override
    public String[] getNames() {
        return new String[] {"drone"};
    }

    @LuaFunction
    public final void debug()
    {
        System.out.println(drone.getOnPos().toShortString());
    }

    @LuaFunction
    public final void engineOn(boolean on)
    {
        drone.setEngineOn(on);
        if(!on)
        {
            drone.setDeltaMovement(Vec3.ZERO);
        }
    }
    @LuaFunction
    public final void hoverOn(boolean on)
    {
        drone.setNoGravity(on);
    }
    @LuaFunction
    public final void right(int deg) {
        drone.turn(deg,0);
    }
    @LuaFunction
    public final void left(int deg) {right(-deg);}

    @LuaFunction
    public final boolean isColliding() {return drone.horizontalCollision;}

    @LuaFunction
    public final void up(int amount)
    {
        drone.addDeltaMovement(Vec3.ZERO.add(0,amount/10D,0));
    }
    @LuaFunction
    public final void down(int amount) {up(-amount);}
    @LuaFunction
    public final MethodResult lookForward()
    {
        ClipContext context = new ClipContext(drone.getOnPos().getCenter(),drone.getOnPos().getCenter().add(drone.getForward().multiply(3,3,3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,drone);
        BlockHitResult result = drone.level().clip(context);

        //System.out.println(result.getBlockPos()+" "+drone.level().getBlockState(result.getBlockPos()));
        return MethodResult.of(VanillaDetailRegistries.BLOCK_IN_WORLD.getDetails(new BlockReference(drone.level(),result.getBlockPos())));
    }
    @LuaFunction
    public final float rotation() {return drone.yRotO;}
    @LuaFunction
    public final MethodResult lookBack()
    {
        ClipContext context = new ClipContext(drone.getOnPos().getCenter(),drone.getOnPos().getCenter().add(drone.getForward().multiply(-3,-3,-3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,drone);
        BlockHitResult result = drone.level().clip(context);

        //System.out.println(result.getBlockPos()+" "+drone.level().getBlockState(result.getBlockPos()));
        return MethodResult.of(VanillaDetailRegistries.BLOCK_IN_WORLD.getDetails(new BlockReference(drone.level(),result.getBlockPos())));
    }

    @LuaFunction
    public final void breakForward()
    {
        ClipContext context = new ClipContext(drone.getOnPos().getCenter(),drone.getOnPos().getCenter().add(drone.getForward().multiply(3,3,3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,drone);
        BlockHitResult result = drone.level().clip(context);

        drone.level().destroyBlock(result.getBlockPos(),true,drone);
    }

    @LuaFunction(mainThread = true)
    public final void pickupBlock()
    {
        drone.setCarrying(drone.getOnPos().below());
    }
    @LuaFunction(mainThread = true)
    public final void dropBlock()
    {
        drone.dropCarrying(drone.getOnPos().below());
    }


    public static void initDrive(ServerComputer computer)
    {

        try {
            //an example of writing files to the filesystem on a turtleCommand
            if(!computer.getAPIEnvironment().getFileSystem().exists("/startup/go.lua"))
            {


                FileSystemWrapper<SeekableByteChannel> file = computer.getAPIEnvironment().getFileSystem().openForWrite("/startup/go.lua", MountConstants.WRITE_OPTIONS);
                file.get().write(ByteBuffer.wrap("drone.debug()\ndrone.engineOn(true)\ndrone.hoverOn(true)\ndrone.left(20)\ndrone.up(16)\nwhile true do\n\tdrone.lookForward()\nend".getBytes(StandardCharsets.UTF_8)));
                file.close();
                computer.reboot();
            }

        } catch (FileSystemException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
