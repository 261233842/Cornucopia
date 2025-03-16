package com.pizza573.cornucopia.init;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.item.components.CornucopiaContents;
import com.pizza573.cornucopia.item.CornucopiaItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems
{
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(Cornucopia.MOD_ID);
    // 物品在注册的时候添加了一个数据组件
    public static final DeferredItem<Item> CORNUCOPIA = REGISTER.register("cornucopia", () -> new CornucopiaItem(new Item
            .Properties()
            .stacksTo(1)
            .component(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY))
    );
    public static final DeferredItem<Item> CREATIVE_TAB_DISPLAY = REGISTER.register("creative_tab_display", () -> new Item(new Item.Properties()));
}
