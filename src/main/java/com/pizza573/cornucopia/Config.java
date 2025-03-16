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
        public final ModConfigSpec.BooleanValue enableContentBar;
        public final ModConfigSpec.IntValue lifeThresholdValue;

        public Common(ModConfigSpec.Builder builder)
        {
            enableClearFoods = builder
//                    .comment("转换为丰饶角后清空食物")
                    .translation("config.cornucopia.enable_clear_foods")
                    .define("enableClearFoods", false);
            enableContentBar=builder
//                    .comment("显示食物内容条")
                    .translation("config.cornucopia.enable_content_bar")
                    .define("enableContentBar", false);
            lifeThresholdValue=builder
//                    .comment("生命值低于多少时自动转换")
                    .translation("config.cornucopia.life_threshold_value")
                    .defineInRange("lifeThresholdValue", 10, 1, 19);
        }
    }
}
