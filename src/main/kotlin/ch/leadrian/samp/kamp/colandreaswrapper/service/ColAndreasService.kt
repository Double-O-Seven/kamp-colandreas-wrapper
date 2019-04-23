package ch.leadrian.samp.kamp.colandreaswrapper.service

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasConstants
import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasNativeFunctions
import ch.leadrian.samp.kamp.colandreaswrapper.MoreColAndreasNativeFunctions
import ch.leadrian.samp.kamp.colandreaswrapper.constant.ExtraId
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastLineAngleResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastLineExtendedResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastLineIdResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastLineNormalVectorResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastLineResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastMultiLineCollision
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastReflectionVectorResult
import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObjectRegistry
import ch.leadrian.samp.kamp.core.api.amx.MutableCellArray
import ch.leadrian.samp.kamp.core.api.amx.MutableFloatCell
import ch.leadrian.samp.kamp.core.api.data.Box
import ch.leadrian.samp.kamp.core.api.data.Quaternion
import ch.leadrian.samp.kamp.core.api.data.Sphere
import ch.leadrian.samp.kamp.core.api.data.Vector3D
import ch.leadrian.samp.kamp.core.api.data.boxOf
import ch.leadrian.samp.kamp.core.api.data.quaternionOf
import ch.leadrian.samp.kamp.core.api.data.sphereOf
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

    fun rayCastLine(start: Vector3D, end: Vector3D): RayCastLineResult {
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
            0 -> RayCastLineResult.NoCollision
            ColAndreasConstants.WATER_OBJECT -> RayCastLineResult.Collision.WithWater(
                    vector3DOf(
                            x.value,
                            y.value,
                            z.value
                    )
            )
            else -> RayCastLineResult.Collision.WithObject(result, vector3DOf(x.value, y.value, z.value))
        }
    }

    fun rayCastLineId(start: Vector3D, end: Vector3D): RayCastLineIdResult {
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
            -1 -> RayCastLineIdResult.Collision.WithObjectOrWater(vector3DOf(x.value, y.value, z.value))
            0 -> RayCastLineIdResult.NoCollision
            else -> {
                val colAndreasObject = colAndreasObjectRegistry[result]
                        ?: throw IllegalStateException("Could not find ColAndreasObject with ID $result")
                RayCastLineIdResult.Collision.WithColAndreasObject(
                        colAndreasObject,
                        vector3DOf(x.value, y.value, z.value)
                )
            }
        }
    }

    fun rayCastLineExtraId(type: ExtraId, start: Vector3D, end: Vector3D): RayCastLineIdResult {
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
            -1 -> RayCastLineIdResult.Collision.WithObjectOrWater(vector3DOf(x.value, y.value, z.value))
            0 -> RayCastLineIdResult.NoCollision
            else -> {
                val colAndreasObject = colAndreasObjectRegistry[result]
                        ?: throw IllegalStateException("Could not find ColAndreasObject with ID $result")
                RayCastLineIdResult.Collision.WithColAndreasObject(
                        colAndreasObject,
                        vector3DOf(x.value, y.value, z.value)
                )
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

    fun rayCastLineAngle(start: Vector3D, end: Vector3D): RayCastLineAngleResult {
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
            0 -> RayCastLineAngleResult.NoCollision
            ColAndreasConstants.WATER_OBJECT -> RayCastLineAngleResult.Collision.WithWater(
                    coordinates = vector3DOf(x.value, y.value, z.value),
                    rotation = vector3DOf(rx.value, ry.value, rz.value)
            )
            else -> RayCastLineAngleResult.Collision.WithObject(
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

    fun rayCastLineNormalVector(start: Vector3D, end: Vector3D): RayCastLineNormalVectorResult {
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
            0 -> RayCastLineNormalVectorResult.NoCollision
            ColAndreasConstants.WATER_OBJECT -> RayCastLineNormalVectorResult.Collision.WithWater(
                    coordinates = vector3DOf(x.value, y.value, z.value),
                    normalVector = vector3DOf(rx.value, ry.value, rz.value)
            )
            else -> RayCastLineNormalVectorResult.Collision.WithObject(
                    modelId = result,
                    coordinates = vector3DOf(x.value, y.value, z.value),
                    normalVector = vector3DOf(rx.value, ry.value, rz.value)
            )
        }
    }

    fun rayCastLineExtended(start: Vector3D, end: Vector3D): RayCastLineExtendedResult {
        val x = MutableFloatCell()
        val y = MutableFloatCell()
        val z = MutableFloatCell()
        val rx = MutableFloatCell()
        val ry = MutableFloatCell()
        val rz = MutableFloatCell()
        val rw = MutableFloatCell()
        val ox = MutableFloatCell()
        val oy = MutableFloatCell()
        val oz = MutableFloatCell()
        val result = nativeFunctions.rayCastLineEx(
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
                rz = rz,
                rw = rw,
                cx = ox,
                cy = oy,
                cz = oz
        )

        return when (result) {
            0 -> RayCastLineExtendedResult.NoCollision
            ColAndreasConstants.WATER_OBJECT -> RayCastLineExtendedResult.Collision.WithWater(
                    collisionCoordinates = vector3DOf(x.value, y.value, z.value),
                    rotation = quaternionOf(x = rx.value, y = ry.value, z = rz.value, w = rw.value),
                    objectCoordinates = vector3DOf(ox.value, ox.value, ox.value)
            )
            else -> RayCastLineExtendedResult.Collision.WithObject(
                    modelId = result,
                    collisionCoordinates = vector3DOf(x.value, y.value, z.value),
                    rotation = quaternionOf(x = rx.value, y = ry.value, z = rz.value, w = rw.value),
                    objectCoordinates = vector3DOf(ox.value, ox.value, ox.value)
            )
        }
    }

    fun contactTest(modelId: Int, start: Vector3D, end: Vector3D): Boolean =
            nativeFunctions.contactTest(modelId, start.x, start.y, start.z, end.x, end.y, end.z)

    fun eulerToQuaternion(rotation: Vector3D): Quaternion {
        val x = MutableFloatCell()
        val y = MutableFloatCell()
        val z = MutableFloatCell()
        val w = MutableFloatCell()
        nativeFunctions.eulerToQuat(rx = rotation.x, ry = rotation.y, rz = rotation.z, x = x, y = y, z = z, w = w)
        return quaternionOf(x = x.value, y = y.value, z = z.value, w = w.value)
    }

    fun quaternionToEuler(quaternion: Quaternion): Vector3D {
        val x = MutableFloatCell()
        val y = MutableFloatCell()
        val z = MutableFloatCell()
        nativeFunctions.quatToEuler(
                x = quaternion.x,
                y = quaternion.y,
                z = quaternion.z,
                w = quaternion.w,
                rx = x,
                ry = y,
                rz = z
        )
        return vector3DOf(x = x.value, y = y.value, z = z.value)
    }

    fun getModelBoundingSphere(modelId: Int): Sphere {
        val x = MutableFloatCell()
        val y = MutableFloatCell()
        val z = MutableFloatCell()
        val r = MutableFloatCell()
        nativeFunctions.getModelBoundingSphere(modelId, x, y, z, r)
        return sphereOf(x.value, y.value, z.value, r.value)
    }

    fun getModelBoundingBox(modelId: Int): Box {
        val minX = MutableFloatCell()
        val minY = MutableFloatCell()
        val minZ = MutableFloatCell()
        val maxX = MutableFloatCell()
        val maxY = MutableFloatCell()
        val maxZ = MutableFloatCell()
        nativeFunctions.getModelBoundingBox(
                modelid = modelId,
                minx = minX,
                miny = minY,
                minz = minZ,
                maxx = maxX,
                maxy = maxY,
                maxz = maxZ
        )
        return boxOf(
                minX = minX.value,
                minY = minY.value,
                minZ = minZ.value,
                maxX = maxX.value,
                maxY = maxY.value,
                maxZ = maxZ.value
        )
    }
}