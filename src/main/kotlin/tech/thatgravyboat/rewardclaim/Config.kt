package tech.thatgravyboat.rewardclaim

import gg.essential.universal.UDesktop
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.io.File
import java.net.URI

@Suppress("unused")
object Config : Vigilant(File("./config/rewardclaim.toml")) {

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Confirmation",
        category = "General",
        description = "Shows a confirmation before you claim an item to make sure you don't accidentally claim a reward you didn't want."
    )
    var showConfirmation = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Double Click Confirmation",
        category = "General",
        description = "Shows a confirmation before you double click claim an item to make sure you don't accidentally claim a reward you didn't want."
    )
    var showDoubleClickConfirmation = true

    @Property(
        type = PropertyType.NUMBER,
        name = "Checking timer",
        category = "General",
        description = "Determines how long it will take to check and ignore screen changes, if you are high ping you may want this higher.",
        min = 1000,
        max = 10000,
        increment = 200
    )
    var checkingTimer = 3000

    @Property(
        type = PropertyType.BUTTON,
        name = "Discord",
        category = "General",
        subcategory = "Self Promotion",
        placeholder = "Visit"
    )
    fun discord() {
        UDesktop.browse(URI("https://discord.gg/jRhkYFmpCa"))
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Twitter",
        category = "General",
        subcategory = "Self Promotion",
        placeholder = "Visit"
    )
    fun twitter() {
        UDesktop.browse(URI("https://twitter.com/ThatGravyBoat"))
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "Github",
        category = "General",
        subcategory = "Self Promotion",
        placeholder = "Visit"
    )
    fun github() {
        UDesktop.browse(URI("https://github.com/ThatGravyBoat/RewardClaim"))
    }

    @Property(
        type = PropertyType.BUTTON,
        name = "YouTube",
        category = "General",
        subcategory = "Self Promotion",
        placeholder = "Visit"
    )
    fun rickroll() {
        UDesktop.browse(URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
    }

    init {
        initialize()
    }
}
