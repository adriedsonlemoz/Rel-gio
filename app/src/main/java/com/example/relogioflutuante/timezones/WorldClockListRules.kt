package com.example.relogioflutuante.timezones

object WorldClockListRules {
    fun add(current: List<String>, zoneId: String): List<String> =
        if (zoneId in current) current else current + zoneId

    fun remove(current: List<String>, zoneId: String): List<String> =
        current.filterNot { it == zoneId }

    fun move(current: List<String>, fromIndex: Int, direction: Int): List<String> {
        if (fromIndex !in current.indices || direction == 0) return current
        val target = fromIndex + direction
        if (target !in current.indices) return current
        return current.toMutableList().apply {
            val item = removeAt(fromIndex)
            add(target, item)
        }
    }
}
