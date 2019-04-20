package ch.leadrian.samp.kamp.colandreaswrapper.service

import ch.leadrian.samp.kamp.colandreaswrapper.ColAndreasNativeFunctions
import ch.leadrian.samp.kamp.core.api.amx.AmxNativeFunction12
import ch.leadrian.samp.kamp.core.api.amx.MutableCellArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColAndreasService
@Inject
internal constructor(private val nativeFunctions: ColAndreasNativeFunctions) {

    // [native] int CA_RayCastMultiLine(float StartX, float StartY, float StartZ, float EndX, float EndY, float EndZ, float retx[], float rety[], float retz[], float retdist[], int ModelIDs[], int size);

    private val CA_RayCastMultiLine by AmxNativeFunction12<Float, Float, Float, Float, Float, Float, MutableCellArray, MutableCellArray, MutableCellArray, MutableCellArray, MutableCellArray, Int>()

    fun initializeColAndreas() {
        nativeFunctions.init()
    }
}