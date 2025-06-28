package tech.thatgravyboat.rewardclaim.ui

import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.components.string.MultilineTextWidget
import earth.terrarium.olympus.client.ui.modals.Modals
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import tech.thatgravyboat.rewardclaim.data.Ad
import tech.thatgravyboat.rewardclaim.data.DataManager
import tech.thatgravyboat.rewardclaim.data.RewardState
import tech.thatgravyboat.rewardclaim.mc
import tech.thatgravyboat.rewardclaim.schedule
import tech.thatgravyboat.rewardclaim.ui.components.LoadingWidgetRenderer
import tech.thatgravyboat.rewardclaim.ui.components.asWidget
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URI
import kotlin.time.Duration.Companion.seconds

object RewardClaimScreens {

    private var openTime = 0L

    val isOpening: Boolean @JvmStatic get() = System.currentTimeMillis() - openTime < 5000L // 5 seconds

    fun close() {
        openTime = 0L
        mc.setScreen(null)
    }

    fun open(id: String) {
        openTime = System.currentTimeMillis()

        DataManager.get(id).handle { state, exception ->
            mc.schedule {
                if (mc.screen !is RewardClaimModal) return@schedule

                if (exception != null) {
                    open(exception, "open the reward claim screen")
                } else if (state != null) {
                    open(state)
                } else {
                    open(RuntimeException("Reward state is null for id: $id"), "open the reward claim screen")
                }
            }
        }

        Modals.action()
            .withTitle(Component.literal("Reward Claim"))
            .withContent(LoadingWidgetRenderer<AbstractWidget>().withCentered(50, 50).asWidget().withSize(75))
            .openWithoutBackground()
    }

    fun open(state: RewardState) {
        val data = state.data
        if (data.skippable) {
            mc.setScreen(RewardClaimScreen(state))
        } else {
            schedule((data.ad?.duration ?: 30).seconds) {
                if (mc.screen !is RewardClaimModal) return@schedule
                open(data.ad, true) {
                    mc.setScreen(RewardClaimScreen(state))
                }
            }
            open(data.ad, false)
        }
    }

    fun open(exception: Throwable, action: String) {
        Modals.action()
            .withTitle(Component.literal("An error occurred"))
            .withMinWidth(300)
            .withContent(Component.literal("""
                An error occurred while trying to $action.
                Please try again later or copy the error and paste in discord if it persists.
                
                Error: ${exception.message ?: "Unknown error"}
            """.trimIndent()))
            .withAction(Widgets.button()
                .withRenderer(WidgetRenderers.text(Component.literal("Close")))
                .withSize(80, 24)
                .withCallback(RewardClaimScreens::close)
            )
            .withAction(Widgets.button()
                .withRenderer(WidgetRenderers.text(Component.literal("Copy Error")))
                .withSize(80, 24)
                .withCallback {
                    val writer = StringWriter()
                    exception.printStackTrace(PrintWriter(writer))
                    mc.keyboardHandler.clipboard = writer.toString()
                    close()
                }
            )
            .openWithoutBackground()
    }

    fun open(ad: Ad?, canClick: Boolean, action: () -> Unit = {}) {
        val text = Component.empty()
            .append("As part of the original hypixel reward claim site.")
            .append("There is an ad that must be watched before you can claim your reward.")
            .append(CommonComponents.NEW_LINE)
            .append("As a substitute for the ad, you must wait ${ad?.duration ?: 30} seconds before you can claim your reward.")
            .append(CommonComponents.NEW_LINE)
            .append("You can view the Hypixel Reward Claim site at ")
            .append(Component.literal("store.hypixel.net").withStyle {
                var style = it
                style = style.withClickEvent(ClickEvent.OpenUrl(URI.create(ad?.link ?: "https://store.hypixel.net")))
                style = style.withColor(ChatFormatting.BLUE)
                style = style.withUnderlined(true)

                style
            })

        val modal = Modals.action()
        modal.withTitle(Component.literal("Reward Claim"))
        modal.withContent { width -> MultilineTextWidget(text, width)
            .clickActionCallback { style -> mc.screen?.handleComponentClicked(style) }
        }

        modal.withAction(Widgets.button()
            .withRenderer(WidgetRenderers.text(Component.literal("Close")))
            .withSize(80, 24)
            .withCallback(RewardClaimScreens::close)
        )
        modal.withAction(Widgets.button {
            it.withRenderer(WidgetRenderers.text(Component.literal("Skip")))
            it.withSize(80, 24)
            it.active = canClick
            it.withCallback(action)
        })

        modal.openWithoutBackground()
    }
}