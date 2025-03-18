package com.pizza573.cornucopia.client.screens.tooltip;

import com.pizza573.cornucopia.common.item.components.CornucopiaContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record CornucopiaTooltip(CornucopiaContents contents) implements TooltipComponent
{

}
