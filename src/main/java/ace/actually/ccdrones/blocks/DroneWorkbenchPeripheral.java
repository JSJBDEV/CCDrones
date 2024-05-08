package ace.actually.ccdrones.blocks;

import ace.actually.ccdrones.entities.DroneAPI;
import ace.actually.ccdrones.entities.DroneEntity;
import dan200.computercraft.api.detail.BlockReference;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.filesystem.MountConstants;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.apis.TableHelper;
import dan200.computercraft.core.filesystem.FileSystemException;
import dan200.computercraft.core.filesystem.FileSystemWrapper;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDrivePeripheral;
import dan200.computercraft.shared.peripheral.modem.ModemPeripheral;
import dan200.computercraft.shared.turtle.apis.TurtleAPI;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DroneWorkbenchPeripheral implements IPeripheral {
    BlockEntity blockEntity;
    private int compId;

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

    @Override
    public void attach(IComputerAccess computer) {
        IPeripheral.super.attach(computer);
        compId= computer.getID();
    }

    /**
     * I have no idea if there is a better way of doing this.
     * From any computer with this peripheral attached you can run lua() -> a = peripheral.wrap(direction) -> a.api()
     * this will then reboot the computer, meaning drone functions can be accessed to the auto-complete in edit.
     * the computer will always have the api in the future, but running the program not on a drone will just not work
     */
    @LuaFunction
    public final void api()
    {
        if(blockEntity.hasLevel() && blockEntity.getLevel() instanceof ServerLevel level)
        {
            ServerComputer thisComputer = ServerContext.get(level.getServer()).registry().getComputers().stream().filter(a->a.getID()==compId).findFirst().get();
            thisComputer.addAPI(new DroneAPI(null));
            thisComputer.reboot();
        }
    }

    /**
     * copies a file from a given location to /startup/ on the nearest drone within cube centred around the peripheral
     * it then reboots the drone, running the file.
     *
     * @param path the location of a file to be ran when the nearest Drone boots up
     * @return nothing or a success message
     */
    @LuaFunction
    public final MethodResult export(String path)
    {
        if(blockEntity.hasLevel() && blockEntity.getLevel() instanceof ServerLevel level)
        {
            ServerComputer thisComputer = ServerContext.get(level.getServer()).registry().getComputers().stream().filter(a->a.getID()==compId).findFirst().get();
            int droneID = level.getEntitiesOfClass(DroneEntity.class,
                    new AABB(blockEntity.getBlockPos().offset(-2,-2,-2),
                            blockEntity.getBlockPos().offset(2,2,2)))
                    .get(0).getComputerID();
            ServerComputer droneComputer = ServerContext.get(level.getServer()).registry().getComputers().stream().filter(a->a.getID()==droneID).findFirst().get();

            try {
                //an example of writing files to the filesystem on a turtleCommand
                if(thisComputer.getAPIEnvironment().getFileSystem().exists(path))
                {


                    FileSystemWrapper<SeekableByteChannel> file = thisComputer.getAPIEnvironment().getFileSystem().openForRead(path);
                    FileSystemWrapper<SeekableByteChannel> droneFile = droneComputer.getAPIEnvironment().getFileSystem().openForWrite("/startup/go.lua",MountConstants.WRITE_OPTIONS);

                    ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.get().size());
                    file.get().read(byteBuffer);
                    String fileString = new String(byteBuffer.array());
                    System.out.println(fileString);

                    droneFile.get().write(ByteBuffer.wrap(fileString.getBytes(StandardCharsets.UTF_8)));
                    file.close();
                    droneFile.close();
                    droneComputer.reboot();
                    return MethodResult.of("Successfully copied data to drone, booting it up!");
                }

            } catch (FileSystemException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return MethodResult.of();
    }

    @LuaFunction
    public final MethodResult debug()
    {
        return MethodResult.of(VanillaDetailRegistries.BLOCK_IN_WORLD.getDetails(new BlockReference(blockEntity.getLevel(),blockEntity.getBlockPos().below())));
    }


    @LuaFunction
    public final MethodResult survey()
    {
        Map<String,Map<String,Object>> found = new HashMap<>();
        for (int i = -5; i < 5; i++) {
            for (int j = -5; j < 5; j++) {
                for (int k = -5; k < 5; k++) {
                    if(blockEntity.getLevel().getBlockState(blockEntity.getBlockPos().offset(i,j,k)).is(BlockTags.PARROTS_SPAWNABLE_ON))
                    {

                        found.put(i+","+j+","+k,VanillaDetailRegistries.BLOCK_IN_WORLD.getBasicDetails(new BlockReference(blockEntity.getLevel(),blockEntity.getBlockPos().offset(i,j,k))));
                    }
                }
            }
        }
        return MethodResult.of(found);
    }


}
