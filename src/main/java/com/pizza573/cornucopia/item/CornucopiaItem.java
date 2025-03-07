package com.pizza573.cornucopia.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CornucopiaItem extends Item
{
    // static 变量，所有对象共享同一个静态字段值，示例：计数器、配置常量等。
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);
    private static final int ITEM_NAME_COLOR = Mth.color(0.133f, 0.545f, 0.133f);// 暗绿色
    private static final int REDUCE_TIME = 6; // 0.3s * 20tick/s = 6tick
    private int suitableFoodIndex;
    private ItemStack suitableFood = ItemStack.EMPTY;

    public CornucopiaItem(Properties properties)
    {
        super(properties);
    }


    @Override
    public @NotNull Component getName(@NotNull ItemStack stack)
    {
        return Component.translatable(super.getName(stack).getString()).withStyle(style -> style.withColor(ITEM_NAME_COLOR));
    }

    // 不能被放入背包之类的容器物品，不影响部分容器实体，如：chest
    public boolean canFitInsideContainerItems()
    {
        return false;
    }
}
