package com.pizza573.cornucopia.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

// Define an example data-bearing record.
public record Increment(int value)
{
    public static final Codec<Increment> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.INT.fieldOf("value").forGetter(Increment::value))
                    .apply(instance, Increment::new)
    );

    public int add(int x)
    {
        return value() + x;
    }
}