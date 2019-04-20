package ch.leadrian.samp.kamp.colandreaswrapper.entity

import ch.leadrian.samp.kamp.core.runtime.entity.registry.EntityRegistry

internal class ColAndreasObjectRegistry(capacity: Int) :
        EntityRegistry<ColAndreasObject, ColAndreasObjectIndex>(arrayOfNulls(capacity))