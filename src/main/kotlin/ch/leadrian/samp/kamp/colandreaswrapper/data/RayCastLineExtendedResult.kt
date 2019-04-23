package ch.leadrian.samp.kamp.colandreaswrapper.data

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasConstants
import ch.leadrian.samp.kamp.core.api.base.HasModelId
import ch.leadrian.samp.kamp.core.api.data.Quaternion
import ch.leadrian.samp.kamp.core.api.data.Vector3D

sealed class RayCastLineExtendedResult {

    abstract val value: Int

    object NoCollision : RayCastLineExtendedResult() {

        override val value: Int = 0

    }

    sealed class Collision : RayCastLineExtendedResult() {

        abstract val collisionCoordinates: Vector3D

        abstract val rotation: Quaternion

        abstract val objectCoordinates: Vector3D

        data class WithWater(
                override val collisionCoordinates: Vector3D,
                override val rotation: Quaternion,
                override val objectCoordinates: Vector3D
        ) : Collision() {

            override val value: Int = ColAndreasConstants.WATER_OBJECT

        }

        data class WithObject(
                override val modelId: Int,
                override val collisionCoordinates: Vector3D,
                override val rotation: Quaternion,
                override val objectCoordinates: Vector3D
        ) : Collision(), HasModelId {

            override val value: Int = modelId

        }

    }

}