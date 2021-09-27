package me.bscal.combatreport

import java.util.concurrent.ArrayBlockingQueue

const val MAX_PAGE_SIZE: Int = 5 * 9

data class CombatData(var currentEntries: ArrayBlockingQueue<CombatEntry> = ArrayBlockingQueue(MAX_PAGE_SIZE),
	var lastDeathEntries: ArrayBlockingQueue<CombatEntry> = ArrayBlockingQueue(MAX_PAGE_SIZE))
{
	fun HandleDeathSwap()
	{
		currentEntries.last().isDeath = true
		lastDeathEntries = currentEntries
		currentEntries = ArrayBlockingQueue(MAX_PAGE_SIZE)
	}
}

data class CombatEntry(val source: String, val damage: Double, val finalDamage: Double, val cause: String, val health: Double,
	val isHeal: Boolean = false, var isDeath: Boolean = false, val time: Long = System.currentTimeMillis())
