package ace.actually.ccdrones.items;

import ace.actually.ccdrones.entities.DroneEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DroneUpgradeItem extends Item {

    private final String upgrade;
    public DroneUpgradeItem(String upgrade) {
        super(new Properties());
        this.upgrade=upgrade;
    }

    public String getUpgrade() {
        return upgrade;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if(itemStack.getItem() instanceof  DroneUpgradeItem droneUpgradeItem && !player.level().isClientSide && player.isCrouching())
        {
            if(livingEntity instanceof DroneEntity drone)
            {
                drone.addUpgrade(droneUpgradeItem.getUpgrade());
            }
        }
        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
