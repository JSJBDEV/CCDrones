package ace.actually.ccdrones;

import ace.actually.ccdrones.blocks.DroneWorkbenchBlock;
import ace.actually.ccdrones.blocks.DroneWorkbenchBlockEntity;
import ace.actually.ccdrones.blocks.DroneWorkbenchPeripheral;
import ace.actually.ccdrones.entities.DroneEntity;
import ace.actually.ccdrones.items.DroneItem;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCDrones implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ccdrones");

    public static final TagKey<Block> SURVEYABLE = TagKey.create(Registries.BLOCK, new ResourceLocation("ccdrones", "surveyable"));
    @Override
    public void onInitialize() {

        registerBlocks();
        registerEntities();
        registerPeripherals();
        registerItems();

    }

    public static final DroneWorkbenchBlock DRONE_WORKBENCH_BLOCK = new DroneWorkbenchBlock(BlockBehaviour.Properties.of());

    private void registerBlocks()
    {
        Registry.register(BuiltInRegistries.BLOCK,new ResourceLocation("ccdrones","drone_workbench"),DRONE_WORKBENCH_BLOCK);
    }

    public static final DroneItem DRONE_ITEM = new DroneItem(new Item.Properties());
    private void registerItems()
    {
        Registry.register(BuiltInRegistries.ITEM,new ResourceLocation("ccdrones","drone_workbench"),new BlockItem(DRONE_WORKBENCH_BLOCK,new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM,new ResourceLocation("ccdrones","drone_item"),DRONE_ITEM);
    }

    private void registerEntities()
    {
        FabricDefaultAttributeRegistry.register(DRONE, DroneEntity.createMobAttributes());
    }

    private void registerPeripherals()
    {
        PeripheralLookup.get().registerForBlockEntity((a,b)->new DroneWorkbenchPeripheral(a),DRONE_WORKBENCH_BE);
    }


    public static final EntityType<DroneEntity> DRONE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation("ccdrones", "drone"),
            FabricEntityTypeBuilder.create(MobCategory.MISC,DroneEntity::new).dimensions(EntityDimensions.fixed(0.5f,0.5f)).build()
    );

    public static BlockEntityType<DroneWorkbenchBlockEntity> DRONE_WORKBENCH_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation("ccdrones", "drone_workbench_block_entity"),
            FabricBlockEntityTypeBuilder.create(DroneWorkbenchBlockEntity::new, DRONE_WORKBENCH_BLOCK).build()
    );
}
