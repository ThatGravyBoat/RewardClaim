package tech.thatgravyboat.rewardclaim.data

private val TRANSLATION_LINE_REGEX = Regex("\"(?<key>.*)\": ?\"(?<text>.*)\",?")

class Language(data: String) {

    private val translations = mutableMapOf<String, String>()

    init {
        for (result in TRANSLATION_LINE_REGEX.findAll(data.replace("\\'", "'"))) {
            translations[result.groups["key"]!!.value] = result.groups["text"]!!.value
        }
    }

    operator fun get(key: String): String {
        return translations[key] ?: key
    }
}