package org.hyperledger.identus.walletsdk.edgeagent

import org.hyperledger.identus.walletsdk.domain.models.Credential
import org.hyperledger.identus.walletsdk.domain.models.CredentialType
import org.hyperledger.identus.walletsdk.domain.models.Error
import org.hyperledger.identus.walletsdk.domain.models.KnownPrismError

sealed class EdgeAgentError : KnownPrismError() {

    class InvitationIsInvalidError : EdgeAgentError() {
        override val code: Int
            get() = 61

        override val message: String
            get() = "The system could not parse the invitation, the message/json are invalid"
    }

    class UnknownInvitationTypeError(private val type: String) : EdgeAgentError() {
        override val code: Int
            get() = 62

        override val message: String
            get() = "The type of the invitation is not supported: $type"
    }

    class InvalidMessageType(private val type: String, private val shouldBe: String) : EdgeAgentError() {
        override val code: Int
            get() = 63

        override val message: String
            get() = "The following message $type, does not represent the protocol $shouldBe.\nAlso the message should have \"from\" and \"to\" fields\n"
    }

    class NoMediatorAvailableError : EdgeAgentError() {
        override val code: Int
            get() = 64

        override val message: String
            get() = "There is no mediator.\nYou need to provide a mediation handler and start the prism agent before doing some operations."
    }

    class MediationRequestFailedError
    @JvmOverloads constructor(private val underlyingError: Array<Error>? = null) : EdgeAgentError() {
        override val code: Int
            get() = 65

        override val message: String
            get() {
                val errorsMessages = underlyingError?.joinToString(separator = "\n") { errorDescription ?: "" }
                val message = errorsMessages?.map { "Errors: $it" }
                return "Something failed while trying to achieve mediation. $message"
            }
    }

    class CannotFindDIDPrivateKey(private val did: String) : EdgeAgentError() {

        override val code: Int
            get() = 66
        override val message: String
            get() = "Could not find private key for DID: $did"
    }

    class FailedToOnboardError(private val statusCode: Int, private val response: String) : EdgeAgentError() {
        override val code: Int
            get() = 67

        override val message: String
            get() = "Failed to onboard.\nStatus code: $statusCode\nResponse: $response"
    }

    class InvalidCredentialError(
        private val credential: Credential? = null,
        private val type: CredentialType? = null
    ) :
        EdgeAgentError() {
        override val code: Int
            get() = 68

        override val message: String
            get() = when {
                credential != null -> "Invalid credential, ${credential::class.simpleName} is not supported."
                type != null -> "Invalid credential type, ${type.name} is not supported."
                else -> "Invalid credential. No further information is available."
            }
    }

    class InvalidCredentialFormatError(private val expectedFormat: CredentialType) :
        EdgeAgentError() {
        override val code: Int
            get() = 69

        override val message: String
            get() = "Invalid credential format, it must be ${expectedFormat.type}"
    }

    class AttachmentTypeNotSupported : EdgeAgentError() {
        override val code: Int
            get() = 610

        override val message: String
            get() = "Attachment type not supported, expecting base 64 attachment."
    }

    class EdgeAgentStateAcceptOnlyOneObserver : EdgeAgentError() {
        override val code: Int
            get() = 611

        override val message: String
            get() = "Agent state only accepts one subscription."
    }

    class InvalidCredentialMetadata : EdgeAgentError() {
        override val code: Int
            get() = 612

        override val message: String
            get() = "Invalid or null credential metadata"
    }

    class MissingOrNullFieldError(private val field: String, private val parent: String) :
        EdgeAgentError() {
        override val code: Int
            get() = 613

        override val message: String = ""
            get() = "$field from $parent is missing or null, and is mandatory"
    }
}
