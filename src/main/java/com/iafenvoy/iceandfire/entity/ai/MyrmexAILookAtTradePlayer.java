package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntityMyrmexBase;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;

public class MyrmexAILookAtTradePlayer extends LookAtEntityGoal {
    private final EntityMyrmexBase myrmex;

    public MyrmexAILookAtTradePlayer(EntityMyrmexBase myrmex) {
        super(myrmex, PlayerEntity.class, 8.0F);
        this.myrmex = myrmex;
    }

    @Override
    public boolean canStart() {
        if (this.myrmex.hasCustomer() && this.myrmex.getHive() != null) {
            assert this.myrmex.getCustomer() != null;
            if (!this.myrmex.getHive().isPlayerReputationTooLowToTrade(this.myrmex.getCustomer().getUuid())) {
                this.target = this.myrmex.getCustomer();
                return true;
            }
        }
        return false;
    }
}
