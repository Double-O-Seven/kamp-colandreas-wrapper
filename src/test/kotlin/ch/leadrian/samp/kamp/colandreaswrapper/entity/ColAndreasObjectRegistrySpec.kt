package ch.leadrian.samp.kamp.colandreaswrapper.entity

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal object ColAndreasObjectRegistrySpec : Spek({

    describe("capacity") {
        val colAndreasObjectRegistry by memoized {
            ColAndreasObjectRegistry(65536)
        }

        it("should be expected value") {
            assertThat(colAndreasObjectRegistry.capacity)
                    .isEqualTo(65536)
        }
    }
})