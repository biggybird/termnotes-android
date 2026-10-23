package com.termnotes.ui

data class ParsedCommand(val action: Action, val argument: String = "", val raw: String = "") {
    enum class Action { HELP, LIST, NEW, THEME, CLEAR, SEARCH, UNKNOWN, EMPTY }
}

object CommandProcessor {
    fun parse(input: String): ParsedCommand {
        val raw = input.trim()
        if (raw.isEmpty()) return ParsedCommand(ParsedCommand.Action.EMPTY, raw = raw)
        val parts = raw.split(Regex("\\s+"), limit = 2)
        val command = parts[0].lowercase()
        val arg = parts.getOrElse(1) { "" }
        val action = when (command) {
            "help", ":help" -> ParsedCommand.Action.HELP
            "list" -> ParsedCommand.Action.LIST
            "new", ":new" -> ParsedCommand.Action.NEW
            ":theme" -> ParsedCommand.Action.THEME
            "clear", ":clear" -> ParsedCommand.Action.CLEAR
            "search" -> if (arg.isBlank()) ParsedCommand.Action.UNKNOWN else ParsedCommand.Action.SEARCH
            else -> ParsedCommand.Action.UNKNOWN
        }
        return ParsedCommand(action, arg, raw)
    }
}
