package ch.leadrian.samp.kamp.colandreaswrapper.data

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasConstants
import ch.leadrian.samp.kamp.core.api.base.HasModelId
import ch.leadrian.samp.kamp.core.api.data.Vector3D

sealed class RayCastLineResult {

    abstract val value: Int

    object NoCollision : RayCastLineResult() {

        override val value: Int = 0

    }

    sealed class Collision : RayCastLineResult() {

        abstract val coordinates: Vector3D

        data class WithWater(override val coordinates: Vector3D) : Collision() {

            override val value: Int = ColAndreasConstants.WATER_OBJECT

        }

        data class WithObject(override val modelId: Int, override val coordinates: Vector3D) : Collision(),
                HasModelId {

            override val value: Int = modelId

        }

    }

}