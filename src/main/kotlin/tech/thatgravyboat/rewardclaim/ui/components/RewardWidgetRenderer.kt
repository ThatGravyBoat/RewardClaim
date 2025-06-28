package tech.thatgravyboat.rewardclaim.ui.components

import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.images.BuiltinImageProviders
import earth.terrarium.olympus.client.layouts.Layouts
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.util.ARGB
import tech.thatgravyboat.rewardclaim.data.Language
import tech.thatgravyboat.rewardclaim.data.Reward
import tech.thatgravyboat.rewardclaim.remote.DEFAULT_IMAGE_TYPE
import tech.thatgravyboat.rewardclaim.remote.RemoteData

private const val IMAGE_SIZE = 50f

fun <T : AbstractWidget> RewardWidgetRenderer(
    reward: Reward,
    language: Language
): WidgetRenderer<T> {

    val image = reward.image

    val text = Layouts.column()
        .withChild(Widgets.text(reward.getDisplayName(language)) {
            it.withShadow()
            it.withLeftAlignment()
            it.withColor(MinecraftColors.WHITE)
        })
        .withChild(RenderWidget { graphics, widget, _ ->
            graphics.fill(
                widget.x,
                widget.y + 1,
                widget.x + widget.width,
                widget.y + 2,
                ARGB.opaque(MinecraftColors.GRAY.value)
            )
        }.withSize(0, 4))
        .withChild(Widgets.text(Component.literal("Rarity: ").append(reward.rarity.getDisplayName(language))) {
            it.withShadow()
            it.withLeftAlignment()
            it.withColor(MinecraftColors.WHITE)
        })
        .withChild(Widgets.text("Amount: §6${reward.amount}") {
            it.withShadow()
            it.withLeftAlignment()
            it.withColor(MinecraftColors.WHITE)
        })

    return WidgetRenderers.layered<T>(
        WidgetRenderer<T> { graphics, ctx, _ ->
            val image = image ?: return@WidgetRenderer
            val type = RemoteData.get().imageTypes[image.imageType] ?: DEFAULT_IMAGE_TYPE
            val texture = BuiltinImageProviders.URL.get(image.url)

            graphics.flush()
            graphics.drawSpecial { buffer ->
                val consumer = buffer.getBuffer(RenderType.guiTextured(texture))
                val pose = graphics.pose().last().pose()

                val maxV = if (type.center) 1f else type.width.toFloat() / type.height.toFloat()

                consumer.addVertex(pose, ctx.left.toFloat(), ctx.top.toFloat(), 0f).setUv(0f, 0f).setColor(-1)
                consumer.addVertex(pose, ctx.left.toFloat(), ctx.top.toFloat() + IMAGE_SIZE, 0f).setUv(0f, maxV).setColor(-1)
                consumer.addVertex(pose, ctx.left.toFloat() + IMAGE_SIZE, ctx.top.toFloat() + IMAGE_SIZE, 0f).setUv(1f, maxV).setColor(-1)
                consumer.addVertex(pose, ctx.left.toFloat() + IMAGE_SIZE, ctx.top.toFloat(), 0f).setUv(1f, 0f).setColor(-1)
            }
        }.withPadding(5),
        LayoutWidgetRenderer<T>(text, mouse = false).withPadding(5, 5, 5, IMAGE_SIZE.toInt() + 10),
    )
}

fun <T : AbstractWidget> RewardButtonWidgetRenderer(
    reward: Reward,
    language: Language,
    state: State<Reward>
): WidgetRenderer<T> {

    return WidgetRenderers.layered<T>(
        WidgetRenderer<T> { graphics, ctx, _ ->
            val texture = if (state.get() == reward) UIConstants.PRIMARY_BUTTON else UIConstants.DARK_BUTTON

            graphics.blitSprite(
                RenderType::guiTextured, texture.get(ctx.widget.active, ctx.widget.isHoveredOrFocused),
                ctx.x, ctx.y, ctx.width, ctx.height
            )
        },
        RewardWidgetRenderer<T>(reward, language)
    )
}