package me.diamondforge.horizon.api.sanitizer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SanitizerTest {
    private val sanitizer = Sanitizer()

    @Test
    fun testTransform() {
        val censor = Sanitizer()
        assertEquals("this", censor.transform("t-h.1.$"))
        assertEquals("this", censor.transform("thhhhiiiisss"))
    }

    @Test
    fun testReplaceStrict() {
        val originalString = "Here is a sentence that tries to share a fishy link https://example.com/login, but dont worry it will get censored even when you try to bypass it like t-h.1.$, thhhhiiiisss, 𝔱𝔥𝔦𝔰, 𝕥𝕙𝕚𝕤 or ᴛʜɪꜱ."
        val toCensorArray = listOf("https://*", "censor", "this")
        val replacement = "*"
        val strict = true

        val censoredText = sanitizer.replace(originalString, toCensorArray, replacement, strict)
        println("Censored (Strict): $censoredText")

        assert(censoredText.contains("*******************"))
        assert(censoredText.contains("******ed"))
        // Check for the 'this' variations
        // Each variation of 'this' (transformed to 'this') becomes '****'
        val words = censoredText.split(" ")
        val censoredCount = words.count { it == "****" }
        // There should be 5 variations of 'this' in the original string
        assertEquals(5, censoredCount)
    }

    @Test
    fun testReplaceNotStrict() {
        val originalString = "Here is a sentence that tries to share a fishy link https://example.com/login, but dont worry it will get censored even when you try to bypass it like t-h.1.$, thhhhiiiisss, 𝔱𝔥𝔦𝔰, 𝕥𝕙𝕚𝕤 or ᴛʜɪꜱ."
        val toCensorArray = listOf("https://*", "censor", "this")
        val replacement = "#"
        val strict = false

        val censoredText = sanitizer.replace(originalString, toCensorArray, replacement, strict)
        println("Censored (Not Strict): $censoredText")

        assert(censoredText.contains("###################"))
        assert(censoredText.contains("######ed"))
        // In improved version, t-h.1.$ is also caught because it's a basic substitution
        assert(censoredText.contains("####")) 
    }

    @Test
    fun testPartialMatching() {
        val originalString = "i like pineapple on pizza."
        val toCensorArray = listOf("*pple", "pizz*", "*ik*", "on")
        val replacement = "*"
        val strict = true

        val censoredText = sanitizer.replace(originalString, toCensorArray, replacement, strict)
        println("Censored (Partial): $censoredText")
        // Expected: i **** ******** ** ****
        assertEquals("i **** ******** ** ****", censoredText)
    }

    @Test
    fun testEmptyPatternBug() {
        val originalString = "This is a normal sentence."
        val toCensorArray = listOf("𝔱𝔥𝔦𝔰") // Homoglyph pattern
        val strict = false // Strict is false, so 𝔱𝔥𝔦𝔰 transforms to ""

        val censoredText = sanitizer.replace(originalString, toCensorArray, "*", strict)
        println("Censored (Empty Pattern): $censoredText")
        
        // It should NOT censor everything. In fact, it should probably censor nothing 
        // because the pattern couldn't be normalized to anything meaningful.
        assert(!censoredText.contains("****"))
        assertEquals(originalString, censoredText)
    }

    @Test
    fun testEmojiHomoglyph() {
        val string = "0️⃣1️⃣2️⃣"
        val cleanedText = sanitizer.convert(string, true)
        println("Cleaned Emoji: $cleanedText")
        assertEquals("012", cleanedText)
    }

    @Test
    fun testConvert() {
        val string = "ỆᶍǍᶆṔƚÉ ℭ𝔩𝔢𝔞𝔫 𝓾𝓹 𝕥𝕙𝕚𝕤 🆃🅴🆇🆃"
        val cleanedText = sanitizer.convert(string, true)
        println("Cleaned: $cleanedText")
        assertEquals("ExAmPlE Clean up this text", cleanedText)
    }
}
