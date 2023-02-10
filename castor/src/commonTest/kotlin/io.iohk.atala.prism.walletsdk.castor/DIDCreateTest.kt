package io.iohk.atala.prism.walletsdk.castor

import io.iohk.atala.prism.walletsdk.domain.models.CastorError
import io.iohk.atala.prism.walletsdk.domain.models.Curve
import io.iohk.atala.prism.walletsdk.domain.models.DIDDocument
import io.iohk.atala.prism.walletsdk.domain.models.KeyCurve
import io.iohk.atala.prism.walletsdk.domain.models.KeyPair
import io.iohk.atala.prism.walletsdk.domain.models.PrivateKey
import io.iohk.atala.prism.walletsdk.domain.models.PublicKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DIDCreateTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun it_should_create_peerDID_correctly() = runTest {
        val verKeyStr = "z6LSci5EK4Ezue5QA72ZX71QUbXY2xr5ygRw7wM1WJigTNnd"
        val authKeyStr = "z6MkqgCXHEGr2wJZANPZGC8WFmeVuS3abAD9uvh7mTXygCFv"
        val serviceStr = "eyJpZCI6IkRJRENvbW1WMiIsInQiOiJkbSIsInMiOiJsb2NhbGhvc3Q6ODA4MiIsInIiOltdLCJhIjpbImRtIl19"

        val fakeVerPrivateKey = PrivateKey(KeyCurve(Curve.X25519), "".encodeToByteArray())
        val fakeAuthPrivateKey = PrivateKey(KeyCurve(Curve.ED25519), "".encodeToByteArray())
        val verificationPubKey = PublicKey(KeyCurve(Curve.X25519), verKeyStr.encodeToByteArray())
        val authenticationPubKey = PublicKey(KeyCurve(Curve.ED25519), authKeyStr.encodeToByteArray())
        val verificationKeyPair = KeyPair(KeyCurve(Curve.X25519), fakeVerPrivateKey, verificationPubKey)
        val authenticationKeyPair = KeyPair(KeyCurve(Curve.ED25519), fakeAuthPrivateKey, authenticationPubKey)
        val service = DIDDocument.Service(
            id = "DIDCommV2",
            type = arrayOf("DIDCommMessaging"),
            serviceEndpoint = DIDDocument.ServiceEndpoint(
                uri = "localhost:8082",
                accept = arrayOf("DIDCommMessaging"),
                routingKeys = arrayOf()
            )
        )
        val keyPairs: Array<KeyPair> = arrayOf(verificationKeyPair, authenticationKeyPair)
        val castor = CastorImpl()
        val did = castor.createPeerDID(keyPairs, arrayOf(service))
        assertEquals(did.toString(), "did:peer:2.E$verKeyStr.V$authKeyStr.S$serviceStr")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun it_should_throw_errors_if_wrong_keys_are_provided() = runTest {
        val verKeyStr = "z6LSci5EK4Ezue5QA72ZX71QUbXY2xr5ygRw7wM1WJigTNnd"
        val authKeyStr = "z6MkqgCXHEGr2wJZANPZGC8WFmeVuS3abAD9uvh7mTXygCFv"

        val fakeVerPrivateKey = PrivateKey(KeyCurve(Curve.ED25519), "".encodeToByteArray())
        val fakeAuthPrivateKey = PrivateKey(KeyCurve(Curve.X25519), "".encodeToByteArray())

        val verificationPubKey = PublicKey(KeyCurve(Curve.ED25519), verKeyStr.encodeToByteArray())
        val authenticationPubKey = PublicKey(KeyCurve(Curve.X25519), authKeyStr.encodeToByteArray())

        val verificationKeyPair = KeyPair(KeyCurve(Curve.ED25519), fakeVerPrivateKey, verificationPubKey)
        val authenticationKeyPair = KeyPair(KeyCurve(Curve.ED25519), fakeAuthPrivateKey, authenticationPubKey)

        val keyPairs: Array<KeyPair> = arrayOf(verificationKeyPair, authenticationKeyPair)

        val service = DIDDocument.Service(
            id = "DIDCommV2",
            type = arrayOf("DIDCommMessaging"),
            serviceEndpoint = DIDDocument.ServiceEndpoint(
                uri = "localhost:8082",
                accept = arrayOf("DIDCommMessaging"),
                routingKeys = arrayOf()
            )
        )

        val castor = CastorImpl()

        assertFailsWith<CastorError.InvalidKeyError> {
            castor.createPeerDID(keyPairs, arrayOf(service))
        }
    }
}