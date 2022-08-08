package juuxel.toolchainversions.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

object Serializers {
    abstract class StringSerializer<T>(name: String) : KSerializer<T> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor(name, PrimitiveKind.STRING)

        protected abstract fun fromString(string: String): T
        protected abstract fun toString(value: T): String

        override fun deserialize(decoder: Decoder): T =
            fromString(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: T) =
            encoder.encodeString(toString(value))
    }

    object ForInstant : StringSerializer<Instant>("Instant") {
        override fun fromString(string: String): Instant = Instant.parse(string)
        override fun toString(value: Instant): String = value.toString()
    }
}
