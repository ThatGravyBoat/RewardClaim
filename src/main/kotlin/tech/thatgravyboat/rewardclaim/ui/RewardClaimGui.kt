package tech.thatgravyboat.rewardclaim.ui

import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.ChatColor
import gg.essential.universal.UDesktop
import gg.essential.vigilance.gui.VigilancePalette
import org.apache.commons.io.IOUtils
import tech.thatgravyboat.rewardclaim.Config
import tech.thatgravyboat.rewardclaim.ExternalConfiguration
import tech.thatgravyboat.rewardclaim.platform.Multithreading.runAsync
import tech.thatgravyboat.rewardclaim.platform.Multithreading.schedule
import tech.thatgravyboat.rewardclaim.types.WebData
import java.awt.Color
import java.net.*
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

private val BUTTON_HOVER = Color(0, 212, 105)

private val SECURITY_REGEX = Regex("window\\.securityToken = \"(?<token>.*)\";")
private val DATA_REGEX = Regex("window\\.appData = '(?<data>\\{.*})';")
private val I18N_REGEX = Regex("window.i18n = \\{(?<translations>.*)};", RegexOption.DOT_MATCHES_ALL)

class RewardClaimGui(private val id: String) : WindowScreen() {

    private var state: State = State.LOADING
    private var selected = -1

    // Raw Data
    private lateinit var data: WebData

    private val background = UIBlock(VigilancePalette.getBackground()).constrain {
        width = 100.percent()
        height = 100.percent()
    } childOf this.window

    private val selectedReward = UISelectedReward(30.percent() - 0.5.pixel()) childOf background

    init {
        UIBlock(VigilancePalette.getDivider()).constrain {
            width = 1.pixel()
            height = 75.percent()
            x = 50.percent() - 0.5.pixel()
            y = 12.5.percent()
        } childOf background

        UIBlock(VigilancePalette.getDivider()).constrain {
            width = 1.pixel()
            height = 75.percent()
            x = 10.percent() - 0.5.pixel()
            y = 12.5.percent()
        } childOf background

        UIBlock(VigilancePalette.getDivider()).constrain {
            width = 30.percent() - 1.pixel()
            height = 1.pixel()
            x = 15.percent()
            y = 70.percent()
        } childOf background

        UIText("Click to claim 1 of the 3 Rewards").apply {
            constrain {
                x = 30.percent() - 0.5.pixel() - (getTextWidth() / 2f).pixel()
                y = 15.percent()
            } childOf background
        }

        UIText("Reward Options").apply {
            constrain {
                x = 75.percent() - (getTextWidth() / 2f).pixel()
                y = 15.percent()
            } childOf background
        }

        UIText("§lClaim Reward", false).let { text ->
            UIBlock(VigilancePalette.getAccent()).let { button ->
                button.constrain {
                    width = text.getTextWidth().pixel() + 18.pixel()
                    height = 17.pixel()
                    y = 60.percent()
                    x = 30.percent() - 0.5.pixel() - (text.getTextWidth() / 2f + 9f).pixel()
                } childOf background

                text.constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                } childOf button

                button.onMouseEnter { setColor(BUTTON_HOVER) }
                button.onMouseLeave { setColor(VigilancePalette.getAccent()) }
                button.onMouseClick { event ->
                    if (event.mouseButton == 0 && selected != -1) {
                        if (Config.showConfirmation) confirmPopup()
                        else claimReward()
                    }
                    event.stopPropagation()
                }
            }
        }

        UIText("Daily Streak").apply {
            constrain {
                x = 30.percent() - 0.5.pixel() - (getTextWidth() / 2f).pixel()
                y = 72.percent()
            } childOf background
        }

