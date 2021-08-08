package co.remotectrl.ctrl.event

interface CtrlCommand<TRoot : CtrlRoot<TRoot>> {

    fun getEventLegend(rootId: RootId<TRoot>, version: Int): EventLegend<TRoot> {
        return EventLegend(
                0.toString(),
                rootId.value,
                version
        )
    }

    fun getEvent(eventLegend: EventLegend<TRoot>): CtrlEvent<TRoot>

    fun validate(root: TRoot, validation: CtrlValidation)

    fun executeOn(root: TRoot): CtrlExecution<TRoot, CtrlEvent<TRoot>, CtrlInvalidation> {
        val validation = CtrlValidation(mutableListOf())

        validate(root, validation)

        val validatedItems = validation.invalidInputItems.toTypedArray()

        return if (validatedItems.isNotEmpty()) CtrlExecution.Invalidated(items = validatedItems)
        else {
            CtrlExecution.Validated(
                    event = getEvent(
                            getEventLegend(rootId = root.legend.rootId, version = root.legend.latestVersion + 1)
                    )
            )
        }
    }
}

@Suppress("FINAL_UPPER_BOUND")
sealed class CtrlExecution<
        TRoot : CtrlRoot<TRoot>,
        out TEvent : CtrlEvent<TRoot>,
        out TInvalid : CtrlInvalidation
        > {
    data class Validated<TRoot : CtrlRoot<TRoot>, out TEvent : CtrlEvent<TRoot>>(
        val event: CtrlEvent<TRoot>
    ) : CtrlExecution<TRoot, TEvent, Nothing>()

    data class Invalidated<TRoot : CtrlRoot<TRoot>>(
        val items: Array<CtrlInvalidInput>
    ) : CtrlExecution<TRoot, Nothing, CtrlInvalidation>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Invalidated<*>

            if (!items.contentEquals(other.items)) return false

            return true
        }

        override fun hashCode(): Int {
            return items.contentHashCode()
        }
    }
}

data class CtrlInvalidation(val items: Array<CtrlInvalidInput>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CtrlInvalidation

        if (!items.contentEquals(other.items)) return false

        return true
    }

    override fun hashCode(): Int {
        return items.contentHashCode()
    }
}

data class CtrlValidation(internal val invalidInputItems: MutableList<CtrlInvalidInput>) {
    fun assert(that: () -> Boolean, description: String) {
        when {
            !that() -> invalidInputItems.add(CtrlInvalidInput(description = description))
        }
    }
}

data class CtrlInvalidInput(val description: String)
