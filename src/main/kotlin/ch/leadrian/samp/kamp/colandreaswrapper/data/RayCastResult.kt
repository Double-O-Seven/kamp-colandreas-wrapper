package ch.leadrian.samp.kamp.colandreaswrapper.data

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasConstants
import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObject
import ch.leadrian.samp.kamp.core.api.base.HasModelId

sealed class RayCastResult(val value: Int)

object NoCollision : RayCastResult(0)

object CollisionWithWater : RayCastResult(ColAndreasConstants.WATER_OBJECT)

data class CollisionWithObject(override val modelId: Int) : RayCastResult(modelId), HasModelId

data class CollisionWithColAndreasObject(
        val colAndreasObject: ColAndreasObject
) : RayCastResult(colAndreasObject.id.value)