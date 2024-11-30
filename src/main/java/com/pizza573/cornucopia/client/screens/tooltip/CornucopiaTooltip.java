package com.pizza573.cornucopia.client.screens.tooltip;

import com.pizza573.cornucopia.item.components.CornucopiaContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class CornucopiaTooltip implements TooltipComponent
{
    CornucopiaContents contents;

    public CornucopiaTooltip(CornucopiaContents contents) {
        this.contents = contents;
    }

    public CornucopiaContents contents() {
        return this.contents;
    }
}
