package me.bscal.combatreport

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*

class CombatListener : Listener
{

	@EventHandler(priority = EventPriority.MONITOR)
	fun OnDeath(event: PlayerDeathEvent)
	{
		event.entity.spigot().sendMessage(DeathText)
		PlayerEntries[event.entity.uniqueId]?.HandleDeathSwap();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun OnRegenateHealth(event: EntityRegainHealthEvent)
	{
		if (event.entity is Player)
		{
			val player: Player = event.entity as Player
			val entry = CombatEntry("regeneration", event.amount, event.amount, event.regainReason.name, player.health, isHeal = true)
			AddEntry(player.uniqueId, entry)
			Bukkit.getLogger().info("PlayerHealed: $entry")
		}
	}

	// Called from OnPlayerDamagedByOther instead of registering both events
	private fun OnPlayerDamagedEvent(event: EntityDamageByEntityEvent)
	{
		val damagee: Player = event.entity as Player
		val damager: Entity = event.damager

		// We need to check if we should get the shooter's name or the damager's name
		val sourceName: String = if (damager is Projectile && damager.shooter is Entity)
		{
			val shooter: Entity = damager.shooter as Entity
			if (shooter.customName.isNullOrBlank()) shooter.name else shooter.customName!!
		}
		else if (damager.customName.isNullOrBlank()) damager.name else damager.customName!!

		val entry = CombatEntry(sourceName, event.damage, event.finalDamage, event.cause.name, damagee.health)
		AddEntry(damagee.uniqueId, entry)
		Bukkit.getLogger().info("DamageByEntity: $entry")
	}

	/**
	 * Event used to get all other damages that are not from entities. (Blocks, fire, falling...)
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	fun OnPlayerDamagedByOther(event: EntityDamageEvent)
	{
		if (event.entity is Player)
		{
			if (event is EntityDamageByEntityEvent) return OnPlayerDamagedEvent(event)

			val damagee: Player = event.entity as Player
			val entry = CombatEntry("environment", event.damage, event.finalDamage, event.cause.name, damagee.health)
			AddEntry(damagee.uniqueId, entry);
			Bukkit.getLogger().info("DamagedByOther: $entry")
		}
	}

	private fun AddEntry(uuid: UUID, entry: CombatEntry)
	{
		val playerData = PlayerEntries[uuid];
		val entries = playerData?.currentEntries;
		if (entries != null)
		{
			if (entries.remainingCapacity() == 0) entries.remove()

			entries.add(entry);
		}
	}

}