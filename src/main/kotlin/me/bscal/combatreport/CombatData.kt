package me.bscal.combatreport

import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import java.util.concurrent.ArrayBlockingQueue

const val MAX_PAGE_SIZE: Int = 5 * 9

data class CombatData(var currentEntries: ArrayBlockingQueue<CombatEntry> = ArrayBlockingQueue(MAX_PAGE_SIZE),
	var lastDeathEntries: ArrayBlockingQueue<CombatEntry> = ArrayBlockingQueue(MAX_PAGE_SIZE))
{
	fun HandleDeathSwap()
	{
		lastDeathEntries = currentEntries
		currentEntries = ArrayBlockingQueue(MAX_PAGE_SIZE)
	}
}

data class CombatEntry(val name: String, val damage: Double, val type: DamageCause, val isHeal: Boolean = false,
	val time: Long = System.currentTimeMillis(), val isDeath: Boolean = false)
