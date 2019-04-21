package ch.leadrian.samp.kamp.colandreaswrapper.service

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasConstants
import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasNativeFunctions
import ch.leadrian.samp.kamp.colandreaswrapper.MoreColAndreasNativeFunctions
import ch.leadrian.samp.kamp.colandreaswrapper.constant.ExtraId
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastAngleResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastIdResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastMultiLineCollision
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastNormalVectorResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastReflectionVectorResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastResult
import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObjectRegistry
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
        private val moreNativeFunctions: MoreColAndreasNativeFunctions,
        private val colAndreasObjectRegistry: ColAndreasObjectRegistry
) {

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
            0 -> RayCastResult.NoCollision
            ColAndreasConstants.WATER_OBJECT -> RayCastResult.Collision.WithWater(vector3DOf(x.value, y.value, z.value))
            else -> RayCastResult.Collision.WithObject(result, vector3DOf(x.value, y.value, z.value))
        }
    }

    fun rayCastLineId(start: Vector3D, end: Vector3D): RayCastIdResult {
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
            -1 -> RayCastIdResult.Collision.WithObjectOrWater(vector3DOf(x.value, y.value, z.value))
            0 -> RayCastIdResult.NoCollision
            else -> {
                val colAndreasObject = colAndreasObjectRegistry[result]
                        ?: throw IllegalStateException("Could not find ColAndreasObject with ID $result")
                RayCastIdResult.Collision.WithColAndreasObject(colAndreasObject, vector3DOf(x.value, y.value, z.value))
            }
        }
    }

    fun rayCastLineExtraId(type: ExtraId, start: Vector3D, end: Vector3D): RayCastIdResult {
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
            -1 -> RayCastIdResult.Collision.WithObjectOrWater(vector3DOf(x.value, y.value, z.value))
            0 -> RayCastIdResult.NoCollision
            else -> {
                val colAndreasObject = colAndreasObjectRegistry[result]
                        ?: throw IllegalStateException("Could not find ColAndreasObject with ID $result")
                RayCastIdResult.Collision.WithColAndreasObject(colAndreasObject, vector3DOf(x.value, y.value, z.value))
            }
        }
    }

    @JvmOverloads
    fun rayCastMultiLine(
            start: Vector3D,
            end: Vector3D,
            maxCollisions: Int = ColAndreasConstants.MAX_MULTICAST_SIZE
    ): List<RayCastMultiLineCollision> {
        require(0 <= maxCollisions && maxCollisions <= ColAndreasConstants.MAX_MULTICAST_SIZE) {
            "maxCollisions must be between 0 and ${ColAndreasConstants.MAX_MULTICAST_SIZE}"
        }
        val xValues = MutableCellArray(maxCollisions)
        val yValues = MutableCellArray(maxCollisions)
        val zValues = MutableCellArray(maxCollisions)
        val distanceValues = MutableCellArray(maxCollisions)
        val modelIds = MutableCellArray(maxCollisions)
        val result = moreNativeFunctions.rayCastMultiLine(
                startX = start.x,
                startY = start.y,
                startZ = start.z,
                endX = end.x,
                endY = end.y,
                endZ = end.z,
                xValues = xValues,
                yValues = yValues,
                zValues = zValues,
                distanceValues = distanceValues,
                modelIds = modelIds,
                maxCollisions = maxCollisions
        )
        return if (result <= 0) {
            emptyList()
        } else {
            (0..(result - 1)).map { i ->
                val x = Float.fromBits(xValues[i])
                val y = Float.fromBits(yValues[i])
                val z = Float.fromBits(zValues[i])
                val distance = Float.fromBits(distanceValues[i])
                RayCastMultiLineCollision(vector3DOf(x, y, z), distance, modelIds[i])
            }
        }
    }

    fun rayCastLineAngle(start: Vector3D, end: Vector3D): RayCastAngleResult {
        val x = MutableFloatCell()
        val y = MutableFloatCell()
        val z = MutableFloatCell()
        val rx = MutableFloatCell()
        val ry = MutableFloatCell()
        val rz = MutableFloatCell()
        val result = nativeFunctions.rayCastLineAngle(
                StartX = start.x,
                StartY = start.y,
                StartZ = start.z,
                EndX = end.x,
                EndY = end.y,
                EndZ = end.z,
                x = x,
                y = y,
                z = z,
                rx = rx,
                ry = ry,
                rz = rz
        )
        return when (result) {
            0 -> RayCastAngleResult.NoCollision
            ColAndreasConstants.WATER_OBJECT -> RayCastAngleResult.Collision.WithWater(
                    coordinates = vector3DOf(x.value, y.value, z.value),
                    rotation = vector3DOf(rx.value, ry.value, rz.value)
            )
            else -> RayCastAngleResult.Collision.WithObject(
                    modelId = result,
                    coordinates = vector3DOf(x.value, y.value, z.value),
                    rotation = vector3DOf(rx.value, ry.value, rz.value)
            )
        }
    }

    fun rayCastReflectionVector(start: Vector3D, end: Vector3D): RayCastReflectionVectorResult {
        val x = MutableFloatCell()
        val y = MutableFloatCell()
        val z = MutableFloatCell()
        val rx = MutableFloatCell()
        val ry = MutableFloatCell()
        val rz = MutableFloatCell()
        val result = nativeFunctions.rayCastReflectionVector(
                startx = start.x,
                starty = start.y,
                startz = start.z,
                endx = end.x,
                endy = end.y,
                endz = end.z,
                x = x,
                y = y,
                z = z,
                nx = rx,
                ny = ry,
                nz = rz
        )
        return when (result) {
            0 -> RayCastReflectionVectorResult.NoCollision
            ColAndreasConstants.WATER_OBJECT -> RayCastReflectionVectorResult.Collision.WithWater(
                    coordinates = vector3DOf(x.value, y.value, z.value),
                    reflectionVector = vector3DOf(rx.value, ry.value, rz.value)
            )
            else -> RayCastReflectionVectorResult.Collision.WithObject(
                    modelId = result,
                    coordinates = vector3DOf(x.value, y.value, z.value),
                    reflectionVector = vector3DOf(rx.value, ry.value, rz.value)
            )
        }
    }

    fun rayCastLineNormalVector(start: Vector3D, end: Vector3D): RayCastNormalVectorResult {
        val x = MutableFloatCell()
        val y = MutableFloatCell()
        val z = MutableFloatCell()
        val rx = MutableFloatCell()
        val ry = MutableFloatCell()
        val rz = MutableFloatCell()
        val result = nativeFunctions.rayCastLineNormal(
                startx = start.x,
                starty = start.y,
                startz = start.z,
                endx = end.x,
                endy = end.y,
                endz = end.z,
                x = x,
                y = y,
                z = z,
                nx = rx,
                ny = ry,
                nz = rz
        )
        return when (result) {
            0 -> RayCastNormalVectorResult.NoCollision
            ColAndreasConstants.WATER_OBJECT -> RayCastNormalVectorResult.Collision.WithWater(
                    coordinates = vector3DOf(x.value, y.value, z.value),
                    normalVector = vector3DOf(rx.value, ry.value, rz.value)
            )
            else -> RayCastNormalVectorResult.Collision.WithObject(
                    modelId = result,
                    coordinates = vector3DOf(x.value, y.value, z.value),
                    normalVector = vector3DOf(rx.value, ry.value, rz.value)
            )
        }
    }
}