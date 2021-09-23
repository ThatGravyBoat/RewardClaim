package tech.thatgravyboat.rewardclaim

import java.util.regex.Pattern

private val TRANSLATION_LINE_REGEX = Pattern.compile("\"(?<key>.*)\": ?\"(?<text>.*)\",?")

class RewardLanguage(translationDataFromHtml: String) {

    private val translations = hashMapOf<String, String>()

    fun translate(key: String) = translations.getValue(key)

    //We have to do it this way as this is easier than fixing all the things that isn't
    //valid in normal json but is valid in javascript objects. Such as escaped single quotes and trailing commas.
    init {
        TRANSLATION_LINE_REGEX.matcher(translationDataFromHtml.replace("\\'", "'")).apply {
            while (find()) translations[group("key")] = group("text")
        }
    }
}