package tech.thatgravyboat.rewardclaim

import com.mojang.blaze3d.vertex.PoseStack
import com.teamresourceful.resourcefullib.common.utils.Scheduling
import earth.terrarium.olympus.client.components.compound.LayoutWidget
import earth.terrarium.olympus.client.pipelines.RoundedRectangle
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

val mc: Minecraft get() = Minecraft.getInstance()

fun schedule(duration: Duration, block: () -> Unit) {
    Scheduling.schedule({
        mc.schedule { block() }
    }, duration.inWholeMilliseconds, TimeUnit.MILLISECONDS)
}

object Utils {

    inline fun GuiGraphics.pushPop(block: PoseStack.() -> Unit) {
        this.pose().pushPose()
        block(this.pose())
        this.pose().popPose()
    }

    fun GuiGraphics.drawRoundedRec(
        x: Int, y: Int, width: Int, height: Int,
        backgroundColor: Int, borderColor: Int = backgroundColor,
        borderSize: Int = 0, radius: Int = 0,
    ) {
        this.flush()

        val xOffset = this.pose().last().pose().m30()
        val yOffset = this.pose().last().pose().m31()
        pushPop {
            translate(-xOffset, -yOffset, 0f)
            RoundedRectangle.draw(
                this@drawRoundedRec, (x + xOffset).toInt(), (y + yOffset).toInt(), width, height,
                backgroundColor, borderColor, width.coerceAtMost(height) * (radius / 100f), borderSize,
            )
        }
    }

    fun LayoutWidget<*>.withVerticalLayout(margin: Int = 0, gap: Int = 0) {
        this.withContentMargin(margin)
        this.withLayoutCallback { widget, layout ->
            var y = widget.y
            layout.visitWidgets {
                it.y = y
                it.x = widget.x + margin
                y += it.height + gap
            }
        }
    }
}