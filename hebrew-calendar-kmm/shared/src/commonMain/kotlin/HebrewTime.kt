/**
 * Represents Hebrew time in the fixed calendar system.
 * 
 * Time is measured from the theoretical moment of the molad (moon conjunction).
 * 
 * Components:
 * - dayOfWeek: 1 (Sunday) through 7 (Saturday) — Talmudic convention
 * - hour: 0–23
 * - chalakim: 0–1079 (1080 chalakim = 1 hour)
 * 
 * Reference: Rambam, Hilchot Kiddush HaChodesh VI
 */
data class HebrewTime(
    val dayOfWeek: Int,        // 1=Sunday, 2=Monday, ..., 7=Saturday
    val hour: Int,             // 0–23
    val chalakim: Int          // 0–1079 (parts of an hour)
) {
    companion object {
        const val CHALAKIM_PER_HOUR = 1080
        const val CHALAKIM_PER_DAY = 25920  // 1080 * 24
        
        // Molad Breshit (theoretical origin of the calendar)
        // According to Rambam: Monday, 5 hours, 204 chalakim
        val MOLAD_BRESHIT = HebrewTime(
            dayOfWeek = 2,        // Monday
            hour = 5,
            chalakim = 204
        )
    }

    /**
     * Add chalakim to this time.
     * Properly handles overflow across hours and days.
     */
    fun addChalakim(numChalakim: Long): HebrewTime {
        var totalChalakim = this.chalakim.toLong() + numChalakim
        var hours = this.hour.toLong()
        var days = this.dayOfWeek.toLong()

        // Handle chalakim overflow
        if (totalChalakim >= CHALAKIM_PER_HOUR) {
            hours += totalChalakim / CHALAKIM_PER_HOUR
            totalChalakim %= CHALAKIM_PER_HOUR
        }

        // Handle hour overflow
        if (hours >= 24) {
            days += hours / 24
            hours %= 24
        }

        // Handle day overflow (wrap around 7-day week, but keep range 1–7)
        days = ((days - 1) % 7) + 1

        return HebrewTime(
            dayOfWeek = days.toInt(),
            hour = hours.toInt(),
            chalakim = totalChalakim.toInt()
        )
    }

    /**
     * Check if this time is >= 18 hours (molad zaken threshold).
     */
    fun isMoladZaken(): Boolean {
        return hour >= 18
    }

    /**
     * Format for display (e.g., "Mon 14:23")
     */
    override fun toString(): String {
        val dayNames = listOf("", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        return "${dayNames[dayOfWeek]} ${hour.toString().padStart(2, '0')}:${(chalakim / 45).toString().padStart(2, '0')}"
    }

    /**
     * Equality check
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HebrewTime) return false
        return dayOfWeek == other.dayOfWeek && 
               hour == other.hour && 
               chalakim == other.chalakim
    }

    override fun hashCode(): Int {
        return 31 * (31 * dayOfWeek + hour) + chalakim
    }
}
