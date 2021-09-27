package me.bscal.combatreport

import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.player.PlayerRespawnEvent

class CombatListener : Listener
{

	@EventHandler(priority = EventPriority.MONITOR)
	fun OnPlayerDamagedEvent(event: EntityDamageByEntityEvent)
	{
		val damagee: Entity = event.entity;
		if (damagee is Player)
		{
			val damager: Entity = event.damager;
			val damage: Double = event.damage;
			val cause: DamageCause = event.cause;
			val name : String = if (damager.customName.isNullOrBlank()) damager.name else damager.customName!!;
			val entry: CombatEntry = CombatEntry(name, damage, cause, isDeath = damagee.isDead);
			Bukkit.getLogger().info(entry.toString())

			val playerData = PlayerEntries[damagee.uniqueId];
			val entries = playerData?.currentEntries;
			if (entries != null)
			{
				if (entries.remainingCapacity() == 0)
					entries.remove()

				entries.add(entry);
			}

			if (entry.isDeath)
				playerData?.HandleDeathSwap();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun OnPlayerDamagedByBlockEvent(event: EntityDamageByBlockEvent)
	{
		val damagee: Entity = event.entity;
		if (damagee is Player)
		{
			val damager: Block? = event.damager;
			val damage: Double = event.damage;
			val cause: DamageCause = event.cause;
			val entry: CombatEntry = CombatEntry(damager!!.type.name, damage, cause, isDeath = damagee.isDead);

			val playerData = PlayerEntries[damagee.uniqueId];
			val entries = playerData?.currentEntries;
			if (entries != null)
			{
				if (entries.remainingCapacity() == 0)
					entries.remove()

				entries.add(entry);
			}

			if (entry.isDeath)
				playerData?.HandleDeathSwap();
		}
	}

}