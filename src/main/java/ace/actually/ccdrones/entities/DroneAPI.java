package ace.actually.ccdrones.entities;

import dan200.computercraft.api.filesystem.MountConstants;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.core.filesystem.FileSystemException;
import dan200.computercraft.core.filesystem.FileSystemWrapper;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;

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


    public static void initDrive(ServerComputer computer)
    {

        try {
            //an example of writing files to the filesystem on a turtleCommand
            if(!computer.getAPIEnvironment().getFileSystem().exists("/startup/go.lua"))
            {

                FileSystemWrapper<SeekableByteChannel> file = computer.getAPIEnvironment().getFileSystem().openForWrite("/startup/go.lua", MountConstants.WRITE_OPTIONS);
                file.get().write(ByteBuffer.wrap("drone.debug()\ndrone.engineOn(true)\ndrone.hoverOn(true)\ndrone.left(20)\ndrone.up(1)".getBytes(StandardCharsets.UTF_8)));
                file.close();
                computer.reboot();
            }

        } catch (FileSystemException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
