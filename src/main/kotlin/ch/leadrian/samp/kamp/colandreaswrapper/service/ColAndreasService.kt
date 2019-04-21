package ch.leadrian.samp.kamp.colandreaswrapper.service

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasConstants
import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasNativeFunctions
import ch.leadrian.samp.kamp.colandreaswrapper.constant.ExtraId
import ch.leadrian.samp.kamp.colandreaswrapper.data.CollisionWithColAndreasObject
import ch.leadrian.samp.kamp.colandreaswrapper.data.CollisionWithObject
import ch.leadrian.samp.kamp.colandreaswrapper.data.CollisionWithObjectOrWater
import ch.leadrian.samp.kamp.colandreaswrapper.data.CollisionWithWater
import ch.leadrian.samp.kamp.colandreaswrapper.data.NoCollision
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastResult
import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObjectRegistry
import ch.leadrian.samp.kamp.core.api.amx.AmxNativeFunction12
import ch.leadrian.samp.kamp.core.api.amx.MutableCellArray
import ch.leadrian.samp.kamp.core.api.amx.MutableFloatCell
import ch.leadrian.samp.kamp.core.api.data.Sphere
import ch.leadrian.samp.kamp.core.api.data.Vector3D
import ch.leadrian.samp.kamp.core.api.data.vector3DOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColAndreasService
@Inject
internal constructor(
        private val nativeFunctions: ColAndreasNativeFunctions,
        private val colAndreasObjectRegistry: ColAndreasObjectRegistry
) {

    // [native] int CA_RayCastMultiLine(float StartX, float StartY, float StartZ, float EndX, float EndY, float EndZ, float retx[], float rety[], float retz[], float retdist[], int ModelIDs[], int size);

    private val CA_RayCastMultiLine by AmxNativeFunction12<Float, Float, Float, Float, Float, Float, MutableCellArray, MutableCellArray, MutableCellArray, MutableCellArray, MutableCellArray, Int>()

    private var isInitialized: Boolean = false

    fun initializeColAndreas() {
        if (isInitialized) {
            return
        }

        nativeFunctions.init()
        isInitialized = true
    }

    fun removeBuilding(modelId: Int, sphere: Sphere) {
        nativeFunctions.removeBuilding(
                modelid = modelId,
                x = sphere.x,
                y = sphere.y,
                z = sphere.z,
                radius = sphere.radius
        )
    }

    fun rayCastLine(start: Vector3D, end: Vector3D): RayCastResult {
        val x = MutableFloatCell()
        val y = MutableFloatCell()
        val z = MutableFloatCell()
        val result = nativeFunctions.rayCastLine(
                StartX = start.x,
                StartY = start.y,
                StartZ = start.z,
                EndX = end.x,
                EndY = end.y,
                EndZ = end.z,
                x = x,
                y = y,
                z = z
        )
        return when (result) {
            0 -> NoCollision
            ColAndreasConstants.WATER_OBJECT -> CollisionWithWater(vector3DOf(x.value, y.value, z.value))
            else -> CollisionWithObject(result, vector3DOf(x.value, y.value, z.value))
        }
    }

    fun rayCastLineId(start: Vector3D, end: Vector3D): RayCastResult {
        val x = MutableFloatCell()
        val y = MutableFloatCell()
        val z = MutableFloatCell()
        val result = nativeFunctions.rayCastLineID(
                StartX = start.x,
                StartY = start.y,
                StartZ = start.z,
                EndX = end.x,
                EndY = end.y,
                EndZ = end.z,
                x = x,
                y = y,
                z = z
        )
        return when (result) {
            -1 -> CollisionWithObjectOrWater(vector3DOf(x.value, y.value, z.value))
            0 -> NoCollision
            else -> {
                val colAndreasObject = colAndreasObjectRegistry[result]
                        ?: throw IllegalStateException("Could not find ColAndreasObject with ID $result")
                CollisionWithColAndreasObject(colAndreasObject, vector3DOf(x.value, y.value, z.value))
            }
        }
    }

    fun rayCastLineExtraId(type: ExtraId, start: Vector3D, end: Vector3D): RayCastResult {
        val x = MutableFloatCell()
        val y = MutableFloatCell()
        val z = MutableFloatCell()
        val result = nativeFunctions.rayCastLineExtraID(
                type = type.value,
                StartX = start.x,
                StartY = start.y,
                StartZ = start.z,
                EndX = end.x,
                EndY = end.y,
                EndZ = end.z,
                x = x,
                y = y,
                z = z
        )
        return when (result) {
            -1 -> CollisionWithObjectOrWater(vector3DOf(x.value, y.value, z.value))
            0 -> NoCollision
            else -> {
                val colAndreasObject = colAndreasObjectRegistry[result]
                        ?: throw IllegalStateException("Could not find ColAndreasObject with ID $result")
                CollisionWithColAndreasObject(colAndreasObject, vector3DOf(x.value, y.value, z.value))
            }
        }
    }
}