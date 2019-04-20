package ch.leadrian.samp.kamp.colandreaswrapper.entity

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal object ColAndreasObjectRegistryProviderSpec : Spek({

    val colAndreasObjectRegistryProvider by memoized { ColAndreasObjectRegistryProvider() }

    describe("get") {
        context("capacity is default value") {
            lateinit var colAndreasObjectRegistry: ColAndreasObjectRegistry

            beforeEach {
                colAndreasObjectRegistry = colAndreasObjectRegistryProvider.get()
            }

            it("should have default capacity") {
                assertThat(colAndreasObjectRegistry.capacity)
                        .isEqualTo(50000)
            }
        }

        context("capacity is set") {
            lateinit var colAndreasObjectRegistry: ColAndreasObjectRegistry

            beforeEach {
                colAndreasObjectRegistryProvider.registryCapacity = 1234
                colAndreasObjectRegistry = colAndreasObjectRegistryProvider.get()
            }

            it("should have default capacity") {
                assertThat(colAndreasObjectRegistry.capacity)
                        .isEqualTo(1234)
            }
        }

        context("should return singleton instance") {
            lateinit var colAndreasObjectRegistry1: ColAndreasObjectRegistry
            lateinit var colAndreasObjectRegistry2: ColAndreasObjectRegistry

            beforeEach {
                colAndreasObjectRegistry1 = colAndreasObjectRegistryProvider.get()
                colAndreasObjectRegistry2 = colAndreasObjectRegistryProvider.get()
            }

            it("should have default capacity") {
                assertThat(colAndreasObjectRegistry1)
                        .isSameAs(colAndreasObjectRegistry2)
            }
        }
    }
})