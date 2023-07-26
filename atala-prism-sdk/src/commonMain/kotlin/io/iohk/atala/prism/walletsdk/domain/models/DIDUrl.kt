package io.iohk.atala.prism.walletsdk.domain.models

import io.iohk.atala.prism.walletsdk.domain.DID_URL_SEPARATOR
import kotlin.jvm.JvmOverloads

/**
 * Represents a DIDUrl with "did", "path", "parameters", "fragment"
 * As specified in [w3 standards](https://www.w3.org/TR/did-core/#dfn-did-urls)
 */
data class DIDUrl @JvmOverloads constructor(
    val did: DID,
    val path: Array<String>? = arrayOf(),
    val parameters: Map<String, String>? = mapOf(),
    val fragment: String? = null
) {

    fun string(): String {
        return "${did}${fragmentString()}"
    }

    fun pathString(): String {
        return "/${path?.joinToString(DID_URL_SEPARATOR)}"
    }

    fun queryString(): String {
        return "?${parameters?.map { "${it.key}=${it.value}" }?.joinToString("&")}"
    }

    fun fragmentString(): String {
        return "#$fragment"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DIDUrl

        if (did != other.did) return false
        if (path != null) {
            if (other.path == null) return false
            if (!path.contentEquals(other.path)) return false
        } else if (other.path != null) return false
        if (parameters != other.parameters) return false
        if (fragment != other.fragment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = did.hashCode()
        result = 31 * result + (path?.contentHashCode() ?: 0)
        result = 31 * result + (parameters?.hashCode() ?: 0)
        result = 31 * result + (fragment?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return string()
    }
}