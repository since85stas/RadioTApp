package stas.batura.data

enum class ListViewType(type: Int) {

    NUMBER(0),
    YEAR(1),
    MONTH(2),
    FAVORITE(3)
    ;

    companion object {
        private val VALUES = values()

        fun getByValue(value: Int) = VALUES.firstOrNull { it.ordinal == value }
    }

}