# StringCensor

`StringCensor` is a robust Kotlin library designed to normalize strings and censor blacklisted words. It features advanced homoglyph conversion, character substitution handling, and flexible wildcard-based matching to prevent bypasses.

## Features

- **Normalization:** Converts text to a canonical form (lowercase, stripped of non-alphanumeric characters, deduplicated letters).
- **Homoglyph Conversion:** Automatically converts visually similar characters (homoglyphs) and emojis to their Latin equivalents (e.g., `𝔱𝔥𝔦𝔰` -> `this`, `0️⃣` -> `0`).
- **Flexible Censoring:** Supports prefix, suffix, and infix wildcards (e.g., `*word`, `word*`, `*word*`).
- **Custom Replacements:** Ability to use specific replacement characters or a random mix of symbols (`@`, `*`, `#`).
- **Strict/Non-strict Modes:** Toggle homoglyph conversion based on your needs.

## Usage

### Basic Censoring

```kotlin
val censor = StringCensor()
val text = "i like pineapple on pizza."
val blacklist = listOf("*pple", "pizz*", "on")

val censored = censor.replace(text, blacklist, replacement = "*")
// Output: "i **** ******** ** ****"
```

### Homoglyph Handling

The library excels at catching bypass attempts using special characters or homoglyphs.

```kotlin
val censor = StringCensor()
val text = "Don't bypass it like t-h.1.$, thhhhiiiisss, or 𝔱𝔥𝔦𝔰."
val blacklist = listOf("this")

val censored = censor.replace(text, blacklist, replacement = "*", strict = true)
// All variations of 'this' will be censored.
```

### Normalization Only

You can also use the `transform` and `convert` methods independently to clean up strings.

```kotlin
val censor = StringCensor()
val clean = censor.convert("ỆᶍǍᶆṔƚÉ ℭ𝔩𝔢𝔞𝔫", strict = true)
// Output: "ExAmPlE Clean"

val normalized = censor.transform("t-h.1.$")
// Output: "this"
```

## Advanced Configuration

### Random Replacements

If no replacement symbol is provided, the library uses a random mix of `@`, `*`, and `#`.

```kotlin
val censored = censor.replace("badword", listOf("badword"))
// Output: e.g., "@*#@*#@"
```

### Strict Mode

The `strict` parameter (defaulting to `true`) controls whether homoglyphs are converted before matching. Disabling it can be useful if you only want to catch exact matches and basic substitutions.

## License

MIT License
