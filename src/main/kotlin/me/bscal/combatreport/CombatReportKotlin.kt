package me.bscal.combatreport

import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.main.KSpigot

class CombatReportKotlin : KSpigot()
{
	companion object {
		lateinit var INSTANCE: CombatReportKotlin; private set
	}

	override fun load()
	{
		INSTANCE = this;
	}

	override fun startup()
	{
		// Plugin startup logic
		server.pluginManager.registerEvents(LoginListeners(), this)
		server.pluginManager.registerEvents(CombatListener(), this)

		command("combatreport") {
			runs {
				OpenGUI(this.player)
			}
		}
	}
}