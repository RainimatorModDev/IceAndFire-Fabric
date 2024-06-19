package com.iafenvoy.iceandfire.event;

import com.iafenvoy.iceandfire.client.particle.CockatriceBeamRender;
import com.iafenvoy.iceandfire.client.render.block.RenderFrozenState;
import com.iafenvoy.iceandfire.client.render.entity.RenderChain;
import com.iafenvoy.iceandfire.data.EntityDataComponent;
import com.iafenvoy.iceandfire.entity.util.ICustomMoveController;
import com.iafenvoy.iceandfire.message.MessageDragonControl;
import com.iafenvoy.iceandfire.network.IafClientNetworkHandler;
import com.iafenvoy.iceandfire.registry.IafKeybindings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
public class ClientEvents {
//    public static boolean onCameraSetup(CameraSetupCallback.CameraInfo info) {
//        PlayerEntity player = MinecraftClient.getInstance().player;
//        if (player.getVehicle() != null) {
//            if (player.getVehicle() instanceof EntityDragonBase) {
//                int currentView = IceAndFire.PROXY.getDragon3rdPersonView();
//                float scale = ((EntityDragonBase) player.getVehicle()).getRenderSize() / 3;
//                if (MinecraftClient.getInstance().options.getPerspective() == Perspective.THIRD_PERSON_BACK ||
//                        MinecraftClient.getInstance().options.getPerspective() == Perspective.THIRD_PERSON_FRONT) {
//                    if (currentView == 1) {
//                        info.camera.move(-info.camera.getMaxZoom(scale * 1.2F), 0F, 0);
//                    } else if (currentView == 2) {
//                        info.camera.move(-info.camera.getMaxZoom(scale * 3F), 0F, 0);
//                    } else if (currentView == 3) {
//                        info.camera.move(-info.camera.getMaxZoom(scale * 5F), 0F, 0);
//                    }
//                }
//            }
//        }
//    }

    public static void onLivingUpdate(LivingEntity entity) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (entity instanceof ICustomMoveController moveController) {
            if (entity.getVehicle() != null && entity.getVehicle() == mc.player) {
                byte previousState = moveController.getControlState();
                moveController.dismount(mc.options.sneakKey.isPressed());
                byte controlState = moveController.getControlState();
                if (controlState != previousState) {
                    IafClientNetworkHandler.send(new MessageDragonControl(entity.getId(), controlState, entity.getX(), entity.getY(), entity.getZ()));
                }
            }
        }
        if (entity instanceof PlayerEntity player && player.getWorld().isClient && player.getVehicle() instanceof ICustomMoveController) {
            Entity vehicle = player.getVehicle();
            ICustomMoveController moveController = (Entity & ICustomMoveController) player.getVehicle();
            byte previousState = moveController.getControlState();
            moveController.up(mc.options.jumpKey.isPressed());
            moveController.down(IafKeybindings.dragon_down.isPressed());
            moveController.attack(IafKeybindings.dragon_strike.isPressed());
            moveController.dismount(mc.options.sneakKey.isPressed());
            moveController.strike(IafKeybindings.dragon_fireAttack.isPressed());
            byte controlState = moveController.getControlState();
            if (controlState != previousState)
                IafClientNetworkHandler.send(new MessageDragonControl(vehicle.getId(), controlState, vehicle.getX(), vehicle.getY(), vehicle.getZ()));
        }
    }

    public static void onPostRenderLiving(LivingEntity entity, float partialRenderTick, MatrixStack matrixStack, VertexConsumerProvider buffers, int light) {
        EntityDataComponent data = EntityDataComponent.ENTITY_DATA_COMPONENT.get(entity);
        for (LivingEntity target : data.miscData.getTargetedByScepter())
            CockatriceBeamRender.render(entity, target, matrixStack, buffers, partialRenderTick);
        if (data.frozenData.isFrozen)
            RenderFrozenState.render(entity, matrixStack, buffers, light, data.frozenData.frozenTicks);
        RenderChain.render(entity, partialRenderTick, matrixStack, buffers, light, data.chainData.getChainedTo());
    }

//    @SubscribeEvent
//    public void onEntityMount(EntityMountEvent event) {
//        if (event.getEntityBeingMounted() instanceof EntityDragonBase dragon && event.getLevel().isClientSide && event.getEntityMounting() == MinecraftClient.getInstance().player) {
//            if (dragon.isTamed() && dragon.isOwner(MinecraftClient.getInstance().player)) {
//                if (this.AUTO_ADAPT_3RD_PERSON) {
//                    // Auto adjust 3rd person camera's according to dragon's size
//                    IceAndFire.PROXY.setDragon3rdPersonView(2);
//                }
//                if (IafConfig.dragonAuto3rdPerson) {
//                    if (event.isDismounting()) {
//                        MinecraftClient.getInstance().options.setPerspective(Perspective.values()[IceAndFire.PROXY.getPreviousViewType()]);
//                    } else {
//                        IceAndFire.PROXY.setPreviousViewType(MinecraftClient.getInstance().options.getPerspective().ordinal());
//                        MinecraftClient.getInstance().options.setPerspective(Perspective.values()[1]);
//                    }
//                }
//            }
//        }
//    }
}