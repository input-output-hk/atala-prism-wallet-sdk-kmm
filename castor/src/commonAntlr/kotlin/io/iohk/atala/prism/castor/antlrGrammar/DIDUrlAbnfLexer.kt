package io.iohk.atala.prism.castor.antlrGrammar

import com.strumenta.kotlinmultiplatform.asCharArray
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.dfa.DFA
import org.antlr.v4.kotlinruntime.Lexer
import org.antlr.v4.kotlinruntime.Vocabulary
import org.antlr.v4.kotlinruntime.VocabularyImpl
import org.antlr.v4.kotlinruntime.atn.ATN
import org.antlr.v4.kotlinruntime.atn.ATNDeserializer
import org.antlr.v4.kotlinruntime.atn.LexerATNSimulator
import org.antlr.v4.kotlinruntime.atn.PredictionContextCache
import org.antlr.v4.kotlinruntime.ThreadLocal

open class DIDUrlAbnfLexer(val input: CharStream) : Lexer(input) {

    override val ruleNames: Array<String>?
        get() = Rules.values().map { it.name }.toTypedArray()

    override val grammarFileName: String
        get() = "DIDUrlAbnf.g4"

    override val atn: ATN
        get() = DIDUrlAbnfLexer.Companion.ATN

    override val vocabulary: Vocabulary
        get() = DIDUrlAbnfLexer.Companion.VOCABULARY

