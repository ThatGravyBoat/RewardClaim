package tech.thatgravyboat.rewardclaim.ui.components

import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import tech.thatgravyboat.rewardclaim.Utils.drawRoundedRec

class CircleWidgetRenderer<T : AbstractWidget>(val color: Color) : WidgetRenderer<T> {

    override fun render(graphics: GuiGraphics, ctx: WidgetRendererContext<T>, partialTick: Float) {
        graphics.drawRoundedRec(
            x = ctx.x,
            y = ctx.y,
            width = ctx.width,
            height = ctx.height,
            backgroundColor = color.value,
            radius = 50,
        )
    }

}