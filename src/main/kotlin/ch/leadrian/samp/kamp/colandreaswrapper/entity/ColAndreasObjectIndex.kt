package ch.leadrian.samp.kamp.colandreaswrapper.entity

import ch.leadrian.samp.kamp.core.api.entity.id.EntityId

data class ColAndreasObjectIndex internal constructor(override val value: Int) : EntityId {

    companion object {

        val INVALID = ColAndreasObjectIndex(-1)

        private val colAndreasObjectIndexes: Array<ColAndreasObjectIndex> = (0..10000)
                .map { ColAndreasObjectIndex(it) }
                .toTypedArray()

        fun valueOf(value: Int): ColAndreasObjectIndex =
                when {
                    0 <= value && value < colAndreasObjectIndexes.size -> colAndreasObjectIndexes[value]
                    value == INVALID.value -> INVALID
                    else -> ColAndreasObjectIndex(value)
                }

    }

}