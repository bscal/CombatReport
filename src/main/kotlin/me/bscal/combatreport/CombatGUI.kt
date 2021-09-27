package me.bscal.combatreport

import net.axay.kspigot.chat.KColors
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.*
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

val DEATH_STRING: String = "${KColors.LIGHTGRAY}Death: ${KColors.RED}"
val HEAL_STRING: String = "${KColors.LIGHTGRAY}Healing Received: ${KColors.GREEN}"
val DAMAGE_STRING: String = "${KColors.LIGHTGRAY}Damage Received: ${KColors.RED}"

val PageOneIcon: ItemStack = itemStack(Material.PAPER) {
	meta {
		name = "${KColors.SALMON}Current Life's Report"
	}
}
val PageTwoIcon: ItemStack = itemStack(Material.BONE) {
	meta {
		name = "${KColors.SALMON}Last Death's Report"
	}
}
val PageOneIconHighlighted: ItemStack = itemStack(Material.PAPER) {
	addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
	meta {
		name = "${KColors.SALMON}Current Life's Report"
		flag(ItemFlag.HIDE_ENCHANTS)
	}
}
val PageTwoIconHighlighted: ItemStack = itemStack(Material.BONE) {
	addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
	meta {
		name = "${KColors.SALMON}Last Death's Report"
		flag(ItemFlag.HIDE_ENCHANTS)
	}
}

private fun IconForEntry(player: Player, entry: CombatEntry): ItemStack
{
	// ***********************************************************
	// Set values based on damage taken or healing. Super ugly because of formatting and colors
	// I cannot remember if there is a better method for colors. possible TODO printf style?
	val playerHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0
	val iconString: String
	val material: Material
	val healthInfo: String
	val damageInfo: String
	val targetInfo: String
	if (entry.isHeal)
	{
		iconString = HEAL_STRING
		material = Material.EMERALD_BLOCK
		healthInfo = String.format("%.2f",
			playerHealth.coerceAtMost(entry.health + entry.damage))
		damageInfo = "${KColors.LIGHTGRAY}Healed ${KColors.GREEN}${String.format("%.2f", entry.finalDamage)} ${KColors.LIGHTGRAY}health"
		targetInfo = "${KColors.LIGHTGRAY}From ${KColors.GREEN}${entry.source}${KColors.LIGHTGRAY} by ${KColors.GREEN}${entry.cause.lowercase(
			Locale.getDefault()).replace("_", " ")}"
	}
	else
	{
		healthInfo = String.format("%.2f", 0.0.coerceAtLeast(entry.health - entry.damage))
		damageInfo = "${KColors.LIGHTGRAY}Received ${KColors.RED}${String.format("%.2f", entry.finalDamage)}${KColors.LIGHTGRAY} (${KColors.RED}${String.format("%.2f", entry.damage)}${KColors.LIGHTGRAY} raw) damage"
		targetInfo = "${KColors.LIGHTGRAY}From ${KColors.RED}${entry.source}${KColors.LIGHTGRAY} by ${KColors.RED}${entry.cause.lowercase(
			Locale.getDefault()).replace("_", " ")}"
		if (entry.isDeath)
		{
			iconString = DEATH_STRING
			material = Material.SKELETON_SKULL
		}
		else
		{
			iconString = DAMAGE_STRING
			material = Material.REDSTONE_BLOCK
		}
	}

	// ***********************************************************
	// Creates the icon item stack for the entry
	return itemStack(material) {
		amount = 1.coerceAtLeast(ceil(playerHealth).toInt())
		meta {
			name = "$iconString ${String.format("%.2f", entry.health)} -> $healthInfo"
			addLore {
				+damageInfo
				+targetInfo
				+"${KColors.LIGHTGRAY}${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - entry.time)} seconds ago"
			}
		}
	}
}

private fun FillInventory(player: Player, page: GUIPageBuilder<ForInventorySixByNine>, queue: ArrayBlockingQueue<CombatEntry>)
{
	var x = 9;
	var y = 1; // Start from top to bottom

	for (entry in queue)
	{
		queue.last()
		page.placeholder(SingleInventorySlot(y, x), IconForEntry(player, entry))
		x--;
		if (x < 1)
		{
			x = 9;
			y++;
		}
	}
}

fun OpenGUI(player: Player)
{
	val entries = PlayerEntries[player.uniqueId] ?: return;

	val playerSkull = itemStack(Material.PLAYER_HEAD) {
		meta {
			this as SkullMeta
			owningPlayer = player;
			name = "${KColors.GOLD}${player.name}"
		}
	}

	val gui = kSpigotGUI(GUIType.SIX_BY_NINE) {
		title = "Combat Report"
		defaultPage = 0;

		// ***********************************************************
		// Page 0
		page(0) {
			transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY;
			transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY;

			placeholder(Slots.RowSixSlotOne, playerSkull);
			placeholder(Slots.RowSixSlotTwo, PageOneIconHighlighted);
			nextPage(Slots.RowSixSlotThree, PageTwoIcon, null, null);

			FillInventory(player, this, entries.currentEntries);
		}

		// ***********************************************************
		// Page 1
		page(1) {
			transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY;
			transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY;

			placeholder(Slots.RowSixSlotOne, playerSkull);
			previousPage(Slots.RowSixSlotTwo, PageOneIcon, null, null);
			placeholder(Slots.RowSixSlotThree, PageTwoIconHighlighted);

			FillInventory(player, this, entries.lastDeathEntries);
		}
	}
	player.openGUI(gui);
}