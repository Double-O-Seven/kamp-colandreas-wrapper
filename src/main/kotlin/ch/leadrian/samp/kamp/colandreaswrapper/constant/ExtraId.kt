package ch.leadrian.samp.kamp.colandreaswrapper.constant

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasConstants
import ch.leadrian.samp.kamp.core.api.constants.ConstantValue
import ch.leadrian.samp.kamp.core.api.constants.ConstantValueRegistry

enum class ExtraId(override val value: Int) : ConstantValue<Int> {
    EXTRA_1(ColAndreasConstants.EXTRA_1),
    EXTRA_2(ColAndreasConstants.EXTRA_2),
    EXTRA_3(ColAndreasConstants.EXTRA_3),
    EXTRA_4(ColAndreasConstants.EXTRA_4),
    EXTRA_5(ColAndreasConstants.EXTRA_5),
    EXTRA_6(ColAndreasConstants.EXTRA_6),
    EXTRA_7(ColAndreasConstants.EXTRA_7),
    EXTRA_8(ColAndreasConstants.EXTRA_8),
    EXTRA_9(ColAndreasConstants.EXTRA_9),
    EXTRA_10(ColAndreasConstants.EXTRA_10);

    companion object : ConstantValueRegistry<Int, ExtraId>(ExtraId.values())

}