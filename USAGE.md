# Usage

## Setup

Instantiate `Sanitizer` once and reuse it:

```kotlin
val sanitizer = Sanitizer()
```

---

## replace

The main method. Takes a string, a list of patterns to censor, an optional replacement symbol, and a strict flag.

```kotlin
fun replace(
    originalString: String,
    toCensorArray: List<String>,
    replacement: String? = null,
    strict: Boolean = true
): String
```

Basic example:

```kotlin
val result = sanitizer.replace(
    originalString = "i like pineapple on pizza.",
    toCensorArray = listOf("*pple", "pizz*", "on"),
    replacement = "*"
)
// "i **** ******** ** ****"
```

### Wildcards

Patterns support `*` at the start and/or end:

| Pattern   | Matches                          | What gets censored         |
|-----------|----------------------------------|----------------------------|
| `word`    | exact word (after normalization) | only the matched part      |
| `word*`   | word as a prefix                 | the match and everything after |
| `*word`   | word as a suffix                 | everything up to the match |
| `*word*`  | word anywhere                    | the entire word            |

### Strict mode

When `strict = true` (default), homoglyphs and special Unicode characters are converted to their Latin equivalents before matching. This catches bypass attempts like `𝔱𝔥𝔦𝔰`, `ᴛʜɪꜱ`, or `t-h.1.$`.

```kotlin
val result = sanitizer.replace(
    originalString = "bypass like t-h.1.$, thhhhiiiisss, 𝔱𝔥𝔦𝔰",
    toCensorArray = listOf("this"),
    replacement = "*",
    strict = true
)
// all three variations become "****"
```

Set `strict = false` if you only want basic substitution matching and no homoglyph conversion.

### Replacement symbol

Pass a string to use as the replacement character. If omitted (`null`), a random mix of `@`, `*` and `#` is used per character.

```kotlin
sanitizer.replace("some badword here", listOf("badword"), replacement = "#")
// "some ####### here"

sanitizer.replace("some badword here", listOf("badword"))
// "some @*#@*#@ here"
```

---

## convert

Converts homoglyphs and special characters to their Latin equivalents. Returns an empty string for null input, or the original string if `strict = false`.

```kotlin
fun convert(str: String?, strict: Boolean): String
```

```kotlin
sanitizer.convert("ỆᶍǍᶆṔƚÉ ℭ𝔩𝔢𝔞𝔫 𝓾𝓹 𝕥𝕙𝕚𝕤 🆃🅴🆇🆃", strict = true)
// "ExAmPlE Clean up this text"

sanitizer.convert("0️⃣1️⃣2️⃣", strict = true)
// "012"
```

---

## transform

Normalizes a string for comparison: lowercases it, applies common character substitutions (`$` -> `s`, `1` -> `i`, etc.), strips non-alphanumeric characters, and deduplicates repeated letters.

```kotlin
fun transform(input: String): String
```

```kotlin
sanitizer.transform("t-h.1.$")      // "this"
sanitizer.transform("thhhhiiiisss") // "this"
sanitizer.transform("H3LL0")        // "helo"
```

Character substitutions applied:

| Input | Output |
|-------|--------|
| `0`   | `o`    |
| `1`   | `i`    |
| `2`   | `z`    |
| `3`   | `e`    |
| `4`   | `a`    |
| `5`   | `s`    |
| `6`   | `b`    |
| `7`   | `t`    |
| `8`   | `b`    |
| `9`   | `g`    |
| `$`   | `s`    |
| `@`   | `a`    |
| `!`   | `i`    |
| `+`   | `t`    |

---

## replacement

Generates a replacement string of a given length. Useful if you need to build censored output manually.

```kotlin
fun replacement(length: Int, symbol: String?): String
```

```kotlin
sanitizer.replacement(5, "*")  // "*****"
sanitizer.replacement(5, null) // e.g. "@*#@*"
```
