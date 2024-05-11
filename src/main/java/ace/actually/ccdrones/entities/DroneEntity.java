package ace.actually.ccdrones.entities;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.util.IDAssigner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.stream.Stream;

public class DroneEntity extends Mob {

    private boolean shouldMakeBoot = false;

    public static final EntityDataAccessor<CompoundTag> EXTRA = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public DroneEntity(EntityType<DroneEntity> e,Level level) {
        super(e, level);

    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide)
        {

            ServerComputer computer = createOrUpkeepComputer();
            computer.keepAlive();
            if(tickCount>5 && shouldMakeBoot)
            {
                DroneAPI.initDrive(computer);
                shouldMakeBoot=false;
            }


        }
        if(engineOn())
        {
            setDeltaMovement(getForward().multiply(0.1,0.1,0.1));

        }



    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put("extra",entityData.get(EXTRA));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        CompoundTag a = new CompoundTag();
        entityData.define(EXTRA,a.copy());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        entityData.set(EXTRA,compoundTag.getCompound("extra"));
    }

    public void setComputerID(int computerID) {
        CompoundTag tag = entityData.get(EXTRA);
        tag.putInt("computerID",computerID);
        entityData.set(EXTRA,tag);

    }

    public void setCarrying(BlockPos pos) {
        CompoundTag tag = entityData.get(EXTRA);

        System.out.println("picking up!");
        BlockState state = level().getBlockState(pos);
        BlockEntity entity = level().getBlockEntity(pos);

        CompoundTag stateTag = NbtUtils.writeBlockState(state);
        tag.put("carryingState",stateTag);

        if(entity!=null)
        {
            CompoundTag entityTag = entity.saveWithFullMetadata();
            tag.put("carryingEntity",entityTag);
            Clearable.tryClear(entity);
        }


        entityData.set(EXTRA,tag,true);
        level().setBlock(pos,Blocks.AIR.defaultBlockState(),2);
    }

    public void dropCarrying(BlockPos pos)
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("carryingState"))
        {
            System.out.println("dropping!");
            BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(),tag.getCompound("carryingState"));
            level().setBlock(pos,state,2);
            if(tag.contains("carryingEntity"))
            {
                BlockEntity entity = BlockEntity.loadStatic(pos,state,tag.getCompound("carryingEntity"));
                level().setBlockEntity(entity);
                tag.remove("carryingEntity");
            }
            tag.remove("carryingState");
            entityData.set(EXTRA,tag,true);
        }



    }

    public boolean isCarryingBlock()
    {
        CompoundTag tag = entityData.get(EXTRA);
        return tag.contains("carryingState");
    }

    public void setComputerUUID(UUID computerUUID) {
        CompoundTag tag = entityData.get(EXTRA);
        tag.putUUID("computerUUID",computerUUID);
        entityData.set(EXTRA,tag);
    }

    public void setEngineOn(boolean on)
    {
        CompoundTag tag = entityData.get(EXTRA);
        tag.putBoolean("engineOn",on);
        entityData.set(EXTRA,tag);
    }
    public boolean engineOn()
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("engineOn"))
        {
            return tag.getBoolean("engineOn");
        }
        return false;
    }

    public int getComputerID()
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("computerID"))
        {
            return tag.getInt("computerID");
        }
        return -1;
    }
    public UUID getComputerUUID()
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("computerUUID"))
        {
            return tag.getUUID("computerUUID");
        }
       return null;
    }


    private ServerComputer createOrUpkeepComputer()
    {
        ServerContext context = ServerContext.get(this.getServer());
        ServerComputer computer = context.registry().get(getComputerUUID());
        if (computer == null) {
            if (getComputerID() < 0) {
                System.out.println("defining ID");
                setComputerID(ComputerCraftAPI.createUniqueNumberedSaveDir(this.getServer(), IDAssigner.COMPUTER));
            }

            computer = new ServerComputer(
                    (ServerLevel) this.level(), this.getOnPos(), getComputerID(), null,
                    ComputerFamily.NORMAL, Config.computerTermWidth, Config.computerTermHeight
            );

            System.out.println("Computer ID: "+computer.getID());
            setComputerUUID(computer.register());

            DroneAPI api = new DroneAPI(this);
            computer.addAPI(api);

            //shouldMakeBoot=true;

            computer.turnOn();


        }

        return computer;

    }

    @Override
    public double getPassengersRidingOffset() {
        return -2;
    }
}
