package ch.leadrian.samp.kamp.colandreaswrapper

import ch.leadrian.samp.kamp.core.api.Plugin
import com.google.inject.Module

class ColAndreasPlugin : Plugin() {

    override fun getModules(): List<Module> = listOf(ColAndreasModule())

}