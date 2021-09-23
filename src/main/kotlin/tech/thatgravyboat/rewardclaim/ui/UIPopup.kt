package tech.thatgravyboat.rewardclaim.ui

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.events.UIClickEvent
import gg.essential.elementa.utils.withAlpha
import gg.essential.vigilance.gui.VigilancePalette
import gg.essential.vigilance.gui.VigilancePalette.getBackground

class UIPopup private constructor(title : String, text : String) : UIBlock(getBackground().withAlpha(200)) {

    private val box: UIBlock

    constructor(title : String, text : String, image : UIImage? = null, event : (UIComponent.(event: UIClickEvent) -> Unit), buttonText : String
    ) : this(title, text) {

        val btnText = UIText(buttonText, false)
        val width = btnText.getTextWidth() + 18 + if (image != null) 18 else 0

        UIButton(image, width, CenterConstraint(), btnText, Alignment.MIDDLE).onMouseClick(event) childOf box
    }

    constructor(title : String, text : String,
                image1 : UIImage? = null, event1 : (UIComponent.(event: UIClickEvent) -> Unit), buttonText : String,
                image2 : UIImage? = null, event2 : (UIComponent.(event: UIClickEvent) -> Unit), buttonText2 : String
    ) : this(title, text) {

        val btn1Text = UIText(buttonText, false)
        val btn1Width = btn1Text.getTextWidth() + 18 + if (image1 != null) 18 else 0
        val btn2Text = UIText(buttonText2, false)
        val btn2Width = btn2Text.getTextWidth() + 18 + if (image2 != null) 18 else 0

        val width = btn1Width.coerceAtLeast(btn2Width)

        UIButton(image1, width, CenterConstraint() - 5.percent(), btn1Text, Alignment.RIGHT).onMouseClick(event1) childOf box
        UIButton(image2, width, CenterConstraint() + 5.percent(), btn2Text, Alignment.LEFT).onMouseClick(event2) childOf box
    }

    init {
        constrain {
            width = 100.percent()
            height = 100.percent()
        }

        box = UIBlock(VigilancePalette.getHighlight()).constrain {
            width = 50.percent()
            height = 45.percent()
            x = CenterConstraint()
            y = CenterConstraint()
        } childOf this

        UIText(title).constrain {
            y = 5.percent()
            x = CenterConstraint()
        } childOf box

        UIWrappedText(text, true, centered = true, trimText = true).constrain {
            width = 90.percent()
            height = 80.percent()
            x = 5.percent()
            y = 5.percent() + 11.pixel()
        } childOf box
    }

}