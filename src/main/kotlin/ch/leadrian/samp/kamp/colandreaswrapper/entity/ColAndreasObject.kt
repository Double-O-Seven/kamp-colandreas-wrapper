package ch.leadrian.samp.kamp.colandreaswrapper.entity

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasNativeFunctions
import ch.leadrian.samp.kamp.colandreaswrapper.entity.id.ColAndreasObjectIndex
import ch.leadrian.samp.kamp.core.api.data.Vector3D
import ch.leadrian.samp.kamp.core.api.entity.AbstractDestroyable
import ch.leadrian.samp.kamp.core.api.entity.Entity

class ColAndreasObject internal constructor(
        private val nativeFunctions: ColAndreasNativeFunctions,
        override val id: ColAndreasObjectIndex
) : Entity<ColAndreasObjectIndex>, AbstractDestroyable() {

    fun setCoordinates(coordinates: Vector3D) {
        TODO()
    }

    fun setRotation(rotation: Vector3D) {
        TODO()
    }

    override fun onDestroy() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}