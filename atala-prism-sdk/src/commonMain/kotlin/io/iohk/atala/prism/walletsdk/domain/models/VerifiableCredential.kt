package io.iohk.atala.prism.walletsdk.domain.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * A data class representing a container for verifiable credential types.
 * This data class is used to encode and decode verifiable credential types for use with JSON.
 * The VerifiableCredentialTypeContainer contains properties for the ID and type of the verifiable credential.
 * Note: The VerifiableCredentialTypeContainer is used to encode and decode verifiable credential types for use with JSON.
 */
@Serializable
data class VerifiableCredentialTypeContainer(
    val id: String,
    val type: String
)

/**
 * Enum class representing different types of verifiable credentials.
 * The CredentialType is used to indicate the type of a verifiable credential.
 * The possible values of the enum are jwt, w3c, and unknown.
 * Note: The CredentialType enum is used to indicate the type of a verifiable credential.
 */
@Serializable
enum class CredentialType(val type: String) {
    JWT("jwt"),
    W3C("w3c"),
    ANONCREDS("anoncreds/credential-offer@v1.0"),
    Unknown("Unknown")
}

/**
 * Interface for objects representing verifiable credentials.
 */
@Serializable
sealed interface VerifiableCredential {
    val id: String
    val credentialType: CredentialType
    val context: Array<String>
    val type: Array<String>
    val credentialSchema: VerifiableCredentialTypeContainer?
    val credentialSubject: String
    val credentialStatus: VerifiableCredentialTypeContainer?
    val refreshService: VerifiableCredentialTypeContainer?
    val evidence: VerifiableCredentialTypeContainer?
    val termsOfUse: VerifiableCredentialTypeContainer?
    val issuer: DID?
    val issuanceDate: String // TODO(Date)
    val expirationDate: String? // TODO(Date)
    val validFrom: VerifiableCredentialTypeContainer?
    val validUntil: VerifiableCredentialTypeContainer?
    val proof: JsonString?
    val aud: Array<String>
    fun toJsonString(): String {
        return Json.encodeToString(this)
    }
}
