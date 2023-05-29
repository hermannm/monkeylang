package dev.hermannm.monkeylang

import kotlin.text.isWhitespace

sealed class Token {
    // Tokens with data
    data class Identifier(val name: String) : Token()
    data class Integer(val value: Int) : Token()
    data class StringLiteral(val literal: String) : Token()

    // Keywords
    object Function : Token()
    object Let : Token()
    object If : Token()
    object Else : Token()
    object Return : Token()
    object True : Token()
    object False : Token()

    // Single-character tokens
    object Assign : Token()
    object Plus : Token()
    object Minus : Token()
    object Slash : Token()
    object Asterisk : Token()
    object Bang : Token()
    object LessThan : Token()
    object GreaterThan : Token()
    object Comma : Token()
    object Semicolon : Token()
    object Colon : Token()
    object LeftParen : Token()
    object RightParen : Token()
    object LeftBrace : Token()
    object RightBrace : Token()
    object LeftBracket : Token()
    object RightBracket : Token()

    // Double-character tokens
    object Equals : Token()
    object NotEquals : Token()

    object EndOfFile : Token()
    object Illegal : Token()
}

class Lexer(private val input: String) {
    private var currentCharacter: Char = Char.MIN_VALUE
    private var currentPosition: Int = 0
    private var nextPosition: Int = 0

    init {
        readCharacter()
    }

    /** @throws IllegalStateException If the input is invalid. */
    fun nextToken(): Token {
        skipWhitespace()

        val token = when (currentCharacter) {
            '+' -> Token.Plus
            '-' -> Token.Minus
            '/' -> Token.Slash
            '*' -> Token.Asterisk
            '<' -> Token.LessThan
            '>' -> Token.GreaterThan
            ',' -> Token.Comma
            ';' -> Token.Semicolon
            ':' -> Token.Colon
            '(' -> Token.LeftParen
            ')' -> Token.RightParen
            '{' -> Token.LeftBrace
            '}' -> Token.RightBrace
            '[' -> Token.LeftBracket
            ']' -> Token.RightBracket
            '=' -> {
                if (peekCharacter() == '=') {
                    readCharacter()
                    Token.Equals
                } else {
                    Token.Assign
                }
            }
            '!' -> {
                if (peekCharacter() == '=') {
                    readCharacter()
                    Token.NotEquals
                } else {
                    Token.Bang
                }
            }
            in 'a'..'z', in 'A'..'Z', '_' -> {
                return when (val identifier = readIdentifier()) {
                    "fn" -> Token.Function
                    "let" -> Token.Let
                    "if" -> Token.If
                    "else" -> Token.Else
                    "return" -> Token.Return
                    "true" -> Token.True
                    "false" -> Token.False
                    else -> Token.Identifier(identifier)
                }
            }
            in '0'..'9' -> {
                val integer = readInteger()
                return Token.Integer(integer)
            }
            '"' -> {
                val string = readString()
                return Token.StringLiteral(string)
            }
            Char.MIN_VALUE -> Token.EndOfFile
            else -> Token.Illegal
        }

        readCharacter()
        return token
    }

    private fun readCharacter() {
        currentCharacter = if (nextPosition < input.length) {
            input[nextPosition]
        } else {
            Char.MIN_VALUE
        }

        currentPosition = nextPosition
        nextPosition++
    }

    private fun peekCharacter(): Char {
        return if (nextPosition < input.length) {
            input[nextPosition]
        } else {
            Char.MIN_VALUE
        }
    }

    private fun readIdentifier(): String {
        val startPosition = currentPosition
        while (currentCharacter.isLetter() || currentCharacter == '_') {
            readCharacter()
        }
        return input.substring(startPosition, currentPosition)
    }

    /** @throws IllegalStateException If parsing integer from current position in input failed. */
    private fun readInteger(): Int {
        val startPosition = currentPosition
        while (currentCharacter.isDigit()) {
            readCharacter()
        }

        val string = input.substring(startPosition, currentPosition)

        return try { string.toInt() } catch (err: Exception) {
            throw IllegalStateException("Failed to read integer '$string' from input", err)
        }
    }

    /** @throws IllegalStateException If input ended before closing quote for the string. */
    private fun readString(): String {
        val startPosition = currentPosition + 1

        while (true) {
            readCharacter()
            when (currentCharacter) {
                '"' -> break
                Char.MIN_VALUE -> throw IllegalStateException(
                    "Input ended before closing quote of string",
                )
            }
        }

        // Skip closing quote
        readCharacter()

        return input.substring(startPosition, currentPosition)
    }

    private fun skipWhitespace() {
        while (currentCharacter.isWhitespace()) {
            readCharacter()
        }
    }
}
