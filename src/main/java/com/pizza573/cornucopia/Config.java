package com.pizza573.cornucopia;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config
{
    // ModConfigSpec
    public static final ModConfigSpec CONFIG_SPEC;// config_specialization
    // config values
    public static final Common COMMON;

    static {
        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        CONFIG_SPEC = specPair.getRight();
    }

    public static class Common
    {
        public final ModConfigSpec.BooleanValue enableClearFoods;
        public final ModConfigSpec.IntValue lifeThresholdValue;

        public Common(ModConfigSpec.Builder builder)
        {
            enableClearFoods = builder
//                    .comment("转换为丰饶角后清空食物")
                    .translation("cornucopia.config.enable_clear_foods")
                    .define("enableClearFoods", false);
            lifeThresholdValue=builder
//                    .comment("生命值低于多少时自动转换")
                    .translation("cornucopia.config.life_threshold_value")
                    .defineInRange("lifeThresholdValue", 10, 1, 19);
        }
    }
}
