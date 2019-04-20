package ch.leadrian.samp.kamp.colandreaswrapper

import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObjectRegistry
import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObjectRegistryProvider
import ch.leadrian.samp.kamp.core.api.inject.KampModule

class ColAndreasModule : KampModule() {

    override fun configure() {
        bind(ColAndreasObjectRegistry::class.java).toProvider(ColAndreasObjectRegistryProvider::class.java)
    }

}