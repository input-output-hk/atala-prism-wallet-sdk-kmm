@file:Suppress("ktlint:standard:import-ordering")

package io.iohk.atala.prism.walletsdk.prismagent

import io.iohk.atala.prism.apollo.base64.base64UrlDecoded
import io.iohk.atala.prism.walletsdk.domain.buildingblocks.Castor
import io.iohk.atala.prism.walletsdk.domain.buildingblocks.Mercury
import io.iohk.atala.prism.walletsdk.domain.buildingblocks.Pluto
import io.iohk.atala.prism.walletsdk.domain.buildingblocks.Pollux
import io.iohk.atala.prism.walletsdk.domain.models.AttachmentBase64
import io.iohk.atala.prism.walletsdk.domain.models.CredentialType
import io.iohk.atala.prism.walletsdk.domain.models.DID
import io.iohk.atala.prism.walletsdk.domain.models.DIDPair
import io.iohk.atala.prism.walletsdk.domain.models.Message
import io.iohk.atala.prism.walletsdk.prismagent.connectionsmanager.ConnectionsManager
import io.iohk.atala.prism.walletsdk.prismagent.connectionsmanager.DIDCommConnection
import io.iohk.atala.prism.walletsdk.prismagent.mediation.MediationHandler
import io.iohk.atala.prism.walletsdk.prismagent.protocols.ProtocolType
import io.iohk.atala.prism.walletsdk.prismagent.protocols.issueCredential.IssueCredential
import io.iohk.atala.prism.walletsdk.prismagent.protocols.revocation.RevocationNotification
import java.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    private var pairings: MutableList<DIDPair>,
    private val pollux: Pollux,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ConnectionsManager, DIDCommConnection {

    var fetchingMessagesJob: Job? = null

    /**
     * Starts the process of fetching messages at a regular interval.
     *
     * @param requestInterval The time interval (in seconds) between message fetch requests.
     *                        Defaults to 5 seconds if not specified.
     */
    @JvmOverloads
    fun startFetchingMessages(requestInterval: Int = 5) {
        // Check if the job for fetching messages is already running
        if (fetchingMessagesJob == null) {
            // Launch a coroutine in the provided scope
            fetchingMessagesJob = scope.launch {
                // Retrieve the current mediator DID
                val currentMediatorDID = mediationHandler.mediatorDID
                // Resolve the DID document for the mediator
                val mediatorDidDoc = castor.resolveDID(currentMediatorDID.toString())
                var serviceEndpoint: String? = null

                // Loop through the services in the DID document to find a WebSocket endpoint
                mediatorDidDoc.services.forEach {
                    if (it.serviceEndpoint.uri.contains("wss://") || it.serviceEndpoint.uri.contains("ws://")) {
                        serviceEndpoint = it.serviceEndpoint.uri
                        return@forEach // Exit loop once the WebSocket endpoint is found
                    }
                }

                // If a WebSocket service endpoint is found
                serviceEndpoint?.let { serviceEndpointUrl ->
                    // Listen for unread messages on the WebSocket endpoint
                    mediationHandler.listenUnreadMessages(
                        serviceEndpointUrl
                    ) { arrayMessages ->
                        processMessages(arrayMessages)
//                        // Process the received messages
//                        val messagesIds = mutableListOf<String>()
//                        val messages = mutableListOf<Message>()
//                        arrayMessages.map { pair ->
//                            messagesIds.add(pair.first)
//                            messages.add(pair.second)
//                        }
//                        // If there are any messages, mark them as read and store them
//                        scope.launch {
//                            if (messagesIds.isNotEmpty()) {
//                                mediationHandler.registerMessagesAsRead(
//                                    messagesIds.toTypedArray()
//                                )
//                                pluto.storeMessages(messages)
//                            }
//                        }
                    }
                }

                // Fallback mechanism if no WebSocket service endpoint is available
                if (serviceEndpoint == null) {
                    while (true) {
                        // Continuously await and process new messages
                        awaitMessages().collect { array ->
                            processMessages(array)
//                            val messagesIds = mutableListOf<String>()
//                            val messages = mutableListOf<Message>()
//                            array.map { pair ->
//                                messagesIds.add(pair.first)
//                                messages.add(pair.second)
//                            }
//                            if (messagesIds.isNotEmpty()) {
//                                mediationHandler.registerMessagesAsRead(
//                                    messagesIds.toTypedArray()
//                                )
//                                pluto.storeMessages(messages)
//                            }
                        }
                        // Wait for the specified request interval before fetching new messages
                        delay(Duration.ofSeconds(requestInterval.toLong()).toMillis())
                    }
                }
            }

            // Start the coroutine if it's not already active
            fetchingMessagesJob?.let {
                if (it.isActive) return
                it.start()
            }
        }
    }

    fun stopConnection() {
        fetchingMessagesJob?.cancel()
    }

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

    internal fun processMessages(arrayMessages: Array<Pair<String, Message>>) {
        scope.launch {
            val messagesIds = mutableListOf<String>()
            val messages = mutableListOf<Message>()
            arrayMessages.map { pair ->
                messagesIds.add(pair.first)
                messages.add(pair.second)
            }

            val allMessages = pluto.getAllMessages().first()

            val revokedMessages = messages.filter { it.piuri == ProtocolType.PrismRevocation.value }
            revokedMessages.forEach { msg ->
                val revokedMessage = RevocationNotification.fromMessage(msg)
                val threadId = revokedMessage.body.threadId
                val matchingMessages =
                    allMessages.filter { it.piuri == ProtocolType.DidcommIssueCredential.value && it.thid == threadId }
                if (matchingMessages.isNotEmpty()) {
                    matchingMessages.forEach { message ->
                        val issueMessage = IssueCredential.fromMessage(message)
                        if (pollux.extractCredentialFormatFromMessage(issueMessage.attachments) == CredentialType.JWT) {
                            val attachment =
                                issueMessage.attachments.firstOrNull()?.data as? AttachmentBase64
                            attachment?.let {
                                val credentialId = it.base64.base64UrlDecoded
                                pluto.revokeCredential(credentialId)
                            }
                        }
                    }
                }
            }

            // If there are any messages, mark them as read and store them
            if (messagesIds.isNotEmpty()) {
                mediationHandler.registerMessagesAsRead(
                    messagesIds.toTypedArray()
                )
                pluto.storeMessages(messages)
            }
        }
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
