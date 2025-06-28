package tech.thatgravyboat.rewardclaim.remote

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.net.URI

object RemoteCodec {

    private val DEFAULT_REMOTE_DATA = RemoteData()

    private val URI_CODEC: Codec<URI> = Codec.STRING.xmap({ uriString -> URI.create(uriString.trim()) }, { uri -> uri.toString() })

    private val IMAGE_TYPE_CODEC: Codec<RemoteImageType> = RecordCodecBuilder.create { it.group(
        Codec.INT.optionalFieldOf("height", 0).forGetter(RemoteImageType::height),
        Codec.INT.optionalFieldOf("width", 0).forGetter(RemoteImageType::width),
        Codec.BOOL.optionalFieldOf("center", false).forGetter(RemoteImageType::center)
    ).apply(it, ::RemoteImageType) }

    private val IMAGE_CODEC: Codec<RemoteImage> = RecordCodecBuilder.create { it.group(
        URI_CODEC.fieldOf("url").forGetter(RemoteImage::url),
        Codec.STRING.optionalFieldOf("imageType", "").forGetter(RemoteImage::imageType)
    ).apply(it, ::RemoteImage) }

    private val REGEX_CODEC: Codec<Regex> = Codec.STRING.xmap({ Regex(it) }, { it.pattern })

    val CODEC: Codec<RemoteData> = RecordCodecBuilder.create { it.group(
        Codec.unboundedMap(Codec.STRING, IMAGE_TYPE_CODEC).optionalFieldOf("imageTypes", emptyMap()).forGetter(RemoteData::imageTypes),
        Codec.unboundedMap(Codec.STRING, IMAGE_CODEC).optionalFieldOf("textures", emptyMap()).forGetter(RemoteData::textures),
        REGEX_CODEC.optionalFieldOf("rewardRegex", DEFAULT_REMOTE_DATA.rewardRegex).forGetter(RemoteData::rewardRegex),
        REGEX_CODEC.optionalFieldOf("missedRewardRegex", DEFAULT_REMOTE_DATA.missedRewardRegex).forGetter(RemoteData::missedRewardRegex),
        Codec.STRING.optionalFieldOf("userAgent", DEFAULT_REMOTE_DATA.userAgent).forGetter(RemoteData::userAgent),
        Codec.BOOL.optionalFieldOf("disabled", DEFAULT_REMOTE_DATA.disabled).forGetter(RemoteData::disabled),
    ).apply(it, ::RemoteData) }
}