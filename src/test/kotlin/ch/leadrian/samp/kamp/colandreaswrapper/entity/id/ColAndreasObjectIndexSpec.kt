package ch.leadrian.samp.kamp.colandreaswrapper.entity.id

import org.assertj.core.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal object ColAndreasObjectIndexSpec : Spek({
    describe("valueOf") {
        listOf(-1, 0, 10000, 99999).forEach { value ->
            it("should return ColAndreasObjectIndex with value $value") {
                val fullyControllableNPCId = ColAndreasObjectIndex.valueOf(value)

                Assertions.assertThat(fullyControllableNPCId.value)
                        .isEqualTo(value)
            }
        }

        listOf(-1, 0, 500, 10000).forEach { value ->
            it("should used cached ColAndreasObjectIndex for value $value") {
                val fullyControllableNPCId = ColAndreasObjectIndex.valueOf(value)

                Assertions.assertThat(fullyControllableNPCId)
                        .isSameAs(ColAndreasObjectIndex.valueOf(value))
            }
        }

        listOf(-2, 10001).forEach { value ->
            it("should create new ColAndreasObjectIndex for value $value") {
                val fullyControllableNPCId = ColAndreasObjectIndex.valueOf(value)

                Assertions.assertThat(fullyControllableNPCId)
                        .isNotSameAs(ColAndreasObjectIndex.valueOf(value))
            }
        }
    }
})