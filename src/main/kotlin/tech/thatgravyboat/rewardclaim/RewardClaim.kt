//#if MODERN==0
package tech.thatgravyboat.rewardclaim


import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod(
    name = "RewardClaim",
    modid = "gravyrewardclaim",
    version = "1.0.4",
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object RewardClaim {

    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent?) {
        Initializer.init()
    }
}
//#endif