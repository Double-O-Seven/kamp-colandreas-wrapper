package ch.leadrian.samp.kamp.colandreaswrapper.data

import ch.leadrian.samp.kamp.core.api.base.HasModelId
import ch.leadrian.samp.kamp.core.api.data.Vector3D

data class RayCastMultiLineCollision(
        val coordinates: Vector3D,
        val distance: Float,
        override val modelId: Int
) : HasModelId