package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.util.IAFMath;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class MyrmexAIFindMate<T extends EntityMyrmexBase> extends TrackTargetGoal {
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super Entity> targetEntitySelector;
    public EntityMyrmexRoyal myrmex;
    protected EntityMyrmexBase targetEntity;

    private List<Entity> list = IAFMath.emptyEntityList;

    public MyrmexAIFindMate(EntityMyrmexRoyal myrmex) {
        super(myrmex, false, false);
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(myrmex);
        this.targetEntitySelector = new Predicate<Entity>() {
            @Override
            public boolean test(Entity myrmex) {
                return myrmex instanceof EntityMyrmexRoyal && ((EntityMyrmexRoyal) myrmex).getGrowthStage() >= 2;
            }
        };
        this.myrmex = myrmex;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.myrmex.shouldHaveNormalAI()) {
            this.list = IAFMath.emptyEntityList;
            return false;
        }
        if (!this.myrmex.canMove() || this.myrmex.getTarget() != null || this.myrmex.releaseTicks < 400 || this.myrmex.mate != null) {
            this.list = IAFMath.emptyEntityList;
            return false;
        }
        MyrmexHive village = this.myrmex.getHive();
        if (village == null) {
            village = MyrmexWorldData.get(this.myrmex.getWorld()).getNearestHive(this.myrmex.getBlockPos(), 100);
        }
        if (village != null && village.getCenter().getSquaredDistanceFromCenter(this.myrmex.getX(), village.getCenter().getY(), this.myrmex.getZ()) < 2000) {
            this.list = IAFMath.emptyEntityList;
            return false;
        }

        if (this.myrmex.getWorld().getTime() % 4 == 0) // only update the list every 4 ticks
            this.list = this.mob.getWorld().getOtherEntities(this.myrmex, this.getTargetableArea(100), this.targetEntitySelector);

        if (this.list.isEmpty())
            return false;

        this.list.sort(this.theNearestAttackableTargetSorter);
        for (Entity royal : this.list) {
            if (this.myrmex.canBreedWith((EntityMyrmexRoyal) royal)) {
                this.myrmex.mate = (EntityMyrmexRoyal) royal;
                this.myrmex.getWorld().sendEntityStatus(this.myrmex, (byte) 76);
                return true;
            }
        }
        return false;
    }

    protected Box getTargetableArea(double targetDistance) {
        return this.mob.getBoundingBox().expand(targetDistance, targetDistance / 2, targetDistance);
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    public static class Sorter implements Comparator<Entity> {
        private final Entity theEntity;

        public Sorter(EntityMyrmexBase theEntityIn) {
            this.theEntity = theEntityIn;
        }

        @Override
        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            final double d0 = this.theEntity.squaredDistanceTo(p_compare_1_);
            final double d1 = this.theEntity.squaredDistanceTo(p_compare_2_);
            return Double.compare(d0, d1);
        }
    }
}