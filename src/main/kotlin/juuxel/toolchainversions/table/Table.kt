package juuxel.toolchainversions.table

private const val UPPER_LEFT = '\u256D'
private const val UPPER_RIGHT = '\u256E'
private const val LOWER_RIGHT = '\u256F'
private const val LOWER_LEFT = '\u2570'
private const val VERTICAL_BAR = '\u2502'
private const val HORIZONTAL_BAR = '\u2500'
private const val LEFT_INTERSECTION = '\u251C'
private const val RIGHT_INTERSECTION = '\u2524'
private const val TOP_INTERSECTION = '\u252C'
private const val BOTTOM_INTERSECTION = '\u2534'
private const val INTERSECTION = '\u253C'

data class Table(val rows: List<Row>) {
    fun display(): String {
        val cellCount = if (rows.isEmpty()) 0 else rows.maxOf { it.cells.size }
        val cellWidths = IntArray(cellCount)
        calculateWidths(cellWidths)

        return buildString {
            append(UPPER_LEFT)
            addBottomTop(TOP_INTERSECTION, cellWidths)
            append(UPPER_RIGHT)
            append('\n')

            for ((i, row) in rows.withIndex()) {
                if (i != 0) {
                    append(LEFT_INTERSECTION)
                    for ((j, width) in cellWidths.withIndex()) {
                        if (j != 0) append(INTERSECTION)
                        repeat(width + 2) { append(HORIZONTAL_BAR) }
                    }
                    append(RIGHT_INTERSECTION)
                    append('\n')
                }

                append(VERTICAL_BAR)
                for ((j, cell) in row.cells.withIndex()) {
                    if (j != 0) append(VERTICAL_BAR)
                    append(' ')
                    append(cell)
                    val widthDifference = cellWidths[j] - cell.length
                    repeat(widthDifference + 1) { append(' ') }
                }
                append(VERTICAL_BAR)
                append('\n')
            }

            append(LOWER_LEFT)
            addBottomTop(BOTTOM_INTERSECTION, cellWidths)
            append(LOWER_RIGHT)
        }
    }

    private fun calculateWidths(widths: IntArray) {
        if (rows.isEmpty()) return

        for (i in widths.indices) {
            widths[i] = rows.maxOf { row -> row.cells.getOrNull(i)?.length ?: 0 }
        }
    }

    private fun StringBuilder.addBottomTop(intersection: Char, cellWidths: IntArray) {
        for ((i, cellWidth) in cellWidths.withIndex()) {
            if (i != 0) append(intersection)
            repeat(cellWidth + 2) { append(HORIZONTAL_BAR) }
        }
    }
}

data class Row(val cells: List<String>)

