package com.pizza573.cornucopia.common.util;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ModParticleHelper
{
    public static void spawnPurpleAround(Level level, LivingEntity livingEntity)
    {
        ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.Y, level, livingEntity.blockPosition(), 1.2, ParticleTypes.DRAGON_BREATH, UniformInt.of(15, 20));
    }

    public static void spawnGreenAround(Level level, LivingEntity livingEntity)
    {
        ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.Y,level, livingEntity.blockPosition(), 1.2,ParticleTypes.HAPPY_VILLAGER, UniformInt.of(5,10));
    }
}
