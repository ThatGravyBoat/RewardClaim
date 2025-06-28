package tech.thatgravyboat.rewardclaim.ui.components

import earth.terrarium.olympus.client.components.base.BaseWidget
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext
import earth.terrarium.olympus.client.layouts.BaseLayout
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget

class LayoutWidgetRenderer<T : AbstractWidget>(
    val layout: BaseLayout<*>,
    val mouse: Boolean = true
) : WidgetRenderer<T> {

    override fun render(graphics: GuiGraphics, ctx: WidgetRendererContext<T>, partialTick: Float) {
        val mouseX = if (mouse) ctx.mouseX else -1
        val mouseY = if (mouse) ctx.mouseY else -1

        layout.withPosition(ctx.x, ctx.y)
        layout.build { widget ->
            widget.width = ctx.width
            widget.render(graphics, mouseX, mouseY, partialTick)
        }
    }

}

class RenderWidget(val renderer: WidgetRenderer<AbstractWidget>) : BaseWidget() {

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderer.render(graphics, WidgetRendererContext(this, mouseX, mouseY), partialTick)
    }
}

@Suppress("UNCHECKED_CAST")
fun WidgetRenderer<*>.asWidget(): RenderWidget {
    return RenderWidget(this as WidgetRenderer<AbstractWidget>)
}