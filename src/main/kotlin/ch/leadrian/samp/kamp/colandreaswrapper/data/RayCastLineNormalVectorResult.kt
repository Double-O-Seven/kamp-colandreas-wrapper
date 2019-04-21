package ch.leadrian.samp.kamp.colandreaswrapper.data

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasConstants
import ch.leadrian.samp.kamp.core.api.base.HasModelId
import ch.leadrian.samp.kamp.core.api.data.Vector3D

sealed class RayCastLineNormalVectorResult {

    abstract val value: Int

    object NoCollision : RayCastLineNormalVectorResult() {

        override val value: Int = 0

    }

    sealed class Collision : RayCastLineNormalVectorResult() {

        abstract val coordinates: Vector3D

        abstract val normalVector: Vector3D

        data class WithWater(override val coordinates: Vector3D, override val normalVector: Vector3D) :
                Collision() {

            override val value: Int = ColAndreasConstants.WATER_OBJECT

        }

        data class WithObject(
                override val modelId: Int,
                override val coordinates: Vector3D,
                override val normalVector: Vector3D
        ) : Collision(), HasModelId {

            override val value: Int = modelId

        }

    }

}