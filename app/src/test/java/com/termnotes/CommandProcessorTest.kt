package com.termnotes

import com.termnotes.ui.CommandProcessor
import com.termnotes.ui.ParsedCommand
import org.junit.Assert.assertEquals
import org.junit.Test

class CommandProcessorTest {
    @Test fun helpAndAliasesAreRecognized() {
        assertEquals(ParsedCommand.Action.HELP, CommandProcessor.parse(":help").action)
        assertEquals(ParsedCommand.Action.NEW, CommandProcessor.parse(":new").action)
        assertEquals(ParsedCommand.Action.CLEAR, CommandProcessor.parse(":clear").action)
        assertEquals(ParsedCommand.Action.THEME, CommandProcessor.parse(":theme").action)
    }

    @Test fun searchPreservesMultiWordQuery() {
        val parsed = CommandProcessor.parse("search meeting notes")
        assertEquals(ParsedCommand.Action.SEARCH, parsed.action)
        assertEquals("meeting notes", parsed.argument)
    }

    @Test fun unknownAndEmptyCommandsAreHandled() {
        assertEquals(ParsedCommand.Action.UNKNOWN, CommandProcessor.parse("delete everything").action)
        assertEquals(ParsedCommand.Action.EMPTY, CommandProcessor.parse("   ").action)
    }
}
