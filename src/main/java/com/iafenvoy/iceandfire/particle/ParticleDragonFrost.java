package com.iafenvoy.iceandfire.particle;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ParticleDragonFrost extends SpriteBillboardParticle {
    private static final Identifier SNOWFLAKE = new Identifier(IceAndFire.MOD_ID, "textures/particles/snowflake_0.png");
    private static final Identifier SNOWFLAKE_BIG = new Identifier(IceAndFire.MOD_ID, "textures/particles/snowflake_1.png");
    private final float dragonSize;
    private final double initialX;
    private final double initialY;
    private final double initialZ;
    private final float speedBonus;
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private EntityDragonBase dragon;

    public ParticleDragonFrost(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, SpriteProvider provider) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.maxAge = 30;
        this.initialX = this.x = xCoordIn;
        this.initialY = this.y = yCoordIn;
        this.initialZ = this.z = zCoordIn;
        this.dragonSize = 1;
        this.targetX = xCoordIn + (double) ((this.random.nextFloat() - this.random.nextFloat()) * 1.75F * this.dragonSize);
        this.targetY = yCoordIn + (double) ((this.random.nextFloat() - this.random.nextFloat()) * 1.75F * this.dragonSize);
        this.targetZ = zCoordIn + (double) ((this.random.nextFloat() - this.random.nextFloat()) * 1.75F * this.dragonSize);
        this.setPos(this.x, this.y, this.z);
        this.setVelocity(xSpeedIn, ySpeedIn, zSpeedIn);
        this.speedBonus = this.random.nextFloat() * 0.015F;
        boolean big = this.random.nextBoolean();
        this.setSprite(provider);
    }

//    public ParticleDragonFrost(ClientWorld world, double x, double y, double z, double motX, double motY, double motZ, EntityDragonBase entityDragonBase, int startingAge) {
//        this(world, x, y, z, motX, motY, motZ, MathHelper.clamp(entityDragonBase.getRenderSize() * 0.08F, 0.55F, 3F));
//        this.dragon = entityDragonBase;
//        this.targetX = this.dragon.burnParticleX + (double) ((this.random.nextFloat() - this.random.nextFloat())) * 3.5F;
//        this.targetY = this.dragon.burnParticleY + (double) ((this.random.nextFloat() - this.random.nextFloat())) * 3.5F;
//        this.targetZ = this.dragon.burnParticleZ + (double) ((this.random.nextFloat() - this.random.nextFloat())) * 3.5F;
//        this.x = x;
//        this.y = y;
//        this.z = z;
//        this.age = startingAge;
//    }


//    @Override
//    public void buildGeometry(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
//        Vec3d inerp = renderInfo.getPos();
//        if (this.age > this.getMaxAge()) {
//            this.markDead();
//        }
//
//        Vec3d Vector3d = renderInfo.getPos();
//        float f = (float) (MathHelper.lerp(partialTicks, this.prevPosX, this.x) - Vector3d.getX());
//        float f1 = (float) (MathHelper.lerp(partialTicks, this.prevPosY, this.y) - Vector3d.getY());
//        float f2 = (float) (MathHelper.lerp(partialTicks, this.prevPosZ, this.z) - Vector3d.getZ());
//        Quaternionf quaternion;
//        if (this.angle == 0.0F) {
//            quaternion = renderInfo.getRotation();
//        } else {
//            quaternion = new Quaternionf(renderInfo.getRotation());
//            float f3 = MathHelper.lerp(partialTicks, this.prevAngle, this.angle);
//            quaternion.mul(RotationAxis.POSITIVE_Z.rotation(f3));
//        }
//
//        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
//        vector3f1 = quaternion.transform(vector3f1);
//        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
//        float f4 = this.getSize(partialTicks);
//
//        for (int i = 0; i < 4; ++i) {
//            Vector3f vector3f = avector3f[i];
//            vector3f = quaternion.transform(vector3f);
//            vector3f.mul(f4);
//            vector3f.add(f, f1, f2);
//        }
//        float f7 = 0;
//        float f8 = 1;
//        float f5 = 0;
//        float f6 = 1;
//        RenderSystem.setShaderTexture(0, this.big ? SNOWFLAKE_BIG : SNOWFLAKE);
//        int j = this.getBrightness(partialTicks);
//        Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder vertexbuffer = tessellator.getBuffer();
//        vertexbuffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
//        vertexbuffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).texture(f8, f6).color(this.red, this.green, this.blue, this.alpha).light(j).next();
//        vertexbuffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).texture(f8, f5).color(this.red, this.green, this.blue, this.alpha).light(j).next();
//        vertexbuffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).texture(f7, f5).color(this.red, this.green, this.blue, this.alpha).light(j).next();
//        vertexbuffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).texture(f7, f6).color(this.red, this.green, this.blue, this.alpha).light(j).next();
//        Tessellator.getInstance().draw();
//    }

    @Override
    public int getMaxAge() {
        return this.dragon == null ? 10 : 30;
    }

    @Override
    public int getBrightness(float partialTick) {
        float f = 0;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        int i = super.getBrightness(partialTick);
        int j = i & 255;
        int k = i >> 16 & 255;
        j = j + (int) (f * 15.0F * 16.0F);

        if (j > 240) j = 240;
        return j | k << 16;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.dragon == null) {
            float distX = (float) (this.initialX - this.x);
            float distZ = (float) (this.initialZ - this.z);
            this.velocityX += distX * -0.01F * this.dragonSize * this.random.nextFloat();
            this.velocityZ += distZ * -0.01F * this.dragonSize * this.random.nextFloat();
            this.velocityY += 0.015F * this.random.nextFloat();
        } else {
            double d2 = this.targetX - this.initialX;
            double d3 = this.targetY - this.initialY;
            double d4 = this.targetZ - this.initialZ;
            float speed = 0.015F + this.speedBonus;
            this.velocityX += d2 * speed;
            this.velocityY += d3 * speed;
            this.velocityZ += d4 * speed;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    public static class Provider implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteSet;

        public Provider(SpriteProvider spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(DefaultParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleDragonFrost(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}