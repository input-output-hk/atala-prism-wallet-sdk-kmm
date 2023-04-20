package io.iohk.atala.prism.walletsdk.prismagent.protocols.issueCredential

import io.iohk.atala.prism.walletsdk.domain.models.Message
import io.iohk.atala.prism.walletsdk.domain.models.PrismAgentError
import io.iohk.atala.prism.walletsdk.prismagent.connectionsmanager.DIDCommConnection
import kotlinx.serialization.Serializable
import kotlin.jvm.Throws

@Serializable
class IssueCredentialProtocol {

    var stage: Stage
    var propose: ProposeCredential? = null
    var offer: OfferCredential? = null
    var request: RequestCredential? = null
    val connector: DIDCommConnection

    @JvmOverloads
    constructor(
        stage: Stage,
        proposeMessage: Message? = null,
        offerMessage: Message? = null,
        requestMessage: Message? = null,
        connector: DIDCommConnection
    ) {
        this.stage = stage
        this.connector = connector
        this.propose = proposeMessage?.let {
            try {
                ProposeCredential.fromMessage(it)
            } catch (e: Throwable) {
                null
            }
        }
        this.offer = offerMessage?.let {
            try {
                OfferCredential.fromMessage(it)
            } catch (e: Throwable) {
                null
            }
        }
        this.request = requestMessage?.let {
            try {
                RequestCredential.fromMessage(it)
            } catch (e: Throwable) {
                null
            }
        }
    }

    @Throws(PrismAgentError.InvalidStepError::class)
    constructor(message: Message, connector: DIDCommConnection) {
        this.connector = connector
        val proposed = try {
            ProposeCredential.fromMessage(message)
        } catch (e: Throwable) {
            null
        }
        val offered = try {
            OfferCredential.fromMessage(message)
        } catch (e: Throwable) {
            null
        }
        val requested = try {
            RequestCredential.fromMessage(message)
        } catch (e: Throwable) {
            null
        }

        when {
            proposed != null -> {
                this.stage = Stage.PROPOSE
                this.propose = proposed
            }
            offered != null -> {
                this.stage = Stage.OFFER
                this.offer = offered
            }
            requested != null -> {
                this.stage = Stage.REQUEST
                this.request = requested
            }
            else -> throw PrismAgentError.InvalidStepError()
        }
    }

    suspend fun nextStage() {
        if (this.stage == Stage.PROPOSE) {
            if (propose == null) {
                stage = Stage.REFUSED
                return
            }
        } else if (this.stage == Stage.OFFER) {
            if (offer == null) {
                stage = Stage.REFUSED
                return
            }
        }

        val messageId: String = when (this.stage) {
            Stage.PROPOSE -> {
                val message = OfferCredential.makeOfferFromProposedCredential(proposed = propose!!)
                connector.sendMessage(message.makeMessage())
                message.id
            }
            Stage.OFFER -> {
                val message = RequestCredential.makeRequestFromOfferCredential(offer = offer!!).makeMessage()
                connector.sendMessage(message)
                message.id
            }
            Stage.REQUEST -> null
            Stage.COMPLETED -> null
            Stage.REFUSED -> null
        } ?: return

        val response = connector.awaitMessageResponse(id = messageId) ?: return

        val issued = try {
            IssueCredential.fromMessage(response)
        } catch (e: Throwable) {
            null
        }
        val offered = try {
            OfferCredential.fromMessage(response)
        } catch (e: Throwable) {
            null
        }
        val requested = try {
            RequestCredential.fromMessage(response)
        } catch (e: Throwable) {
            null
        }

        when {
            offered != null -> {
                this.stage = Stage.OFFER
                this.offer = offered
            }
            issued != null -> {
                this.stage = Stage.COMPLETED
            }
            requested != null -> {
                this.stage = Stage.REQUEST
                this.request = requested
            }
        }
    }

    enum class Stage {
        PROPOSE,
        OFFER,
        REQUEST,
        COMPLETED,
        REFUSED
    }
}