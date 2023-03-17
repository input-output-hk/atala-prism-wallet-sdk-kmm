package io.iohk.atala.prism.walletsdk.prismagent.mediation

import io.iohk.atala.prism.apollo.uuid.UUID
import io.iohk.atala.prism.walletsdk.domain.buildingBlocks.Mercury
import io.iohk.atala.prism.walletsdk.domain.buildingBlocks.Pluto
import io.iohk.atala.prism.walletsdk.domain.models.AttachmentBase64
import io.iohk.atala.prism.walletsdk.domain.models.AttachmentDescriptor
import io.iohk.atala.prism.walletsdk.domain.models.AttachmentJsonData
import io.iohk.atala.prism.walletsdk.domain.models.DID
import io.iohk.atala.prism.walletsdk.domain.models.Mediator
import io.iohk.atala.prism.walletsdk.domain.models.Message
import io.iohk.atala.prism.walletsdk.domain.models.PrismAgentError
import io.iohk.atala.prism.walletsdk.prismagent.helpers.KMMPair
import io.iohk.atala.prism.walletsdk.prismagent.protocols.mediation.MediationGrant
import io.iohk.atala.prism.walletsdk.prismagent.protocols.mediation.MediationKeysUpdateList
import io.iohk.atala.prism.walletsdk.prismagent.protocols.mediation.MediationRequest
import io.iohk.atala.prism.walletsdk.prismagent.protocols.pickup.PickupDelivery
import io.iohk.atala.prism.walletsdk.prismagent.protocols.pickup.PickupReceived
import io.iohk.atala.prism.walletsdk.prismagent.protocols.pickup.PickupRequest
import kotlinx.coroutines.flow.first
import kotlin.js.Promise

@OptIn(ExperimentalJsExport::class)
final class DefaultMediationHandler(
    override val mediatorDID: DID,
    private val mercury: Mercury,
    private val store: MediatorRepository,
) : MediationHandler {
    final class PlutoMediatorRepositoryImpl(private val pluto: Pluto) : MediatorRepository {
        override suspend fun getAllMediators(): List<Mediator> {
            return pluto.getAllMediators().first()
        }

        override fun storeMediator(mediator: Mediator) {
            pluto.storeMediator(mediator.mediatorDID, mediator.hostDID, mediator.routingDID)
        }
    }

    override var mediator: Mediator? = null
        private set

    init {
        this.mediator = null
    }

    override suspend fun bootRegisteredMediator(): Mediator? {
        if (mediator == null) {
            mediator = store.getAllMediators().first()
        }
        return mediator
    }

    override fun achieveMediation(host: DID): Promise<Mediator> {
        val requestMessage = MediationRequest(from = host, to = mediatorDID).makeMessage()
        return mercury.sendMessageParseMessage(message = requestMessage)
            .then {
                val grantedMessage = it?.let { MediationGrant(it) } ?: throw PrismAgentError.mediationRequestFailedError()
                val routingDID = DID(grantedMessage.body.routingDid)
                Mediator(
                    id = UUID.randomUUID4().toString(),
                    mediatorDID = mediatorDID,
                    hostDID = host,
                    routingDID = routingDID,
                )
            }
    }

    override fun updateKeyListWithDIDs(dids: Array<DID>): Promise<Boolean> {
        val keyListUpdateMessage = mediator?.let {
            MediationKeysUpdateList(
                from = it.hostDID,
                to = it.mediatorDID,
                recipientDids = dids,
            ).makeMessage()
        } ?: throw PrismAgentError.noMediatorAvailableError()
        return keyListUpdateMessage
            .let { message -> mercury.sendMessage(message).then { true } }
    }

    override fun pickupUnreadMessages(limit: Int): Promise<Array<KMMPair<String, Message>>> {
        val requestMessage = mediator?.let {
            PickupRequest(
                from = it.hostDID,
                to = it.mediatorDID,
                body = PickupRequest.Body(null, limit.toString()),
            ).makeMessage()
        } ?: throw PrismAgentError.noMediatorAvailableError()

        return mercury.sendMessageParseMessage(requestMessage)
            .then {
                val receivedMessage = it?.let { PickupDelivery(it) }
                receivedMessage?.let { delivery ->
                    delivery.attachments.mapNotNull { attachment: AttachmentDescriptor ->
                        val data = attachment.data
                        when (data) {
                            is AttachmentBase64 -> KMMPair(it.id, data.base64)
                            is AttachmentJsonData -> KMMPair(it.id, data.data)
                            else -> null
                        }
                    }.map { pair ->
                        KMMPair(pair.first, mercury.unpackMessage(pair.second))
                    }.toTypedArray()
                } ?: emptyArray()
            }
    }

    override fun registerMessagesAsRead(ids: Array<String>): Promise<Boolean> {
        val requestMessage = mediator?.let {
            PickupReceived(
                from = it.hostDID,
                to = it.mediatorDID,
                body = PickupReceived.Body(messageIdList = ids),
            ).makeMessage()
        } ?: throw PrismAgentError.noMediatorAvailableError()
        return mercury.sendMessage(requestMessage).then { true }
    }
}