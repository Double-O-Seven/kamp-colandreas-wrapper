package ch.leadrian.samp.kamp.colandreaswrapper.data

import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObject
import ch.leadrian.samp.kamp.core.api.data.Vector3D

sealed class RayCastLineIdResult {

    abstract val value: Int

    object NoCollision : RayCastLineIdResult() {

        override val value: Int = 0

    }

    sealed class Collision : RayCastLineIdResult() {

        abstract val coordinates: Vector3D

        data class WithObjectOrWater(override val coordinates: Vector3D) : Collision() {

            override val value: Int = -1

        }

        data class WithColAndreasObject(
                val colAndreasObject: ColAndreasObject,
                override val coordinates: Vector3D
        ) : Collision() {

            override val value: Int = colAndreasObject.id.value

        }

    }

}