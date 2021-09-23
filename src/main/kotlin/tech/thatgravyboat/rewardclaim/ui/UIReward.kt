package tech.thatgravyboat.rewardclaim.ui

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.XConstraint
import gg.essential.elementa.constraints.YConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.utils.withAlpha
import gg.essential.universal.ChatColor
import gg.essential.vigilance.gui.VigilancePalette
import tech.thatgravyboat.rewardclaim.MappedImageCache
import tech.thatgravyboat.rewardclaim.RewardLanguage
import tech.thatgravyboat.rewardclaim.types.RewardData

class UIReward(xConstraint: XConstraint, yConstraint: YConstraint) : UIBlock(VigilancePalette.getHighlight().withAlpha(204)) {

    private val imageBackground : UIBlock
    private val title : UIText
    private val rarityDesc : UIText
    private val amountDesc : UIText

    init {
        constrain {
            width = 35.percent()
            height = 16.percent()
            x = xConstraint
            y = yConstraint
        }

        imageBackground = UIBlock(VigilancePalette.getBrightHighlight()).constrain {
            width = 20.57.percent()
            height = 80.percent()
            x = 2.545.percent()
            y = 10.percent()
        } childOf this

        UIBlock(VigilancePalette.getDivider()).constrain {
            width = 1.pixel()
            height = 95.percent()
            x = 25.66.percent()
            y = 2.5.percent()
        } childOf this

        val rightSideStart = (25.66.percent() + 2.pixel()) + 1.28.percent()

        UIBlock(VigilancePalette.getDivider()).constrain {
            width = 71.78.percent() - 1.pixel()
            height = 1.pixel()
            x = rightSideStart
            y = 26.percent()
        } childOf this

        title = UIText("Unknown Reward").constrain {
            x = rightSideStart
            y = 26.percent() - (getHeight() + 1).pixel()
        } childOf this

        rarityDesc = UIText("Rarity: Unknown").constrain {
            x = rightSideStart
            y = 26.percent() + 3.pixel()
        } childOf this

        amountDesc = UIText("Amount: ${ChatColor.GOLD}0").constrain {
            x = rightSideStart
            y = 26.percent() + (5+rarityDesc.getHeight()).pixel()
        } childOf this
        amountDesc.hide(true)
    }

    fun setSelected(selected : Boolean) {
        if (selected) {
            enableEffect(OutlineEffect(VigilancePalette.getAccent(), 1F))
        }else {
            removeEffect<OutlineEffect>()
        }
    }

    fun setData(data : RewardData, language : RewardLanguage) {
        title.setText(data.getDisplayName(language))
        rarityDesc.setText("Rarity: ${data.rarity.color}${language.translate(data.rarity.translationKey)}")

        data.amount?.let {
            amountDesc.setText("Amount: ${ChatColor.GOLD}$it")
            amountDesc.unhide(true)
        }

        data.boxes?.let {
            amountDesc.setText("Boxes: ${ChatColor.GOLD}$it")
            amountDesc.unhide(true)
        }

        data.image?.let {
            it.url?.let { url ->
                UIImage.ofURL(url, MappedImageCache).constrain {
                    width = 100.percent()
                    height = it.height.percent()
                } childOf imageBackground
            }
        }
    }
}