        adPopup(false)
    }

    //region Daily Streak

    private val streakSubHeading = UIText(String.format("Current: %d Highest: %d", 0, 0)).apply {
        constrain {
            x = 30.percent() - 0.5.pixel() - (getTextWidth() / 2f).pixel()
            y = 77.percent()
        } childOf background
    }

    private val streaks = Array(9) { i ->
        UICircle(5f, VigilancePalette.getDivider(), 15).let {
            it.constrain {
                x = ((15.percent() + (15.percent() - 0.5.pixel())) - 45.pixel()) + (it.getWidth() * i).pixel()
                y = 83.percent()
            } childOf background
        }
    }

    //endregion

    private val rewards =
        Array(3) { i -> UIReward(57.5.percent(), 30.percent() + (18 * i).percent()) childOf background }.also {
            for ((i,reward) in it.withIndex()) {
                reward.onMouseClick { event ->
                    if (event.mouseButton == 0 && state == State.SUCCESSFUL) {
                        val wasSelected = i == selected
                        selected = i
                        it.forEach { rew ->
                            rew.setSelected(rew == reward)
                        }
                        selectedReward.updateInfo(data.rewards[selected], data.language)
                        if (event.clickCount >= 2 && wasSelected) {
                            if (Config.showDoubleClickConfirmation) confirmPopup()
                            else claimReward()
                        }
                    }
                }
                reward.hide(true)
            }
        }

    init {
        runAsync {
            try {
                val cookieManager = CookieManager()
                CookieHandler.setDefault(cookieManager)
                val url = URL("https://rewards.hypixel.net/claim-reward/$id")
                (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    useCaches = true
                    addRequestProperty("User-Agent", ExternalConfiguration.userAgent)
                    readTimeout = 15000
                    connectTimeout = 15000
                    doOutput = true
                    inputStream.use {
                        val html = IOUtils.toString(it, Charset.defaultCharset())
                        val securityMatcher = SECURITY_REGEX.find(html)
                        val dataMatcher = DATA_REGEX.find(html)
                        val i18nMatcher = I18N_REGEX.find(html)

                        if (securityMatcher != null && dataMatcher != null && i18nMatcher != null) {
                            data = WebData(securityMatcher, dataMatcher, i18nMatcher)

                            if (data.rewards.isEmpty()) {
                                state = State.FAILED_REWARDS
                                errorPopup("Rewards were empty.")
                            } else {
                                state = State.SUCCESSFUL
                                updateElements()
                            }

                            if (data.skippable || data.duration == 0) {
                                adPopup(true)
                            } else {
                                schedule({ adPopup(true) }, data.duration.toLong(), TimeUnit.SECONDS)
                            }
                        } else {
                            state = State.FAILED_REWARDS
                            errorPopup("Regex could not be found.\nUrl:${this.url}\nSecurity: ${securityMatcher != null}\nI18n: ${i18nMatcher != null}\nData: $${dataMatcher != null}")
                        }
                    }
                }
            } catch (e: Exception) {
                state = State.FAILED
                errorPopup("Error: " + e.message)
                e.printStackTrace()
            }
        }
        adPopup(false)
    }

    private fun updateElements() {
        data.let {
            for (i in 0 until data.streak.progress) streaks[i].setColor(VigilancePalette.getAccent())
            streakSubHeading.setText(String.format("Current: %d Highest: %d", data.streak.current, data.streak.highest))
            streakSubHeading.setX(30.percent() - 0.5.pixel() - (streakSubHeading.getTextWidth() / 2f).pixel())

            for (i in 0 until 3) {
                rewards[i].let {
                    it.setData(data.rewards[i], data.language)
                    it.unhide(true)
                }
            }
        }
    }

    private var popup: UIPopup? = null

    private fun errorPopup(error: String) {
        Window.enqueueRenderOperation {
            popup?.let {
                if (it.parent.hasParent) {
                    it.parent.removeChild(it)
                    popup = null
                }
            }
            popup = UIPopup(
                "${ChatColor.RED}An Error Occurred!", error,
                UIImage.ofResourceCached("/vigilance/cancel.png"),
                { restorePreviousScreen() },
                "${ChatColor.BOLD}Close",
                UIImage.ofResourceCached("/rewardclaim/external_link.png"),
                { UDesktop.browse(URI("https://rewards.hypixel.net/claim-reward/$id")) },
                "${ChatColor.BOLD}Reward"
            ) childOf this.window
        }
    }

    private fun confirmPopup() {
        popup = UIPopup(
            "Confirmation",
            "Are you sure you want to claim this reward. Click 'Back' if you would like to change which reward you would like to claim or 'Continue' if you like to go ahead with your reward.",
            null,
            { removePopup() },
            "${ChatColor.BOLD}Back",
            null,
            { claimReward() },
            "${ChatColor.BOLD}Continue"
        ) childOf this.window
    }

    private fun claimReward() {
        runAsync {
            try {
                (URL("https://rewards.hypixel.net/claim-reward/claim?option=$selected&id=$id&activeAd=${data.activeAd}&_csrf=${data.securityToken}&watchedFallback=false").openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    useCaches = true
                    addRequestProperty("User-Agent", ExternalConfiguration.userAgent)
                    readTimeout = 15000
                    connectTimeout = 15000
                    responseCode
                    CookieManager.setDefault(null)
                    restorePreviousScreen()
                }
            } catch (e: Exception) {
                errorPopup("Error: " + e.message)
                e.printStackTrace()
            }
        }
    }

    private fun removePopup() {
        Window.enqueueRenderOperation {
            popup?.let {
                if (it.parent.hasParent) {
                    it.parent.removeChild(it)
                    popup = null
                }
            }
        }
    }

    private fun adPopup(skip: Boolean) {
        Window.enqueueRenderOperation {
            popup?.let {
                if (it.parent.hasParent) {
                    it.parent.removeChild(it)
                    popup = null
                }
            }
            popup = if (skip) {
                UIPopup(
                    "Reward Claim AD",
                    "Hypixel is a great server and as such we don't want to remove their ad for their store. You can click the skip button when it appears to remove this message and claim your reward.",
                    UIImage.ofResourceCached("/rewardclaim/external_link.png"),
                    { UDesktop.browse(getAd()) },
                    "${ChatColor.BOLD}Store",
                    null,
                    { removePopup() },
                    "${ChatColor.BOLD}Skip"
                ) childOf this.window
            } else {
                UIPopup(
                    "Reward Claim AD",
                    "Hypixel is a great server and as such we don't want to remove their ad for their store. You can click the skip button when it appears to remove this message and claim your reward.",
                    UIImage.ofResourceCached("/rewardclaim/external_link.png"),
                    { UDesktop.browse(getAd()) },
                    "${ChatColor.BOLD}Store"
                ) childOf this.window
            }
        }
    }

    private fun getAd(): URI {
        return if (this::data.isInitialized) URI(data.adLink) else URI("https://store.hypixel.net/?utm_source=rewards-video&utm_medium=website&utm_content=TRsCiBNYY7M&utm_campaign=Rewardss")
    }

    override fun onDrawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.window.draw()
    }

    private enum class State {
        LOADING,
        FAILED,
        SUCCESSFUL,
        FAILED_REWARDS
    }
}
