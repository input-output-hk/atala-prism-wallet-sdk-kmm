package io.iohk.atala.prism.walletsdk.castor.did

import io.iohk.atala.prism.walletsdk.castor.antlrgrammar.DIDAbnfLexer
import io.iohk.atala.prism.walletsdk.castor.antlrgrammar.DIDAbnfParser
import io.iohk.atala.prism.walletsdk.domain.models.DID
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.ParseTreeWalker
import kotlin.jvm.Throws

object DIDParser {

    @Throws(InvalidDIDStringError::class)
    fun parse(didString: String): DID {
        val inputStream = CharStreams.fromString(didString)
        val lexer = DIDAbnfLexer(inputStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = DIDAbnfParser(tokenStream)

        parser.errorHandler = ErrorStrategy()

        val context = parser.did()
        val listener = DIDParserListener()
        ParseTreeWalker().walk(listener, context as ParseTree)

        val scheme = listener.scheme ?: throw InvalidDIDStringError("Invalid DID string, missing scheme")
        val methodName = listener.methodName ?: throw InvalidDIDStringError("Invalid DID string, missing method name")
        val methodId = listener.methodId ?: throw InvalidDIDStringError("Invalid DID string, missing method ID")

        return DID(scheme, methodName, methodId)
    }
}