package com.example.relogioflutuante.timezones

import java.text.Normalizer

object WorldClockCatalog {
    val entries: List<WorldClockEntry> = listOf(
        WorldClockEntry("America/Sao_Paulo", "Brasília / São Paulo", "Brasil"),
        WorldClockEntry("America/Fortaleza", "Fortaleza", "Brasil"),
        WorldClockEntry("America/Manaus", "Manaus", "Brasil"),
        WorldClockEntry("America/Rio_Branco", "Rio Branco", "Brasil"),
        WorldClockEntry("UTC", "UTC", "Tempo universal"),
        WorldClockEntry("Europe/Lisbon", "Lisboa", "Portugal"),
        WorldClockEntry("Europe/London", "Londres", "Reino Unido"),
        WorldClockEntry("Europe/Madrid", "Madri", "Espanha"),
        WorldClockEntry("Europe/Paris", "Paris", "França"),
        WorldClockEntry("Europe/Berlin", "Berlim", "Alemanha"),
        WorldClockEntry("Europe/Rome", "Roma", "Itália"),
        WorldClockEntry("Europe/Kyiv", "Kyiv", "Ucrânia"),
        WorldClockEntry("Europe/Moscow", "Moscou", "Rússia"),
        WorldClockEntry("America/New_York", "Nova York", "Estados Unidos"),
        WorldClockEntry("America/Chicago", "Chicago", "Estados Unidos"),
        WorldClockEntry("America/Denver", "Denver", "Estados Unidos"),
        WorldClockEntry("America/Los_Angeles", "Los Angeles", "Estados Unidos"),
        WorldClockEntry("America/Toronto", "Toronto", "Canadá"),
        WorldClockEntry("America/Mexico_City", "Cidade do México", "México"),
        WorldClockEntry("America/Bogota", "Bogotá", "Colômbia"),
        WorldClockEntry("America/Lima", "Lima", "Peru"),
        WorldClockEntry("America/Santiago", "Santiago", "Chile"),
        WorldClockEntry("America/Argentina/Buenos_Aires", "Buenos Aires", "Argentina"),
        WorldClockEntry("Asia/Tokyo", "Tóquio", "Japão"),
        WorldClockEntry("Asia/Seoul", "Seul", "Coreia do Sul"),
        WorldClockEntry("Asia/Shanghai", "Xangai", "China"),
        WorldClockEntry("Asia/Hong_Kong", "Hong Kong", "China"),
        WorldClockEntry("Asia/Singapore", "Singapura", "Singapura"),
        WorldClockEntry("Asia/Kolkata", "Nova Délhi / Kolkata", "Índia"),
        WorldClockEntry("Asia/Dubai", "Dubai", "Emirados Árabes Unidos"),
        WorldClockEntry("Asia/Jerusalem", "Jerusalém", "Israel"),
        WorldClockEntry("Asia/Bangkok", "Bangkok", "Tailândia"),
        WorldClockEntry("Australia/Sydney", "Sydney", "Austrália"),
        WorldClockEntry("Pacific/Auckland", "Auckland", "Nova Zelândia"),
        WorldClockEntry("Africa/Cairo", "Cairo", "Egito"),
        WorldClockEntry("Africa/Johannesburg", "Joanesburgo", "África do Sul")
    )

    private val byId = entries.associateBy(WorldClockEntry::zoneId)

    fun find(zoneId: String): WorldClockEntry? = byId[zoneId]

    fun search(query: String): List<WorldClockEntry> {
        val normalized = normalize(query)
        if (normalized.isEmpty()) return entries
        return entries.filter { entry ->
            normalize(entry.city).contains(normalized) ||
                normalize(entry.country).contains(normalized) ||
                normalize(entry.zoneId).contains(normalized)
        }
    }

    private fun normalize(value: String): String = Normalizer
        .normalize(value.trim(), Normalizer.Form.NFD)
        .replace(COMBINING_MARKS, "")
        .lowercase()

    private val COMBINING_MARKS = "\\p{M}+".toRegex()
}
