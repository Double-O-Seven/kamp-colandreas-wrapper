package ch.leadrian.samp.kamp.colandreaswrapper.entity

import com.netflix.governator.annotations.Configuration
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
internal class ColAndreasObjectRegistryProvider
@Inject
constructor() : Provider<ColAndreasObjectRegistry> {

    @Configuration("kamp.colandreas.object.registry.capacity")
    var registryCapacity: Int = 50000

    private val registry by lazy { ColAndreasObjectRegistry(registryCapacity) }

    override fun get(): ColAndreasObjectRegistry = registry

}