package io.iohk.atala.prism.walletsdk.prismagent

import io.iohk.atala.prism.walletsdk.domain.buildingblocks.Castor
import io.iohk.atala.prism.walletsdk.domain.buildingblocks.Mercury
import io.iohk.atala.prism.walletsdk.domain.buildingblocks.Pluto
import io.iohk.atala.prism.walletsdk.domain.models.DID
import io.iohk.atala.prism.walletsdk.domain.models.DIDPair
import io.iohk.atala.prism.walletsdk.domain.models.Message
import io.iohk.atala.prism.walletsdk.prismagent.connectionsmanager.ConnectionsManager
import io.iohk.atala.prism.walletsdk.prismagent.connectionsmanager.DIDCommConnection
import io.iohk.atala.prism.walletsdk.prismagent.mediation.MediationHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.jvm.Throws

/**
 * ConnectionManager is responsible for managing connections and communication between entities.
 *
 * @property mercury The instance of the Mercury interface used for sending and receiving messages.
 * @property castor The instance of the Castor interface used for working with DIDs.
 * @property pluto The instance of the Pluto interface used for storing messages and connection information.
 * @property mediationHandler The instance of the MediationHandler interface used for handling mediation.
 * @property pairings The mutable list of DIDPair representing the connections managed by the ConnectionManager.
 */
class ConnectionManager(
    private val mercury: Mercury,
    private val castor: Castor,
    private val pluto: Pluto,
    internal val mediationHandler: MediationHandler,
    private var pairings: MutableList<DIDPair>
) : ConnectionsManager, DIDCommConnection {

    /**
     * Suspends the current coroutine and boots the registered mediator associated with the mediator handler.
     * If no mediator is available, a [PrismAgentError.NoMediatorAvailableError] is thrown.
     *
     * @throws PrismAgentError.NoMediatorAvailableError if no mediator is available.
     */
    suspend fun startMediator() {
        mediationHandler.bootRegisteredMediator() ?: throw PrismAgentError.NoMediatorAvailableError()
    }

    /**
     * Registers a mediator with the given host DID.
     *
     * @param host The DID of the entity to mediate with.
     */
    suspend fun registerMediator(host: DID) {
        mediationHandler.achieveMediation(host).collect {
            println("Achieve mediation")
        }
    }

    /**
     * Sends a message over the connection.
     *
     * @param message The message to send.
     * @return The response message, if one is received.
     */
    @Throws(PrismAgentError.NoMediatorAvailableError::class)
    override suspend fun sendMessage(message: Message): Message? {
        if (mediationHandler.mediator == null) {
            throw PrismAgentError.NoMediatorAvailableError()
        }
        pluto.storeMessage(message)
        return mercury.sendMessageParseResponse(message)
    }

    /**
     * Awaits messages from the connection.
     *
     * @return An array of messages received from the connection.
     */
    override suspend fun awaitMessages(): Flow<Array<Pair<String, Message>>> {
        return mediationHandler.pickupUnreadMessages(NUMBER_OF_MESSAGES)
    }

    /**
     * Adds a connection to the manager.
     *
     * @param paired The [DIDPair] representing the connection to be added.
     */
    override suspend fun addConnection(paired: DIDPair) {
        if (pairings.contains(paired)) return
        pluto.storeDIDPair(paired.host, paired.receiver, paired.name ?: "")
        pairings.add(paired)
    }

    /**
     * Removes a connection from the manager.
     *
     * @param pair The [DIDPair] representing the connection to be removed.
     * @return The [DIDPair] object that was removed from the manager, or null if the connection was not found.
     */
    override suspend fun removeConnection(pair: DIDPair): DIDPair? {
        val index = pairings.indexOf(pair)
        if (index > -1) {
            pairings.removeAt(index)
        }
        return null
    }

    /**
     * Awaits a response to a specified message ID from the connection.
     *
     * @param id The ID of the message for which to await a response.
     * @return The response message, if one is received.
     */
    override suspend fun awaitMessageResponse(id: String): Message? {
        return try {
            awaitMessages().first().map {
                it.second
            }.first {
                it.thid == id
            }
        } catch (e: NoSuchElementException) {
            null
        }
    }

    companion object {
        const val NUMBER_OF_MESSAGES = 10
    }
}
