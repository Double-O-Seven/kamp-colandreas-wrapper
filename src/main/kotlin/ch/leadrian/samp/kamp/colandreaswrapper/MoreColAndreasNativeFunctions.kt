package ch.leadrian.samp.kamp.colandreaswrapper

import ch.leadrian.samp.kamp.core.api.amx.AmxNativeFunction12
import ch.leadrian.samp.kamp.core.api.amx.MutableCellArray
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Contains native functions that could not be automatically generated due to missing parameter type support in cidl-kotlin.
 */
@Singleton
class MoreColAndreasNativeFunctions
@Inject
internal constructor() {

    @Suppress("PrivatePropertyName")
    private val CA_RayCastMultiLine by AmxNativeFunction12<Float, Float, Float, Float, Float, Float, MutableCellArray, MutableCellArray, MutableCellArray, MutableCellArray, MutableCellArray, Int>()

    fun rayCastMultiLine(
            startX: Float,
            startY: Float,
            startZ: Float,
            endX: Float,
            endY: Float,
            endZ: Float,
            xValues: MutableCellArray,
            yValues: MutableCellArray,
            zValues: MutableCellArray,
            distanceValues: MutableCellArray,
            modelIds: MutableCellArray,
            maxCollisions: Int
    ): Int {
        return CA_RayCastMultiLine(
                startX,
                startY,
                startZ,
                endX,
                endY,
                endZ,
                xValues,
                yValues,
                zValues,
                distanceValues,
                modelIds,
                maxCollisions
        )
    }

}