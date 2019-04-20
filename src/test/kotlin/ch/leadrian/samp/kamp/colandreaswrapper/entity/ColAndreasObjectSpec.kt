package ch.leadrian.samp.kamp.colandreaswrapper.entity

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasNativeFunctions
import ch.leadrian.samp.kamp.core.api.data.MutableVector3D
import ch.leadrian.samp.kamp.core.api.data.mutableVector3DOf
import ch.leadrian.samp.kamp.core.api.data.vector3DOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class ColAndreasObjectSpec : Spek({
    val colAndreasNativeFunctions by memoized { mockk<ColAndreasNativeFunctions>() }
    val colAndreasObjectIndex = ColAndreasObjectIndex.valueOf(69)
    lateinit var colAndreasObject: ColAndreasObject

    beforeEachTest {
        every {
            colAndreasNativeFunctions.createObject(
                    modelid = 1337,
                    x = 1f,
                    y = 2f,
                    z = 3f,
                    rx = 4f,
                    ry = 5f,
                    rz = 6f,
                    add = true
            )
        } returns colAndreasObjectIndex.value
        colAndreasObject = ColAndreasObject(
                modelId = 1337,
                coordinates = mutableVector3DOf(1f, 2f, 3f),
                rotation = mutableVector3DOf(4f, 5f, 6f),
                isManaged = true,
                nativeFunctions = colAndreasNativeFunctions
        )
    }

    describe("id") {
        it("should be set") {
            assertThat(colAndreasObject.id)
                    .isEqualTo(colAndreasObjectIndex)
        }
    }

    describe("coordinates") {
        it("should initialize") {
            assertThat(colAndreasObject.coordinates)
                    .isEqualTo(vector3DOf(1f, 2f, 3f))
                    .isNotInstanceOf(MutableVector3D::class.java)
        }

        describe("setter") {
            beforeEach {
                every { colAndreasNativeFunctions.setObjectPos(any(), any(), any(), any()) } returns true
                colAndreasObject.coordinates = mutableVector3DOf(11f, 22f, 33f)
            }

            it("should update coordinates") {
                assertThat(colAndreasObject.coordinates)
                        .isEqualTo(vector3DOf(11f, 22f, 33f))
                        .isNotInstanceOf(MutableVector3D::class.java)
            }

            it("should call colAndreasNativeFunctions.setObjectPos") {
                verify {
                    colAndreasNativeFunctions.setObjectPos(
                            index = colAndreasObjectIndex.value,
                            x = 11f,
                            y = 22f,
                            z = 33f
                    )
                }
            }
        }
    }

    describe("rotation") {
        it("should initialize") {
            assertThat(colAndreasObject.rotation)
                    .isEqualTo(vector3DOf(4f, 5f, 6f))
                    .isNotInstanceOf(MutableVector3D::class.java)
        }

        describe("setter") {
            beforeEach {
                every { colAndreasNativeFunctions.setObjectRot(any(), any(), any(), any()) } returns true
                colAndreasObject.rotation = mutableVector3DOf(11f, 22f, 33f)
            }

            it("should update rotation") {
                assertThat(colAndreasObject.rotation)
                        .isEqualTo(vector3DOf(11f, 22f, 33f))
                        .isNotInstanceOf(MutableVector3D::class.java)
            }

            it("should call colAndreasNativeFunctions.setObjectRot") {
                verify {
                    colAndreasNativeFunctions.setObjectRot(
                            index = colAndreasObjectIndex.value,
                            rx = 11f,
                            ry = 22f,
                            rz = 33f
                    )
                }
            }
        }
    }

    describe("destroy") {
        beforeEach {
            every { colAndreasNativeFunctions.destroyObject(any()) } returns 1
            colAndreasObject.destroy()
        }

        it("should call colAndreasNativeFunctions.destroyObject") {
            verify { colAndreasNativeFunctions.destroyObject(colAndreasObjectIndex.value) }
        }
    }

})