package ace.actually.ccdrones.entities;

import ace.actually.ccdrones.ClientInit;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DroneEntityRenderer extends MobRenderer<DroneEntity,DroneEntityModel> {
    public DroneEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DroneEntityModel(context.bakeLayer(ClientInit.MODEL_DRONE_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(DroneEntity entity) {
        return new ResourceLocation("ccdrones","entity/drone.png");
    }
}
