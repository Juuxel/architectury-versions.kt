package juuxel.toolchainversions.table

@DslMarker
@Retention(AnnotationRetention.SOURCE)
private annotation class TableDsl

inline fun table(block: TableBuilder.() -> Unit): Table =
    TableBuilder().apply(block).build()

@TableDsl
class TableBuilder {
    private val rows: MutableList<Row> = ArrayList()

    inline fun row(block: RowBuilder.() -> Unit) =
        row(RowBuilder().apply(block).build())

    fun row(row: Row) {
        rows += row
    }

    fun build(): Table = Table(rows)
}

@TableDsl
class RowBuilder {
    private val cells: MutableList<String> = ArrayList()

    fun cell(content: String) {
        cells += content
    }

    fun build(): Row = Row(cells)
}
