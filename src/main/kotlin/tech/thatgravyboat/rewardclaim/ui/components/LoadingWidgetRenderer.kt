package tech.thatgravyboat.rewardclaim.ui.components

import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.util.ARGB
import tech.thatgravyboat.rewardclaim.Utils.drawRoundedRec
import tech.thatgravyboat.rewardclaim.Utils.pushPop
import kotlin.math.cos
import kotlin.math.sin

private const val SPEED = 4
private const val SIZE = 5

class LoadingWidgetRenderer<T : AbstractWidget> : WidgetRenderer<T> {

    private var frames = 0L

    override fun render(graphics: GuiGraphics, ctx: WidgetRendererContext<T>, partialTick: Float) {
        frames++

        graphics.pushPop {
            translate(ctx.x.toFloat(), ctx.y.toFloat(), 0f)

            val time = (frames % 360) * SPEED

            for (degree in 0 until 360 step 30) {

                val x = ctx.width / 2f + cos(Math.toRadians(degree.toDouble())) * ctx.width / 2.5f
                val y = ctx.height / 2f + sin(Math.toRadians(degree.toDouble())) * ctx.height / 2.5f

                val alpha = (sin(Math.toRadians(degree.toDouble() + time.toDouble() * -1)) * 128 + 128).toInt()

                graphics.drawRoundedRec(
                    x = (x - SIZE / 2f).toInt(),
                    y = (y - SIZE / 2f).toInt(),
                    width = SIZE,
                    height = SIZE,
                    backgroundColor = ARGB.color(255.coerceAtMost(alpha), 0x80FFFFFF.toInt()),
                    radius = 60,
                )
            }
        }
    }

}