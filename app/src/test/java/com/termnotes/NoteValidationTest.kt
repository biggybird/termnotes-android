package com.termnotes

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteValidationTest {
    @Test
    fun blankTitleAndBlankContentAreInvalid() {
        assertFalse(isValidNote("", ""))
        assertFalse(isValidNote("  ", "\n  "))
    }

    @Test
    fun titleOrContentMakesNoteValid() {
        assertTrue(isValidNote("A title", ""))
        assertTrue(isValidNote("", "Some content"))
    }

    private fun isValidNote(title: String, content: String): Boolean =
        title.isNotBlank() || content.isNotBlank()
}
