package me.bscal.combatreport

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

val PlayerEntries: HashMap<UUID, CombatData> = HashMap()

class LoginListeners : Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	fun OnLogin(event: PlayerLoginEvent)
	{
		PlayerEntries[event.player.uniqueId] = CombatData();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun OnLogout(event: PlayerQuitEvent)
	{
		PlayerEntries.remove(event.player.uniqueId);
	}
}