package io.iohk.atala.prism.walletsdk.domain.models

import kotlinx.serialization.Serializable

/**
 * Data class representing supported key curves for key generation.
 */
@Serializable
data class KeyCurve @JvmOverloads constructor(
    val curve: Curve,
    val index: Int? = 0
)

@Serializable
enum class Curve(val value: String) {
    X25519("X25519"),
    ED25519("Ed25519"),
    SECP256K1("secp256k1")
}