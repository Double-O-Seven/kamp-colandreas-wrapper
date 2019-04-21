package ch.leadrian.samp.kamp.colandreaswrapper.service

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasConstants
import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasNativeFunctions
import ch.leadrian.samp.kamp.colandreaswrapper.MoreColAndreasNativeFunctions
import ch.leadrian.samp.kamp.colandreaswrapper.constant.ExtraId
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastLineAngleResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastLineIdResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastLineNormalVectorResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastLineResult
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastMultiLineCollision
import ch.leadrian.samp.kamp.colandreaswrapper.data.RayCastReflectionVectorResult
import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObject
import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObjectIndex
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
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal object ColAndreasServiceSpec : Spek({
    val colAndreasNativeFunctions by memoized { mockk<ColAndreasNativeFunctions>() }
    val moreColAndreasNativeFunctions by memoized { mockk<MoreColAndreasNativeFunctions>() }
    val colAndreasObjectRegistry by memoized { mockk<ColAndreasObjectRegistry>() }
    val colAndreasService by memoized {
        ColAndreasService(colAndreasNativeFunctions, moreColAndreasNativeFunctions, colAndreasObjectRegistry)
    }

    describe("initializeColAndreas") {
        beforeEach {
            every { colAndreasNativeFunctions.init() } returns true
            colAndreasService.initializeColAndreas()
        }

        it("should call colAndreasNativeFunctions.init") {
            verify { colAndreasNativeFunctions.init() }
        }

        context("second call is executed") {
            beforeEach {
                colAndreasService.initializeColAndreas()
            }

            it("should not call colAndreasNativeFunctions.init twice") {
                verify(exactly = 1) { colAndreasNativeFunctions.init() }
            }
        }
    }

    describe("removeBuilding") {
        beforeEach {
            every { colAndreasNativeFunctions.removeBuilding(any(), any(), any(), any(), any()) } returns true
            colAndreasService.removeBuilding(1234, sphereOf(x = 1f, y = 2f, z = 3f, radius = 4f))
        }

        it("should call colAndreasNativeFunctions.removeBuilding") {
            verify { colAndreasNativeFunctions.removeBuilding(modelid = 1234, x = 1f, y = 2f, z = 3f, radius = 4f) }
        }
    }

    describe("rayCastLine") {
        context("no collision") {
            lateinit var result: RayCastLineResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLine(
                            StartX = 1f,
                            StartY = 2f,
                            StartZ = 3f,
                            EndX = 4f,
                            EndY = 5f,
                            EndZ = 6f,
                            x = any(),
                            y = any(),
                            z = any()
                    )
                } returns 0
                result = colAndreasService.rayCastLine(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return NoCollision") {
                assertThat(result)
                        .isEqualTo(RayCastLineResult.NoCollision)
            }
        }

        context("collision with water") {
            lateinit var result: RayCastLineResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLine(
                            StartX = 1f,
                            StartY = 2f,
                            StartZ = 3f,
                            EndX = 4f,
                            EndY = 5f,
                            EndZ = 6f,
                            x = any(),
                            y = any(),
                            z = any()
                    )
                } answers {
                    arg<MutableFloatCell>(6).value = 7f
                    arg<MutableFloatCell>(7).value = 8f
                    arg<MutableFloatCell>(8).value = 9f
                    ColAndreasConstants.WATER_OBJECT
                }
                result = colAndreasService.rayCastLine(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return Collision.WithWater") {
                assertThat(result)
                        .isEqualTo(RayCastLineResult.Collision.WithWater(vector3DOf(x = 7f, y = 8f, z = 9f)))
            }
        }

        context("collision with object") {
            lateinit var result: RayCastLineResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLine(
                            StartX = 1f,
                            StartY = 2f,
                            StartZ = 3f,
                            EndX = 4f,
                            EndY = 5f,
                            EndZ = 6f,
                            x = any(),
                            y = any(),
                            z = any()
                    )
                } answers {
                    arg<MutableFloatCell>(6).value = 7f
                    arg<MutableFloatCell>(7).value = 8f
                    arg<MutableFloatCell>(8).value = 9f
                    1337
                }
                result = colAndreasService.rayCastLine(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return Collision.WithObject") {
                assertThat(result)
                        .isEqualTo(RayCastLineResult.Collision.WithObject(1337, vector3DOf(x = 7f, y = 8f, z = 9f)))
            }
        }
    }

    describe("rayCastLineId") {
        context("no collision") {
            lateinit var result: RayCastLineIdResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLineID(
                            StartX = 1f,
                            StartY = 2f,
                            StartZ = 3f,
                            EndX = 4f,
                            EndY = 5f,
                            EndZ = 6f,
                            x = any(),
                            y = any(),
                            z = any()
                    )
                } returns 0
                result = colAndreasService.rayCastLineId(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return NoCollision") {
                assertThat(result)
                        .isEqualTo(RayCastLineIdResult.NoCollision)
            }
        }

        context("collision with water or object") {
            lateinit var result: RayCastLineIdResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLineID(
                            StartX = 1f,
                            StartY = 2f,
                            StartZ = 3f,
                            EndX = 4f,
                            EndY = 5f,
                            EndZ = 6f,
                            x = any(),
                            y = any(),
                            z = any()
                    )
                } answers {
                    arg<MutableFloatCell>(6).value = 7f
                    arg<MutableFloatCell>(7).value = 8f
                    arg<MutableFloatCell>(8).value = 9f
                    -1
                }
                result = colAndreasService.rayCastLineId(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return Collision.WithObjectOrWater") {
                assertThat(result)
                        .isEqualTo(RayCastLineIdResult.Collision.WithObjectOrWater(vector3DOf(x = 7f, y = 8f, z = 9f)))
            }
        }

        context("collision with ColAndreas object") {
            val colAndreasObject by memoized {
                mockk<ColAndreasObject> {
                    every { id } returns ColAndreasObjectIndex.valueOf(69)
                }
            }
            lateinit var result: RayCastLineIdResult

            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLineID(
                            StartX = 1f,
                            StartY = 2f,
                            StartZ = 3f,
                            EndX = 4f,
                            EndY = 5f,
                            EndZ = 6f,
                            x = any(),
                            y = any(),
                            z = any()
                    )
                } answers {
                    arg<MutableFloatCell>(6).value = 7f
                    arg<MutableFloatCell>(7).value = 8f
                    arg<MutableFloatCell>(8).value = 9f
                    1337
                }
                every { colAndreasObjectRegistry[1337] } returns colAndreasObject
                result = colAndreasService.rayCastLineId(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return Collision.WithColAndreasObject") {
                assertThat(result)
                        .isEqualTo(
                                RayCastLineIdResult.Collision.WithColAndreasObject(
                                        colAndreasObject,
                                        vector3DOf(x = 7f, y = 8f, z = 9f)
                                )
                        )
            }
        }
    }

    describe("rayCastLineExtraId") {
        context("no collision") {
            lateinit var result: RayCastLineIdResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLineExtraID(
                            type = ExtraId.EXTRA_3.value,
                            StartX = 1f,
                            StartY = 2f,
                            StartZ = 3f,
                            EndX = 4f,
                            EndY = 5f,
                            EndZ = 6f,
                            x = any(),
                            y = any(),
                            z = any()
                    )
                } returns 0
                result = colAndreasService.rayCastLineExtraId(
                        ExtraId.EXTRA_3,
                        vector3DOf(1f, 2f, 3f),
                        vector3DOf(4f, 5f, 6f)
                )
            }

            it("should return NoCollision") {
                assertThat(result)
                        .isEqualTo(RayCastLineIdResult.NoCollision)
            }
        }

        context("collision with water or object") {
            lateinit var result: RayCastLineIdResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLineExtraID(
                            type = ExtraId.EXTRA_3.value,
                            StartX = 1f,
                            StartY = 2f,
                            StartZ = 3f,
                            EndX = 4f,
                            EndY = 5f,
                            EndZ = 6f,
                            x = any(),
                            y = any(),
                            z = any()
                    )
                } answers {
                    arg<MutableFloatCell>(7).value = 7f
                    arg<MutableFloatCell>(8).value = 8f
                    arg<MutableFloatCell>(9).value = 9f
                    -1
                }
                result = colAndreasService.rayCastLineExtraId(
                        ExtraId.EXTRA_3,
                        vector3DOf(1f, 2f, 3f),
                        vector3DOf(4f, 5f, 6f)
                )
            }

            it("should return Collision.WithObjectOrWater") {
                assertThat(result)
                        .isEqualTo(RayCastLineIdResult.Collision.WithObjectOrWater(vector3DOf(x = 7f, y = 8f, z = 9f)))
            }
        }

        context("collision with ColAndreas object") {
            val colAndreasObject by memoized {
                mockk<ColAndreasObject> {
                    every { id } returns ColAndreasObjectIndex.valueOf(69)
                }
            }
            lateinit var result: RayCastLineIdResult

            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLineExtraID(
                            type = ExtraId.EXTRA_3.value,
                            StartX = 1f,
                            StartY = 2f,
                            StartZ = 3f,
                            EndX = 4f,
                            EndY = 5f,
                            EndZ = 6f,
                            x = any(),
                            y = any(),
                            z = any()
                    )
                } answers {
                    arg<MutableFloatCell>(7).value = 7f
                    arg<MutableFloatCell>(8).value = 8f
                    arg<MutableFloatCell>(9).value = 9f
                    1337
                }
                every { colAndreasObjectRegistry[1337] } returns colAndreasObject
                result = colAndreasService.rayCastLineExtraId(
                        ExtraId.EXTRA_3,
                        vector3DOf(1f, 2f, 3f),
                        vector3DOf(4f, 5f, 6f)
                )
            }

            it("should return Collision.WithColAndreasObject") {
                assertThat(result)
                        .isEqualTo(
                                RayCastLineIdResult.Collision.WithColAndreasObject(
                                        colAndreasObject,
                                        vector3DOf(x = 7f, y = 8f, z = 9f)
                                )
                        )
            }
        }
    }

    describe("rayCastMultiLine") {
        listOf(-1, 0).forEach { result ->
            context("result is $result") {
                lateinit var collisions: List<RayCastMultiLineCollision>

                beforeEach {
                    every {
                        moreColAndreasNativeFunctions.rayCastMultiLine(
                                1f,
                                2f,
                                3f,
                                4f,
                                5f,
                                6f,
                                any(),
                                any(),
                                any(),
                                any(),
                                any(),
                                13
                        )
                    } returns result
                    collisions = colAndreasService.rayCastMultiLine(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f), 13)
                }

                it("should return empty list") {
                    assertThat(collisions)
                            .isEmpty()
                }
            }
        }

        context("result greater than 0") {
            lateinit var collisions: List<RayCastMultiLineCollision>

            beforeEach {
                every {
                    moreColAndreasNativeFunctions.rayCastMultiLine(
                            1f,
                            2f,
                            3f,
                            4f,
                            5f,
                            6f,
                            any(),
                            any(),
                            any(),
                            any(),
                            any(),
                            13
                    )
                } answers {
                    arg<MutableCellArray>(6).also {
                        it[0] = 1f.toRawBits()
                        it[1] = 4f.toRawBits()
                    }
                    arg<MutableCellArray>(7).also {
                        it[0] = 2f.toRawBits()
                        it[1] = 5f.toRawBits()
                    }
                    arg<MutableCellArray>(8).also {
                        it[0] = 3f.toRawBits()
                        it[1] = 6f.toRawBits()
                    }
                    arg<MutableCellArray>(9).also {
                        it[0] = 13.37f.toRawBits()
                        it[1] = 12.34f.toRawBits()
                    }
                    arg<MutableCellArray>(10).also {
                        it[0] = 1337
                        it[1] = 1234
                    }
                    2
                }
                collisions = colAndreasService.rayCastMultiLine(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f), 13)
            }

            it("should return collisions") {
                assertThat(collisions)
                        .containsExactly(
                                RayCastMultiLineCollision(vector3DOf(1f, 2f, 3f), 13.37f, 1337),
                                RayCastMultiLineCollision(vector3DOf(4f, 5f, 6f), 12.34f, 1234)
                        )
            }
        }

        listOf(-1, ColAndreasConstants.MAX_MULTICAST_SIZE + 1).forEach { maxCollisions ->
            context("maxCollisions is $maxCollisions") {
                lateinit var caughtThrowable: Throwable

                beforeEach {
                    caughtThrowable = catchThrowable {
                        colAndreasService.rayCastMultiLine(
                                vector3DOf(1f, 2f, 3f),
                                vector3DOf(4f, 5f, 6f),
                                maxCollisions
                        )
                    }
                }

                it("should throw exception") {
                    assertThat(caughtThrowable)
                            .isInstanceOf(IllegalArgumentException::class.java)
                            .hasMessage("maxCollisions must be between 0 and ${ColAndreasConstants.MAX_MULTICAST_SIZE}")
                }
            }
        }

        listOf(0, ColAndreasConstants.MAX_MULTICAST_SIZE).forEach { maxCollisions ->
            context("maxCollisions is $maxCollisions") {
                var caughtThrowable: Throwable? = null

                beforeEach {
                    caughtThrowable = catchThrowable {
                        every {
                            moreColAndreasNativeFunctions.rayCastMultiLine(
                                    any(),
                                    any(),
                                    any(),
                                    any(),
                                    any(),
                                    any(),
                                    any(),
                                    any(),
                                    any(),
                                    any(),
                                    any(),
                                    any()
                            )
                        } returns 0
                        colAndreasService.rayCastMultiLine(
                                vector3DOf(1f, 2f, 3f),
                                vector3DOf(4f, 5f, 6f),
                                maxCollisions
                        )
                    }
                }

                it("should not throw exception") {
                    assertThat(caughtThrowable)
                            .isNull()
                }
            }
        }
    }

    describe("rayCastLineAngle") {
        context("no collision") {
            lateinit var result: RayCastLineAngleResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLineAngle(
                            StartX = 1f,
                            StartY = 2f,
                            StartZ = 3f,
                            EndX = 4f,
                            EndY = 5f,
                            EndZ = 6f,
                            x = any(),
                            y = any(),
                            z = any(),
                            rx = any(),
                            ry = any(),
                            rz = any()
                    )
                } returns 0
                result = colAndreasService.rayCastLineAngle(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return NoCollision") {
                assertThat(result)
                        .isEqualTo(RayCastLineAngleResult.NoCollision)
            }
        }

        context("collision with water") {
            lateinit var result: RayCastLineAngleResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLineAngle(
                            StartX = 1f,
                            StartY = 2f,
                            StartZ = 3f,
                            EndX = 4f,
                            EndY = 5f,
                            EndZ = 6f,
                            x = any(),
                            y = any(),
                            z = any(),
                            rx = any(),
                            ry = any(),
                            rz = any()
                    )
                } answers {
                    arg<MutableFloatCell>(6).value = 7f
                    arg<MutableFloatCell>(7).value = 8f
                    arg<MutableFloatCell>(8).value = 9f
                    arg<MutableFloatCell>(9).value = 10f
                    arg<MutableFloatCell>(10).value = 11f
                    arg<MutableFloatCell>(11).value = 12f
                    ColAndreasConstants.WATER_OBJECT
                }
                result = colAndreasService.rayCastLineAngle(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return Collision.WithWater") {
                assertThat(result)
                        .isEqualTo(
                                RayCastLineAngleResult.Collision.WithWater(
                                        coordinates = vector3DOf(x = 7f, y = 8f, z = 9f),
                                        rotation = vector3DOf(x = 10f, y = 11f, z = 12f)
                                )
                        )
            }
        }

        context("collision with object") {
            lateinit var result: RayCastLineAngleResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLineAngle(
                            StartX = 1f,
                            StartY = 2f,
                            StartZ = 3f,
                            EndX = 4f,
                            EndY = 5f,
                            EndZ = 6f,
                            x = any(),
                            y = any(),
                            z = any(),
                            rx = any(),
                            ry = any(),
                            rz = any()
                    )
                } answers {
                    arg<MutableFloatCell>(6).value = 7f
                    arg<MutableFloatCell>(7).value = 8f
                    arg<MutableFloatCell>(8).value = 9f
                    arg<MutableFloatCell>(9).value = 10f
                    arg<MutableFloatCell>(10).value = 11f
                    arg<MutableFloatCell>(11).value = 12f
                    1337
                }
                result = colAndreasService.rayCastLineAngle(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return Collision.WithObject") {
                assertThat(result)
                        .isEqualTo(
                                RayCastLineAngleResult.Collision.WithObject(
                                        1337,
                                        coordinates = vector3DOf(x = 7f, y = 8f, z = 9f),
                                        rotation = vector3DOf(x = 10f, y = 11f, z = 12f)
                                )
                        )
            }
        }
    }

    describe("rayCastReflectionVector") {
        context("no collision") {
            lateinit var result: RayCastReflectionVectorResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastReflectionVector(
                            startx = 1f,
                            starty = 2f,
                            startz = 3f,
                            endx = 4f,
                            endy = 5f,
                            endz = 6f,
                            x = any(),
                            y = any(),
                            z = any(),
                            nx = any(),
                            ny = any(),
                            nz = any()
                    )
                } returns 0
                result = colAndreasService.rayCastReflectionVector(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return NoCollision") {
                assertThat(result)
                        .isEqualTo(RayCastReflectionVectorResult.NoCollision)
            }
        }

        context("collision with water") {
            lateinit var result: RayCastReflectionVectorResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastReflectionVector(
                            startx = 1f,
                            starty = 2f,
                            startz = 3f,
                            endx = 4f,
                            endy = 5f,
                            endz = 6f,
                            x = any(),
                            y = any(),
                            z = any(),
                            nx = any(),
                            ny = any(),
                            nz = any()
                    )
                } answers {
                    arg<MutableFloatCell>(6).value = 7f
                    arg<MutableFloatCell>(7).value = 8f
                    arg<MutableFloatCell>(8).value = 9f
                    arg<MutableFloatCell>(9).value = 10f
                    arg<MutableFloatCell>(10).value = 11f
                    arg<MutableFloatCell>(11).value = 12f
                    ColAndreasConstants.WATER_OBJECT
                }
                result = colAndreasService.rayCastReflectionVector(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return Collision.WithWater") {
                assertThat(result)
                        .isEqualTo(
                                RayCastReflectionVectorResult.Collision.WithWater(
                                        coordinates = vector3DOf(x = 7f, y = 8f, z = 9f),
                                        reflectionVector = vector3DOf(x = 10f, y = 11f, z = 12f)
                                )
                        )
            }
        }

        context("collision with object") {
            lateinit var result: RayCastReflectionVectorResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastReflectionVector(
                            startx = 1f,
                            starty = 2f,
                            startz = 3f,
                            endx = 4f,
                            endy = 5f,
                            endz = 6f,
                            x = any(),
                            y = any(),
                            z = any(),
                            nx = any(),
                            ny = any(),
                            nz = any()
                    )
                } answers {
                    arg<MutableFloatCell>(6).value = 7f
                    arg<MutableFloatCell>(7).value = 8f
                    arg<MutableFloatCell>(8).value = 9f
                    arg<MutableFloatCell>(9).value = 10f
                    arg<MutableFloatCell>(10).value = 11f
                    arg<MutableFloatCell>(11).value = 12f
                    1337
                }
                result = colAndreasService.rayCastReflectionVector(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return Collision.WithObject") {
                assertThat(result)
                        .isEqualTo(
                                RayCastReflectionVectorResult.Collision.WithObject(
                                        1337,
                                        coordinates = vector3DOf(x = 7f, y = 8f, z = 9f),
                                        reflectionVector = vector3DOf(x = 10f, y = 11f, z = 12f)
                                )
                        )
            }
        }
    }

    describe("rayCastLineNormalVector") {
        context("no collision") {
            lateinit var result: RayCastLineNormalVectorResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLineNormal(
                            startx = 1f,
                            starty = 2f,
                            startz = 3f,
                            endx = 4f,
                            endy = 5f,
                            endz = 6f,
                            x = any(),
                            y = any(),
                            z = any(),
                            nx = any(),
                            ny = any(),
                            nz = any()
                    )
                } returns 0
                result = colAndreasService.rayCastLineNormalVector(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return NoCollision") {
                assertThat(result)
                        .isEqualTo(RayCastLineNormalVectorResult.NoCollision)
            }
        }

        context("collision with water") {
            lateinit var result: RayCastLineNormalVectorResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLineNormal(
                            startx = 1f,
                            starty = 2f,
                            startz = 3f,
                            endx = 4f,
                            endy = 5f,
                            endz = 6f,
                            x = any(),
                            y = any(),
                            z = any(),
                            nx = any(),
                            ny = any(),
                            nz = any()
                    )
                } answers {
                    arg<MutableFloatCell>(6).value = 7f
                    arg<MutableFloatCell>(7).value = 8f
                    arg<MutableFloatCell>(8).value = 9f
                    arg<MutableFloatCell>(9).value = 10f
                    arg<MutableFloatCell>(10).value = 11f
                    arg<MutableFloatCell>(11).value = 12f
                    ColAndreasConstants.WATER_OBJECT
                }
                result = colAndreasService.rayCastLineNormalVector(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return Collision.WithWater") {
                assertThat(result)
                        .isEqualTo(
                                RayCastLineNormalVectorResult.Collision.WithWater(
                                        coordinates = vector3DOf(x = 7f, y = 8f, z = 9f),
                                        normalVector = vector3DOf(x = 10f, y = 11f, z = 12f)
                                )
                        )
            }
        }

        context("collision with object") {
            lateinit var result: RayCastLineNormalVectorResult
            beforeEach {
                every {
                    colAndreasNativeFunctions.rayCastLineNormal(
                            startx = 1f,
                            starty = 2f,
                            startz = 3f,
                            endx = 4f,
                            endy = 5f,
                            endz = 6f,
                            x = any(),
                            y = any(),
                            z = any(),
                            nx = any(),
                            ny = any(),
                            nz = any()
                    )
                } answers {
                    arg<MutableFloatCell>(6).value = 7f
                    arg<MutableFloatCell>(7).value = 8f
                    arg<MutableFloatCell>(8).value = 9f
                    arg<MutableFloatCell>(9).value = 10f
                    arg<MutableFloatCell>(10).value = 11f
                    arg<MutableFloatCell>(11).value = 12f
                    1337
                }
                result = colAndreasService.rayCastLineNormalVector(vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
            }

            it("should return Collision.WithObject") {
                assertThat(result)
                        .isEqualTo(
                                RayCastLineNormalVectorResult.Collision.WithObject(
                                        1337,
                                        coordinates = vector3DOf(x = 7f, y = 8f, z = 9f),
                                        normalVector = vector3DOf(x = 10f, y = 11f, z = 12f)
                                )
                        )
            }
        }
    }

    describe("contactTest") {
        listOf(true, false).forEach { expectedResult ->
            context("result is $expectedResult") {
                var result: Boolean? = null

                beforeEach {
                    every {
                        colAndreasNativeFunctions.contactTest(1337, 1f, 2f, 3f, 4f, 5f, 6f)
                    } returns expectedResult
                    result = colAndreasService.contactTest(1337, vector3DOf(1f, 2f, 3f), vector3DOf(4f, 5f, 6f))
                }

                it("should return $expectedResult") {
                    assertThat(result)
                            .isEqualTo(expectedResult)
                }
            }
        }
    }

    describe("eulerToQuaternion") {
        lateinit var quaternion: Quaternion

        beforeEach {
            every { colAndreasNativeFunctions.eulerToQuat(1f, 2f, 3f, any(), any(), any(), any()) } answers {
                arg<MutableFloatCell>(3).value = 4f
                arg<MutableFloatCell>(4).value = 5f
                arg<MutableFloatCell>(5).value = 6f
                arg<MutableFloatCell>(6).value = 7f
                1
            }
            quaternion = colAndreasService.eulerToQuaternion(vector3DOf(1f, 2f, 3f))
        }

        it("should return quaternion") {
            assertThat(quaternion)
                    .isEqualTo(quaternionOf(x = 4f, y = 5f, z = 6f, w = 7f))
        }
    }

    describe("quaternionToEuler") {
        lateinit var rotation: Vector3D

        beforeEach {
            every { colAndreasNativeFunctions.quatToEuler(1f, 2f, 3f, 4f, any(), any(), any()) } answers {
                arg<MutableFloatCell>(4).value = 5f
                arg<MutableFloatCell>(5).value = 6f
                arg<MutableFloatCell>(6).value = 7f
                1
            }
            rotation = colAndreasService.quaternionToEuler(quaternionOf(1f, 2f, 3f, 4f))
        }

        it("should return quaternion") {
            assertThat(rotation)
                    .isEqualTo(vector3DOf(x = 5f, y = 6f, z = 7f))
        }
    }

    describe("getModelBoundingSphere") {
        lateinit var sphere: Sphere

        beforeEach {
            every { colAndreasNativeFunctions.getModelBoundingSphere(1337, any(), any(), any(), any()) } answers {
                arg<MutableFloatCell>(1).value = 1f
                arg<MutableFloatCell>(2).value = 2f
                arg<MutableFloatCell>(3).value = 3f
                arg<MutableFloatCell>(4).value = 4f
                true
            }
            sphere = colAndreasService.getModelBoundingSphere(1337)
        }

        it("should return bounding sphere") {
            assertThat(sphere)
                    .isEqualTo(sphereOf(x = 1f, y = 2f, z = 3f, radius = 4f))
        }
    }

    describe("getModelBoundingBox") {
        lateinit var box: Box

        beforeEach {
            every {
                colAndreasNativeFunctions.getModelBoundingBox(
                        1337,
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        any()
                )
            } answers {
                arg<MutableFloatCell>(1).value = 1f
                arg<MutableFloatCell>(2).value = 2f
                arg<MutableFloatCell>(3).value = 3f
                arg<MutableFloatCell>(4).value = 4f
                arg<MutableFloatCell>(5).value = 5f
                arg<MutableFloatCell>(6).value = 6f
                true
            }
            box = colAndreasService.getModelBoundingBox(1337)
        }

        it("should return bounding box") {
            assertThat(box)
                    .isEqualTo(
                            boxOf(
                                    minX = 1f,
                                    minY = 2f,
                                    minZ = 3f,
                                    maxX = 4f,
                                    maxY = 5f,
                                    maxZ = 6f
                            )
                    )
        }
    }
})