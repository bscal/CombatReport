package me.bscal.combatreport

import net.axay.kspigot.chat.KColors
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

const val DEATH_STRING: String = "Death - Damage Received:"
const val HEAL_STRING: String = "Healing Received:"
const val DAMAGE_STRING: String = "Damage Received:"

val DateFormat: SimpleDateFormat = SimpleDateFormat("mm-ss");

val PageOneIcon: ItemStack = itemStack(Material.PAPER) {
	meta() {
		name = "${KColors.SALMON}Current Combat Report"
	}
}
val PageTwoIcon: ItemStack = itemStack(Material.BONE) {
	meta() {
		name = "${KColors.SALMON}Last Deaths Combat Report"
	}
}
val PageOneIconHighlighted: ItemStack = itemStack(Material.PAPER) {
	addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
	meta() {
		name = "${KColors.SALMON}Current Combat Report"
		flag(ItemFlag.HIDE_ENCHANTS)
	}
}
val PageTwoIconHighlighted: ItemStack = itemStack(Material.BONE) {
	addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
	meta() {
		name = "${KColors.SALMON}Last Deaths Combat Report"
		flag(ItemFlag.HIDE_ENCHANTS)
	}
}

val DamageIcon: ItemStack = ItemStack(Material.REDSTONE_BLOCK);
val HealIcon: ItemStack = ItemStack(Material.EMERALD_BLOCK);
val DeathIcon: ItemStack = ItemStack(Material.SKELETON_SKULL);

private fun IconForEntry(player: Player, entry: CombatEntry): ItemStack
{
	val iconString: String
	val material: Material
	if (entry.isDeath)
	{
		iconString = DEATH_STRING
		material = Material.SKELETON_SKULL
	}
	else if (entry.isHeal)
	{
		iconString = HEAL_STRING
		material = Material.EMERALD_BLOCK
	}
	else
	{
		iconString = DAMAGE_STRING
		material = Material.REDSTONE_BLOCK
	}

	return itemStack(material) {
		meta() {
			name = "${KColors.RED}${iconString} ${String.format("%.2f", entry.damage)}"
			addLore() {
				+"Cause: ${entry.type.name}"
				+"From: ${entry.name}"
				+"Time: ${DateFormat.format(Date(entry.time))}"
			}
		}
	}
}

private fun FillInventory(player: Player, page: GUIPageBuilder<ForInventorySixByNine>, queue: ArrayBlockingQueue<CombatEntry>)
{
	var x = 1;
	var y = 5; // Start from top to bottom
	for (entry in queue)
	{
		page.placeholder(SingleInventorySlot(y, x), IconForEntry(player, entry))
		x++;
		if (x > 9)
		{
			x = 1;
			y--;
		}
	}
}

fun OpenGUI(player: Player)
{
	val entries = PlayerEntries[player.uniqueId] ?: return;

	val playerSkull = itemStack(Material.PLAYER_HEAD)
	{
		meta()
		{
			this as SkullMeta
			owningPlayer = player;
			name = "${KColors.GOLD}${player.name}"
		}
	}

	val gui = kSpigotGUI(GUIType.SIX_BY_NINE) {
		title = "Combat Report"
		defaultPage = 0;
		page(0) {
			transitionFrom = PageChangeEffect.SLIDE_HORIZONTALLY;
			transitionTo = PageChangeEffect.SLIDE_HORIZONTALLY;

			placeholder(Slots.RowSixSlotOne, playerSkull);
			placeholder(Slots.RowSixSlotTwo, PageOneIconHighlighted);
			nextPage(Slots.RowSixSlotThree, PageTwoIcon, null, null);

			FillInventory(player, this, entries.currentEntries);
		}

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