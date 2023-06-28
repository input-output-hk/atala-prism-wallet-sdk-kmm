package io.iohk.atala.prism.walletsdk.mercury

import io.iohk.atala.prism.walletsdk.domain.buildingblocks.Pluto
import io.iohk.atala.prism.walletsdk.domain.models.DID
import io.iohk.atala.prism.walletsdk.domain.models.DIDPair
import io.iohk.atala.prism.walletsdk.domain.models.Mediator
import io.iohk.atala.prism.walletsdk.domain.models.Message
import io.iohk.atala.prism.walletsdk.domain.models.PeerDID
import io.iohk.atala.prism.walletsdk.domain.models.PrismDIDInfo
import io.iohk.atala.prism.walletsdk.domain.models.PrivateKey
import io.iohk.atala.prism.walletsdk.domain.models.StorableCredential
import io.iohk.atala.prism.walletsdk.domain.models.VerifiableCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlutoMock : Pluto {
    var privateKeys = mutableListOf<PrivateKey>()

    override fun getDIDPrivateKeyByID(id: String): Flow<PrivateKey?> {
        val pk = privateKeys.find { it.keyCurve.curve.value == id }

        return flow { emit(pk) }
    }

    override fun storePrismDIDAndPrivateKeys(
        did: DID,
        keyPathIndex: Int,
        alias: String?,
        privateKeys: List<PrivateKey>
    ) {
        TODO("Not yet implemented")
    }

    override fun storePeerDID(did: DID) {
        TODO("Not yet implemented")
    }

    override fun storeDIDPair(host: DID, receiver: DID, name: String) {
        TODO("Not yet implemented")
    }

    override fun storeMessage(message: Message) {
        TODO("Not yet implemented")
    }

    override fun storeMessages(messages: List<Message>) {
        TODO("Not yet implemented")
    }

    override fun storePrivateKeys(privateKey: PrivateKey, did: DID, keyPathIndex: Int, metaId: String?) {
        TODO("Not yet implemented")
    }

    override fun storeMediator(mediator: DID, host: DID, routing: DID) {
        TODO("Not yet implemented")
    }

    override fun storeCredential(credential: VerifiableCredential) {
        TODO("Not yet implemented")
    }

    override fun getAllPrismDIDs(): Flow<List<PrismDIDInfo>> {
        TODO("Not yet implemented")
    }

    override fun getDIDInfoByDID(did: DID): Flow<PrismDIDInfo?> {
        TODO("Not yet implemented")
    }

    override fun getDIDInfoByAlias(alias: String): Flow<List<PrismDIDInfo>> {
        TODO("Not yet implemented")
    }

    override fun getPrismDIDKeyPathIndex(did: DID): Flow<Int?> {
        TODO("Not yet implemented")
    }

    override fun getPrismLastKeyPathIndex(): Flow<Int> {
        TODO("Not yet implemented")
    }

    override fun getAllPeerDIDs(): Flow<List<PeerDID>> {
        TODO("Not yet implemented")
    }

    override fun getDIDPrivateKeysByDID(did: DID): Flow<List<PrivateKey?>> {
        TODO("Not yet implemented")
    }

    override fun getAllDidPairs(): Flow<List<DIDPair>> {
        TODO("Not yet implemented")
    }

    override fun getPairByDID(did: DID): Flow<DIDPair?> {
        TODO("Not yet implemented")
    }

    override fun getPairByName(name: String): Flow<DIDPair?> {
        TODO("Not yet implemented")
    }

    override fun getAllMessages(): Flow<List<Message>> {
        TODO("Not yet implemented")
    }

    override fun getAllMessages(did: DID): Flow<List<Message>> {
        TODO("Not yet implemented")
    }

    override fun getAllMessagesSent(): Flow<List<Message>> {
        TODO("Not yet implemented")
    }

    override fun getAllMessagesReceived(): Flow<List<Message>> {
        TODO("Not yet implemented")
    }

    override fun getAllMessagesSentTo(did: DID): Flow<List<Message>> {
        TODO("Not yet implemented")
    }

    override fun getAllMessagesReceivedFrom(did: DID): Flow<List<Message>> {
        TODO("Not yet implemented")
    }

    override fun getAllMessagesOfType(type: String, relatedWithDID: DID?): Flow<List<Message>> {
        TODO("Not yet implemented")
    }

    override fun getAllMessages(from: DID, to: DID): Flow<List<Message>> {
        TODO("Not yet implemented")
    }

    override fun getMessage(id: String): Flow<Message?> {
        TODO("Not yet implemented")
    }

    override fun getAllMediators(): Flow<List<Mediator>> {
        TODO("Not yet implemented")
    }

    override fun getAllCredentials(): Flow<List<VerifiableCredential>> {
        TODO("Not yet implemented")
    }

    override fun getNewAllCredentials(): Flow<List<StorableCredential>> {
        TODO("Not yet implemented")
    }
}
