package me.bscal.combatreport

import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.main.KSpigot

val DeathString: String = String.format("%s[%sCombatReport%s] %sClick me or run %s/combatreport %sto see details about your death.",
	KColors.GOLD, KColors.SALMON, KColors.GOLD, KColors.GRAY, KColors.GOLD, KColors.GRAY)

val DeathText = literalText(DeathString) {
	onClickCommand("/combatreport")
}

class CombatReport : KSpigot()
{
	companion object
	{
		lateinit var INSTANCE: CombatReport; private set
	}

	override fun load()
	{
		INSTANCE = this;
	}

	override fun startup()
	{        // Plugin startup logic
		server.pluginManager.registerEvents(LoginListeners(), this)
		server.pluginManager.registerEvents(CombatListener(), this)

		command("combatreport") {
			runs {
				OpenGUI(this.player)
			}
		}
	}
}