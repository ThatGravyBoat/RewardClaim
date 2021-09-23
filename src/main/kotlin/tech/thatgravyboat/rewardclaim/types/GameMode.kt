package tech.thatgravyboat.rewardclaim.types

@Suppress("unused")
enum class GameMode(val displayName: String) {
    ERROR("Error"),
    BEDWARS("Bed Wars"),
    SKYWARS("SkyWars"),
    PROTOTYPE("Prototype"),
    SKYBLOCK("SkyBlock"),
    MAIN("Main"),
    MURDER_MYSTERY("Murder Mystery"),
    HOUSING("Housing"),
    ARCADE("Arcade"),
    BUILD_BATTLE("Build Battle"),
    DUELS("Duels"),
    PIT("PIT"),
    UHC("UHC"),
    SPEED_UHC("Speed UHC"),
    TNTGAMES("TNT Games"),
    LEGACY("Classic"),
    QUAKECRAFT("Quakecraft"),
    WALLS("Walls"),
    PAINTBALL("Paintball"),
    VAMPIREZ("VampireZ"),
    ARENA("Arena"),
    GINGERBREAD("Turbo Kart Racers"),
    MCGO("Cops and Crims"),
    SURVIVAL_GAMES("Blitz SG"),
    WALLS3("Mega Walls"),
    SUPER_SMASH("Smash Heroes"),
    BATTLEGROUND("Warlords");

    companion object {
        fun getModeFromId(id: String?): GameMode {
            return try {
                valueOf(id!!)
            } catch (e: Exception) {
                ERROR
            }
        }
    }
}