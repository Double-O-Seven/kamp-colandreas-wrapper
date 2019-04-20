package ch.leadrian.samp.kamp.colandreaswrapper.entity

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasNativeFunctions
import ch.leadrian.samp.kamp.colandreaswrapper.entity.id.ColAndreasObjectIndex
import ch.leadrian.samp.kamp.core.api.base.HasModelId
import ch.leadrian.samp.kamp.core.api.data.Vector3D
import ch.leadrian.samp.kamp.core.api.entity.AbstractDestroyable
import ch.leadrian.samp.kamp.core.api.entity.Entity
import ch.leadrian.samp.kamp.core.api.entity.requireNotDestroyed

class ColAndreasObject internal constructor(
        override val modelId: Int,
        coordinates: Vector3D,
        rotation: Vector3D,
        val isManaged: Boolean,
        private val nativeFunctions: ColAndreasNativeFunctions
) : Entity<ColAndreasObjectIndex>, AbstractDestroyable(), HasModelId {

    override val id: ColAndreasObjectIndex
        get() = requireNotDestroyed { field }

    var coordinates: Vector3D = coordinates.toVector3D()
        set(value) {
            nativeFunctions.setObjectPos(index = id.value, x = value.x, y = value.y, z = value.z)
            field = value.toVector3D()
        }

    var rotation: Vector3D = rotation.toVector3D()
        set(value) {
            nativeFunctions.setObjectRot(index = id.value, rx = value.x, ry = value.y, rz = value.z)
            field = value.toVector3D()
        }

    init {
        val index = nativeFunctions.createObject(
                modelid = modelId,
                x = coordinates.x,
                y = coordinates.y,
                z = coordinates.z,
                rx = rotation.x,
                ry = rotation.y,
                rz = rotation.z,
                add = isManaged
        )
        id = ColAndreasObjectIndex.valueOf(index)
    }

    override fun onDestroy() {
        nativeFunctions.destroyObject(id.value)
    }

}