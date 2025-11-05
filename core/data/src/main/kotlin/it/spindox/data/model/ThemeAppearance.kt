package it.spindox.data.model

enum class ThemeAppearance(val value: Int) {
    AUTO(0),
    LIGHT(1),
    DARK(2);

    companion object {
        private val values = entries.toTypedArray()
        fun getByValue(value: Int?) = values.firstOrNull { it.value == value } ?: AUTO
    }
}