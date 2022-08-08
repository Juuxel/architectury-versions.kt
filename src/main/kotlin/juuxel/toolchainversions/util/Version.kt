package juuxel.toolchainversions.util

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlin.math.max

/** Extended semantic version with a variable number of components. */
data class Version(val components: List<Int>, val preRelease: String?) : Comparable<Version> {
    override fun compareTo(other: Version): Int {
        // See https://semver.org/
        if (this == other) return 0

        val size = max(components.size, other.components.size)

        for (i in 0 until size) {
            val compA = components.getOrNull(i) ?: 0
            val compB = other.components.getOrNull(i) ?: 0
            val result = compA.compareTo(compB)
            if (result != 0) return result
        }

        // All components are equal, let's check the pre-release

        // If only one has a pre-release
        if (preRelease != null && other.preRelease == null) {
            return -1
        } else if (other.preRelease != null && preRelease == null) {
            return 1
        }

        val preComponentsA = preRelease!!.split(".")
        val preComponentsB = other.preRelease!!.split(".")
        val preSize = max(preComponentsA.size, preComponentsB.size)

        for (i in 0 until preSize) {
            // "A larger set of pre-release fields has a higher precedence than a smaller set,
            // if all of the preceding identifiers are equal."
            val compA = preComponentsA.getOrNull(i) ?: return -1
            val compB = preComponentsB.getOrNull(i) ?: return 1
            val compANum = compA.toIntOrNull()
            val compBNum = compB.toIntOrNull()

            // "Numeric identifiers always have lower precedence than non-numeric identifiers."
            if (compANum != null && compBNum == null) return -1
            if (compANum == null && compBNum != null) return 1

            // Both numeric
            if (compANum != null) {
                val result = compANum.compareTo(compBNum!!)
                if (result != 0) return result
            } else { // Both alphabetic
                val result = compA.compareTo(compB)
                if (result != 0) return result
            }
        }

        return 0 // I guess they must be equal
    }

    override fun toString(): String = buildString {
        append(components.joinToString("."))

        if (preRelease != null) {
            append('-')
            append(preRelease)
        }
    }

    companion object {
        fun parse(version: String): Either<String, Version> {
            // Strip build metadata
            val stripped = version.substringBeforeLast('+')

            // Split into components and pre-release
            val components: List<String>
            val preRelease: String?
            val hyphenIndex = stripped.indexOf('-')

            if (hyphenIndex != -1) {
                components = stripped.substring(0 until hyphenIndex).split('.')
                preRelease = stripped.substring(hyphenIndex + 1)
            } else {
                components = stripped.split('.')
                preRelease = null
            }

            val intComponents: MutableList<Int> = ArrayList(components.size)
            for (component in components) {
                intComponents += component.toIntOrNull() ?: return "Cannot parse '$component' into a number".left()
            }

            return Version(intComponents, preRelease).right()
        }
    }
}

private const val UNKNOWN_VERSION = "N/A"

fun getLatest(versions: Iterable<Version>): String =
    versions.asSequence()
        .sortedDescending()
        .map { it.toString() }
        .firstOrNull() ?: UNKNOWN_VERSION
