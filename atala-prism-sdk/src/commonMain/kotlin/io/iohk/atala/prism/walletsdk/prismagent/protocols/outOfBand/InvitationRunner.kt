package io.iohk.atala.prism.walletsdk.prismagent.protocols.outOfBand

import io.iohk.atala.prism.walletsdk.domain.buildingblocks.Mercury
import io.iohk.atala.prism.walletsdk.domain.models.Message
import io.ktor.http.Url

/**
 * The InvitationRunner class is responsible for running the invitation process by parsing the out-of-band URL,
 * unpacking the message, and returning the unpacked message object.
 *
 * @param mercury The Mercury interface implementation used for packing and unpacking messages.
 * @param url The URL object representing the out-of-band URL.
 */
class InvitationRunner(private val mercury: Mercury, private val url: Url) {
    /**
     * Runs the invitation process by parsing the out-of-band URL, unpacking the message, and returning the unpacked message object.
     *
     * @return The unpacked [Message] object.
     */
    suspend fun run(): Message {
        val messageString = OutOfBandParser().parseMessage(url)
        return mercury.unpackMessage(messageString)
    }
}
