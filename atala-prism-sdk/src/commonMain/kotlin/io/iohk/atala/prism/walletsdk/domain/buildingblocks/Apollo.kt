package io.iohk.atala.prism.walletsdk.domain.buildingblocks

import io.iohk.atala.prism.apollo.derivation.MnemonicLengthException
import io.iohk.atala.prism.walletsdk.domain.models.Seed
import io.iohk.atala.prism.walletsdk.domain.models.SeedWords
import io.iohk.atala.prism.walletsdk.domain.models.keyManagement.KeyRestoration
import io.iohk.atala.prism.walletsdk.domain.models.keyManagement.PrivateKey
import kotlin.jvm.Throws

/**
 * Apollo defines the set of cryptographic operations that are used in the Atala PRISM.
 */
interface Apollo : KeyRestoration {

    /**
     * Creates a random set of mnemonic phrases that can be used as a seed for generating
     * a private key.
     *
     * @return An array of mnemonic phrases.
     */
    fun createRandomMnemonics(): Array<String>

    /**
     * Takes in a set of mnemonics and a passphrase, and returns a seed object used to generate a private key.
     *
     * @param mnemonics An array of mnemonic phrases.
     * @param passphrase A passphrase used to enhance the security of the seed.
     * @return A seed object.
     * @throws [MnemonicLengthException] if the mnemonics or passphrase are invalid.
     */
    @Throws(MnemonicLengthException::class)
    fun createSeed(mnemonics: Array<String>, passphrase: String): Seed

    /**
     * Creates a random seed and a corresponding set of mnemonic phrases.
     *
     * @param passphrase A passphrase used to enhance the security of the seed.
     * @return [SeedWords].
     */
    fun createRandomSeed(passphrase: String? = ""): SeedWords

    /**
     * Creates a private key using the provided properties.
     *
     * @param properties A map containing the properties of the private key.
     *                   The supported properties are:
     *                   - "type": The type of the private key. Use KeyTypes.EC for elliptic curve keys.
     *                   - "seed": The seed used for key generation. Must be a byte array.
     *                   - "curve": The key curve. Use Curve.SECP256K1 for secp256k1 curve.
     * @return A PrivateKey object representing the created private key.
     */
    fun createPrivateKey(properties: Map<String, Any>): PrivateKey
}
