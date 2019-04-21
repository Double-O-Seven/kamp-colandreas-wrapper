package ch.leadrian.samp.kamp.colandreaswrapper.data

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasConstants
import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObject
import ch.leadrian.samp.kamp.core.api.base.HasModelId
import ch.leadrian.samp.kamp.core.api.data.Vector3D

sealed class RayCastResult {

    abstract val value: Int

}

object NoCollision : RayCastResult() {

    override val value: Int = 0

}

sealed class Collision : RayCastResult() {

    abstract val coordinates: Vector3D

}

data class CollisionWithWater(override val coordinates: Vector3D) : Collision() {

    override val value: Int = ColAndreasConstants.WATER_OBJECT

}

data class CollisionWithObject(override val modelId: Int, override val coordinates: Vector3D) : Collision(), HasModelId {

    override val value: Int = modelId

}

data class CollisionWithObjectOrWater(override val coordinates: Vector3D) : Collision() {

    override val value: Int = -1

}

data class CollisionWithColAndreasObject(
        val colAndreasObject: ColAndreasObject,
        override val coordinates: Vector3D
) : Collision() {

    override val value: Int = colAndreasObject.id.value

}