    @ThreadLocal
    companion object {
        val decisionToDFA: Array<DFA>
        val sharedContextCache = PredictionContextCache()

        private val LITERAL_NAMES: List<String?> = listOf(
            null, "'/'", "'?'",
            "'#'", "'&'", "'='",
            null, null, null, null,
            "'%'", "'-'", "'.'",
            "':'", "'_'"
        )
        private val SYMBOLIC_NAMES: List<String?> = listOf(
            null, null, null, null,
            null, null, "SCHEMA",
            "ALPHA", "DIGIT", "PCT_ENCODED",
            "PERCENT", "DASH",
            "PERIOD", "COLON",
            "UNDERSCORE", "HEX",
            "STRING"
        )

        val VOCABULARY = VocabularyImpl(LITERAL_NAMES.toTypedArray(), SYMBOLIC_NAMES.toTypedArray())

        val tokenNames: Array<String?> = Array<String?>(SYMBOLIC_NAMES.size) {
            var el = VOCABULARY.getLiteralName(it)
            if (el == null) {
                el = VOCABULARY.getSymbolicName(it)
            }

            if (el == null) {
                el = "<INVALID>"
            }
            el
        }

        private const val serializedATN: String =
            "\u0003\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\u0002\u0012\u0067\u0008\u0001\u0004\u0002\u0009\u0002\u0004\u0003\u0009\u0003\u0004\u0004\u0009\u0004\u0004\u0005\u0009\u0005\u0004\u0006\u0009\u0006\u0004\u0007\u0009\u0007\u0004\u0008\u0009\u0008\u0004\u0009\u0009\u0009\u0004\u000a\u0009\u000a\u0004\u000b\u0009\u000b\u0004\u000c\u0009\u000c\u0004\u000d\u0009\u000d\u0004\u000e\u0009\u000e\u0004\u000f\u0009\u000f\u0004\u0010\u0009\u0010\u0004\u0011\u0009\u0011\u0004\u0012\u0009\u0012\u0004\u0013\u0009\u0013\u0004\u0014\u0009\u0014\u0004\u0015\u0009\u0015\u0003\u0002\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0003\u0008\u0003\u0008\u0003\u0009\u0003\u0009\u0003\u0009\u0003\u0009\u0003\u000a\u0003\u000a\u0003\u000b\u0003\u000b\u0003\u000c\u0003\u000c\u0005\u000c\u0044\u000a\u000c\u0003\u000d\u0003\u000d\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000e\u0003\u000f\u0003\u000f\u0003\u0010\u0003\u0010\u0003\u0011\u0003\u0011\u0003\u0012\u0003\u0012\u0003\u0013\u0003\u0013\u0003\u0014\u0003\u0014\u0003\u0014\u0006\u0014\u0059\u000a\u0014\u000d\u0014\u000e\u0014\u005a\u0003\u0015\u0003\u0015\u0005\u0015\u005f\u000a\u0015\u0003\u0015\u0003\u0015\u0007\u0015\u0063\u000a\u0015\u000c\u0015\u000e\u0015\u0066\u000b\u0015\u0002\u0002\u0016\u0003\u0003\u0005\u0004\u0007\u0005\u0009\u0006\u000b\u0007\u000d\u0002\u000f\u0002\u0011\u0008\u0013\u0002\u0015\u0002\u0017\u0009\u0019\u000a\u001b\u000b\u001d\u000c\u001f\u000d\u0021\u000e\u0023\u000f\u0025\u0010\u0027\u0011\u0029\u0012\u0003\u0002\u000a\u0004\u0002\u0046\u0046\u0066\u0066\u0004\u0002\u004b\u004b\u006b\u006b\u0003\u0002\u0063\u007c\u0003\u0002\u0043\u005c\u0003\u0002\u0032\u003b\u0005\u0002\u0032\u003b\u0043\u0048\u0063\u0068\u0006\u0002\u0032\u003b\u0043\u005c\u0063\u007c\u0080\u0080\u0007\u0002\u002d\u002d\u002f\u0030\u0032\u003b\u0043\u005c\u0063\u007c\u0002\u0067\u0002\u0003\u0003\u0002\u0002\u0002\u0002\u0005\u0003\u0002\u0002\u0002\u0002\u0007\u0003\u0002\u0002\u0002\u0002\u0009\u0003\u0002\u0002\u0002\u0002\u000b\u0003\u0002\u0002\u0002\u0002\u0011\u0003\u0002\u0002\u0002\u0002\u0017\u0003\u0002\u0002\u0002\u0002\u0019\u0003\u0002\u0002\u0002\u0002\u001b\u0003\u0002\u0002\u0002\u0002\u001d\u0003\u0002\u0002\u0002\u0002\u001f\u0003\u0002\u0002\u0002\u0002\u0021\u0003\u0002\u0002\u0002\u0002\u0023\u0003\u0002\u0002\u0002\u0002\u0025\u0003\u0002\u0002\u0002\u0002\u0027\u0003\u0002\u0002\u0002\u0002\u0029\u0003\u0002\u0002\u0002\u0003\u002b\u0003\u0002\u0002\u0002\u0005\u002d\u0003\u0002\u0002\u0002\u0007\u002f\u0003\u0002\u0002\u0002\u0009\u0031\u0003\u0002\u0002\u0002\u000b\u0033\u0003\u0002\u0002\u0002\u000d\u0035\u0003\u0002\u0002\u0002\u000f\u0037\u0003\u0002\u0002\u0002\u0011\u0039\u0003\u0002\u0002\u0002\u0013\u003d\u0003\u0002\u0002\u0002\u0015\u003f\u0003\u0002\u0002\u0002\u0017\u0043\u0003\u0002\u0002\u0002\u0019\u0045\u0003\u0002\u0002\u0002\u001b\u0047\u0003\u0002\u0002\u0002\u001d\u004b\u0003\u0002\u0002\u0002\u001f\u004d\u0003\u0002\u0002\u0002\u0021\u004f\u0003\u0002\u0002\u0002\u0023\u0051\u0003\u0002\u0002\u0002\u0025\u0053\u0003\u0002\u0002\u0002\u0027\u0058\u0003\u0002\u0002\u0002\u0029\u005e\u0003\u0002\u0002\u0002\u002b\u002c\u0007\u0031\u0002\u0002\u002c\u0004\u0003\u0002\u0002\u0002\u002d\u002e\u0007\u0041\u0002\u0002\u002e\u0006\u0003\u0002\u0002\u0002\u002f\u0030\u0007\u0025\u0002\u0002\u0030\u0008\u0003\u0002\u0002\u0002\u0031\u0032\u0007\u0028\u0002\u0002\u0032\u000a\u0003\u0002\u0002\u0002\u0033\u0034\u0007\u003f\u0002\u0002\u0034\u000c\u0003\u0002\u0002\u0002\u0035\u0036\u0009\u0002\u0002\u0002\u0036\u000e\u0003\u0002\u0002\u0002\u0037\u0038\u0009\u0003\u0002\u0002\u0038\u0010\u0003\u0002\u0002\u0002\u0039\u003a\u0005\u000d\u0007\u0002\u003a\u003b\u0005\u000f\u0008\u0002\u003b\u003c\u0005\u000d\u0007\u0002\u003c\u0012\u0003\u0002\u0002\u0002\u003d\u003e\u0009\u0004\u0002\u0002\u003e\u0014\u0003\u0002\u0002\u0002\u003f\u0040\u0009\u0005\u0002\u0002\u0040\u0016\u0003\u0002\u0002\u0002\u0041\u0044\u0005\u0013\u000a\u0002\u0042\u0044\u0005\u0015\u000b\u0002\u0043\u0041\u0003\u0002\u0002\u0002\u0043\u0042\u0003\u0002\u0002\u0002\u0044\u0018\u0003\u0002\u0002\u0002\u0045\u0046\u0009\u0006\u0002\u0002\u0046\u001a\u0003\u0002\u0002\u0002\u0047\u0048\u0005\u001d\u000f\u0002\u0048\u0049\u0005\u0027\u0014\u0002\u0049\u004a\u0005\u0027\u0014\u0002\u004a\u001c\u0003\u0002\u0002\u0002\u004b\u004c\u0007\u0027\u0002\u0002\u004c\u001e\u0003\u0002\u0002\u0002\u004d\u004e\u0007\u002f\u0002\u0002\u004e\u0020\u0003\u0002\u0002\u0002\u004f\u0050\u0007\u0030\u0002\u0002\u0050\u0022\u0003\u0002\u0002\u0002\u0051\u0052\u0007\u003c\u0002\u0002\u0052\u0024\u0003\u0002\u0002\u0002\u0053\u0054\u0007\u0061\u0002\u0002\u0054\u0026\u0003\u0002\u0002\u0002\u0055\u0056\u0007\u0027\u0002\u0002\u0056\u0057\u0009\u0007\u0002\u0002\u0057\u0059\u0009\u0007\u0002\u0002\u0058\u0055\u0003\u0002\u0002\u0002\u0059\u005a\u0003\u0002\u0002\u0002\u005a\u0058\u0003\u0002\u0002\u0002\u005a\u005b\u0003\u0002\u0002\u0002\u005b\u0028\u0003\u0002\u0002\u0002\u005c\u005f\u0009\u0008\u0002\u0002\u005d\u005f\u0005\u0027\u0014\u0002\u005e\u005c\u0003\u0002\u0002\u0002\u005e\u005d\u0003\u0002\u0002\u0002\u005f\u0064\u0003\u0002\u0002\u0002\u0060\u0063\u0009\u0009\u0002\u0002\u0061\u0063\u0005\u0027\u0014\u0002\u0062\u0060\u0003\u0002\u0002\u0002\u0062\u0061\u0003\u0002\u0002\u0002\u0063\u0066\u0003\u0002\u0002\u0002\u0064\u0062\u0003\u0002\u0002\u0002\u0064\u0065\u0003\u0002\u0002\u0002\u0065\u002a\u0003\u0002\u0002\u0002\u0066\u0064\u0003\u0002\u0002\u0002\u0008\u0002\u0043\u005a\u005e\u0062\u0064\u0002"

        val ATN = ATNDeserializer().deserialize(serializedATN.asCharArray())

        init {
            decisionToDFA = Array<DFA>(ATN.numberOfDecisions, {
                DFA(ATN.getDecisionState(it)!!, it)
            })
        }
    }

    enum class Tokens(val id: Int) {
        T__0(1),
        T__1(2),
        T__2(3),
        T__3(4),
        T__4(5),
        SCHEMA(6),
        ALPHA(7),
        DIGIT(8),
        PCT_ENCODED(9),
        PERCENT(10),
        DASH(11),
        PERIOD(12),
        COLON(13),
        UNDERSCORE(14),
        HEX(15),
        STRING(16)
    }

    enum class Channels(val id: Int) {
        DEFAULT_TOKEN_CHANNEL(0),
        HIDDEN(1),
    }

    override val channelNames = Channels.values().map(Channels::name).toTypedArray()

    enum class Modes(val id: Int) {
        DEFAULT_MODE(0),
    }

    enum class Rules {
        T__0,
        T__1,
        T__2,
        T__3,
        T__4,
        D,
        I,
        SCHEMA,
        LOWERCASE,
        UPPERCASE,
        ALPHA,
        DIGIT,
        PCT_ENCODED,
        PERCENT,
        DASH,
        PERIOD,
        COLON,
        UNDERSCORE,
        HEX,
        STRING
    }

    init {
        this.interpreter = LexerATNSimulator(this, ATN, decisionToDFA as Array<DFA?>, sharedContextCache)
    }
}
