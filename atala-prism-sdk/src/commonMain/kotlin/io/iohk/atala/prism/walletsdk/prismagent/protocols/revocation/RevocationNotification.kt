package io.iohk.atala.prism.walletsdk.prismagent.protocols.revocation

import io.iohk.atala.prism.walletsdk.domain.models.DID
import io.iohk.atala.prism.walletsdk.domain.models.Message
import io.iohk.atala.prism.walletsdk.prismagent.PrismAgentError
import io.iohk.atala.prism.walletsdk.prismagent.protocols.ProtocolType
import kotlinx.serialization.SerialName
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RevocationNotification(
    val id: String = UUID.randomUUID().toString(),
    val body: Body,
    val from: DID,
    val to: DID
) {
    val type = ProtocolType.PrismRevocation

    fun makeMessage(): Message {
        return Message(
            id = id,
            piuri = type.value,
            from = from,
            to = to,
            body = Json.encodeToString(body)
        )
    }

    @Serializable
    data class Body @JvmOverloads constructor(
        @SerialName("issueCredentialProtocolThreadId")
        val threadId: String,
        val comment: String?
    )

    companion object {
        fun fromMessage(message: Message): RevocationNotification {
            require(
                message.piuri == ProtocolType.PrismRevocation.value &&
                        message.from != null &&
                        message.to != null
            ) {
                throw PrismAgentError.InvalidMessageType(
                    type = message.piuri,
                    shouldBe = ProtocolType.PrismRevocation.value
                )
            }
            return RevocationNotification(
                body = Json.decodeFromString(message.body),
                from = message.from,
                to = message.to
            )
        }
    }
}