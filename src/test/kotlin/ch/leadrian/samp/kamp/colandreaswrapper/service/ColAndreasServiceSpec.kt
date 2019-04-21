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
import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObject
import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObjectIndex
import ch.leadrian.samp.kamp.colandreaswrapper.entity.ColAndreasObjectRegistry
import ch.leadrian.samp.kamp.core.api.amx.MutableFloatCell
import ch.leadrian.samp.kamp.core.api.data.sphereOf
import ch.leadrian.samp.kamp.core.api.data.vector3DOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal object ColAndreasServiceSpec : Spek({
    val colAndreasNativeFunctions by memoized { mockk<ColAndreasNativeFunctions>() }
    val colAndreasObjectRegistry by memoized { mockk<ColAndreasObjectRegistry>() }
    val colAndreasService by memoized { ColAndreasService(colAndreasNativeFunctions, colAndreasObjectRegistry) }

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
            lateinit var result: RayCastResult
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
                        .isEqualTo(NoCollision)
            }
        }

        context("collision with water") {
            lateinit var result: RayCastResult
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

            it("should return CollisionWithWater") {
                assertThat(result)
                        .isEqualTo(CollisionWithWater(vector3DOf(x = 7f, y = 8f, z = 9f)))
            }
        }

        context("collision with object") {
            lateinit var result: RayCastResult
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

            it("should return CollisionWithObject") {
                assertThat(result)
                        .isEqualTo(CollisionWithObject(1337, vector3DOf(x = 7f, y = 8f, z = 9f)))
            }
        }
    }

    describe("rayCastLineId") {
        context("no collision") {
            lateinit var result: RayCastResult
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
                        .isEqualTo(NoCollision)
            }
        }

        context("collision with water or object") {
            lateinit var result: RayCastResult
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

            it("should return CollisionWithObjectOrWater") {
                assertThat(result)
                        .isEqualTo(CollisionWithObjectOrWater(vector3DOf(x = 7f, y = 8f, z = 9f)))
            }
        }

        context("collision with ColAndreas object") {
            val colAndreasObject by memoized {
                mockk<ColAndreasObject> {
                    every { id } returns ColAndreasObjectIndex.valueOf(69)
                }
            }
            lateinit var result: RayCastResult

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

            it("should return CollisionWithColAndreasObject") {
                assertThat(result)
                        .isEqualTo(CollisionWithColAndreasObject(colAndreasObject, vector3DOf(x = 7f, y = 8f, z = 9f)))
            }
        }
    }

    describe("rayCastLineExtraId") {
        context("no collision") {
            lateinit var result: RayCastResult
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
                        .isEqualTo(NoCollision)
            }
        }

        context("collision with water or object") {
            lateinit var result: RayCastResult
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

            it("should return CollisionWithObjectOrWater") {
                assertThat(result)
                        .isEqualTo(CollisionWithObjectOrWater(vector3DOf(x = 7f, y = 8f, z = 9f)))
            }
        }

        context("collision with ColAndreas object") {
            val colAndreasObject by memoized {
                mockk<ColAndreasObject> {
                    every { id } returns ColAndreasObjectIndex.valueOf(69)
                }
            }
            lateinit var result: RayCastResult

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

            it("should return CollisionWithColAndreasObject") {
                assertThat(result)
                        .isEqualTo(CollisionWithColAndreasObject(colAndreasObject, vector3DOf(x = 7f, y = 8f, z = 9f)))
            }
        }
    }
})