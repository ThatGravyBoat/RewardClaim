package tech.thatgravyboat.rewardclaim.ui

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.XConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.utils.withAlpha
import gg.essential.universal.ChatColor
import gg.essential.vigilance.gui.VigilancePalette
import tech.thatgravyboat.rewardclaim.MappedImageCache
import tech.thatgravyboat.rewardclaim.ExternalConfiguration
import tech.thatgravyboat.rewardclaim.types.RewardData
import tech.thatgravyboat.rewardclaim.types.RewardLanguage

class UISelectedReward(middle: XConstraint) : UIBlock(VigilancePalette.getHighlight().withAlpha(204)) {

    private var image: UIImage? = null

    private val displayName = UIText("Select a Reward!").constrain {
        x = 31.percent()
        y = 10.3.percent()
    } childOf this

    private val imageBackground = UIBlock(VigilancePalette.getBrightHighlight()).constrain {
        x = 5.percent()
        y = 10.3.percent()
        width = 20.5.percent()
        height = 42.6.percent()
    } childOf this

    private val rarity = UIText().constrain {
        x = 31.percent()
        y = 10.3.percent() + 13.pixel()
    } childOf this

    private val amount = UIText().constrain {
        x = 31.percent()
        y = 10.3.percent() + 25.pixel()
    } childOf this

    private val desc = UIWrappedText().constrain {
        x = 5.percent()
        y = 52.9.percent() + 3.pixel()
        width = 90.percent()
    } childOf this

    init {
        constrain {
            width = 35.percent()
            height = 30.percent()
            x = middle - 17.5.percent()
            y = 25.percent()
        }

        UIBlock(VigilancePalette.getDivider()).constrain {
            width = 64.5.percent()
            height = 1.pixel()
            x = 30.5.percent()
            y = 10.3.percent() + 11.pixel()
        } childOf this
    }

    fun updateInfo(data: RewardData, language: RewardLanguage) {
        displayName.setText(data.getDisplayName(language))
        enableEffect(OutlineEffect(data.rarity.color.color!!, 1F))
        rarity.setText("Rarity: ${data.rarity.color}${language.translate(data.rarity.translationKey)}")

        if (data.amount != null) amount.setText("Amount: ${ChatColor.GOLD}${data.amount}")
        else if (data.intlist != null) amount.setText("Boxes: ${ChatColor.GOLD}${data.intlist.size}")
        else amount.setText("")
        desc.setText(data.getDescription(language))

        data.image?.let {
            it.url?.let { url ->
                val imageType = ExternalConfiguration.getImageType(it.imageType)
                image?.let(imageBackground::removeChild)
                image = UIImage.ofURL(url, MappedImageCache).constrain {
                    width = imageType.width.percent()
                    height = imageType.height.percent()
                    if (imageType.center) {
                        x = CenterConstraint()
                        y = CenterConstraint()
                    }
                } childOf imageBackground
            }
        }

    }
}