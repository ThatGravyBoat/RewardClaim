package tech.thatgravyboat.rewardclaim.ui

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.layouts.Layouts
import earth.terrarium.olympus.client.layouts.LinearViewLayout
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.network.chat.Component
import tech.thatgravyboat.rewardclaim.Utils.withVerticalLayout
import tech.thatgravyboat.rewardclaim.data.DataManager
import tech.thatgravyboat.rewardclaim.data.Reward
import tech.thatgravyboat.rewardclaim.data.RewardState
import tech.thatgravyboat.rewardclaim.mc
import tech.thatgravyboat.rewardclaim.ui.components.CircleWidgetRenderer
import tech.thatgravyboat.rewardclaim.ui.components.RewardButtonWidgetRenderer
import tech.thatgravyboat.rewardclaim.ui.components.RewardWidgetRenderer
import tech.thatgravyboat.rewardclaim.ui.components.asWidget

class RewardClaimScreen(private val state: RewardState) : BaseCursorScreen(Component.empty()) {

    private val selected = State.empty<Reward>()

    private fun getLeftColumn(): Layout {
        val left = LinearLayout.vertical().spacing(10)


        left.addChild(Widgets.text("Select 1 of the 3 rewards") {
            it.withCenterAlignment()
            it.withSize(this.width / 3, 20)
            it.withColor(MinecraftColors.WHITE)
        })

        left.addChild(Widgets.frame {
            it.withTexture(UIConstants.MODAL_INSET)
            it.withContentFillWidth()
            it.withSize(this.width / 3, 120)
            it.withVerticalLayout(5, 5)
            it.withContents { content ->
                val selected = selected.get() ?: return@withContents
                content.addChild(RewardWidgetRenderer<AbstractWidget>(selected, state.language).asWidget().withSize(50, 50))
                content.addChild(Widgets.textarea(selected.getDisplayDescription(state.language), this.width / 3 - 10).textAlignLeft().alignLeft())
            }
        })

        left.addChild(Widgets.button {
            it.withTexture(UIConstants.PRIMARY_BUTTON)
            it.withSize(100, 20)
            it.active = selected.get() != null
            it.withRenderer(WidgetRenderers.text<Button>(Component.literal("Claim Reward")).withColor(MinecraftColors.WHITE))
            it.withCallback {
                val selected = selected.get() ?: return@withCallback
                DataManager.claim(state, selected).handle { _, exception ->
                    mc.schedule {
                        if (exception != null) {
                            RewardClaimScreens.open(exception, "claim the reward")
                        } else {
                            mc.screen?.onClose()
                        }
                    }
                }
            }
        }, LayoutSettings.defaults().alignHorizontallyCenter())

        val streakInfo = LinearLayout(this.width / 3, 40, LinearLayout.Orientation.VERTICAL).spacing(5)

        streakInfo.addChild(
            Widgets.text("Daily Streak").withColor(MinecraftColors.WHITE),
            LayoutSettings.defaults().alignHorizontallyCenter()
        )
        streakInfo.addChild(
            Widgets.text("Current: ${state.data.dailyStreak.score} Highest: ${state.data.dailyStreak.highScore}").withColor(MinecraftColors.WHITE),
            LayoutSettings.defaults().alignHorizontallyCenter()
        )

        val streak = (0 until 9)
            .map { if (it < state.data.dailyStreak.value) Color(0x3C8527) else MinecraftColors.GRAY }
            .map { it.withAlpha(0xFF) }
            .map { CircleWidgetRenderer<AbstractWidget>(it).asWidget().withSize(10, 10) }
            .fold(Layouts.row().withGap(3), LinearViewLayout::withChild)

        streakInfo.addChild(streak, LayoutSettings.defaults().alignHorizontallyCenter())

        left.addChild(streakInfo, LayoutSettings.defaults().alignHorizontallyCenter())

        return left
    }

    private fun getRightColumn(): Layout {
        val right = LinearLayout.vertical().spacing(10)

        right.addChild(Widgets.text("Reward Options") {
            it.withCenterAlignment()
            it.withSize(this.width / 3, 20)
            it.withColor(MinecraftColors.WHITE)
        })

        for (reward in state.data.rewards) {
            right.addChild(Widgets.button {
                it.withTexture(null)
                it.withRenderer(RewardButtonWidgetRenderer<Button>(reward, state.language, selected))
                it.withSize(180, 60)
                it.withCallback {
                    selected.set(reward)
                    this.rebuildWidgets()
                }
            }, LayoutSettings.defaults().alignHorizontallyCenter())
        }

        return right
    }

    override fun init() {
        val layout = Layouts.row().withChildren(getLeftColumn(), getRightColumn()).withGap(50).withPosition(0, 0)
        layout.arrangeElements()
        FrameLayout.centerInRectangle(layout, 0, 0, this.width, this.height)
        layout.visitWidgets(this::addRenderableWidget)
    }

    override fun onClose() {
        RewardClaimScreens.close()
    }
}