package com.elytradev.movingworld.common.experiments.mixin;

import com.elytradev.movingworld.common.experiments.interact.ContainerWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityPlayerMP.class)
public class MixinEntityPlayerMP {

    @Inject(method = "Lnet/minecraft/entity/player/EntityPlayerMP;onUpdate()V", at = @At(value = "INVOKE"))
    public void onUpdateHook(CallbackInfo cbi) {
        if (getThis().openContainer != getThis().inventoryContainer
                && !(getThis().openContainer instanceof ContainerWrapper)) {
            getThis().openContainer = new ContainerWrapper(getThis().openContainer);
        }
    }

    public EntityPlayer getThis() {
        return ((EntityPlayer) (Object) this);
    }

}