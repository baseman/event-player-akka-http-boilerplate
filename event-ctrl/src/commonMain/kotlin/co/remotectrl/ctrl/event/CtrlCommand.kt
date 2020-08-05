package co.remotectrl.ctrl.event

interface CtrlCommand<TAggregate : CtrlAggregate<TAggregate>> {

    fun getEventLegend(aggregateId: AggregateId<TAggregate>, version: Int): EventLegend<TAggregate> {
        return EventLegend(
                0.toString(),
                aggregateId.value,
                version
        )
    }

    fun getEvent(eventLegend: EventLegend<TAggregate>): CtrlEvent<TAggregate>

    fun validate(aggregate: TAggregate, validation: CtrlValidation)

    fun executeOn(aggregate: TAggregate): CtrlExecution<TAggregate, CtrlEvent<TAggregate>, CtrlInvalidation> {
        val validation = CtrlValidation(mutableListOf())

        validate(aggregate, validation)

        val validatedItems = validation.invalidInputItems.toTypedArray()

        return if (validatedItems.isNotEmpty()) CtrlExecution.Invalidated(items = validatedItems)
        else {
            CtrlExecution.Validated(
                    event = getEvent(
                            getEventLegend(aggregateId = aggregate.legend.aggregateId, version = aggregate.legend.latestVersion + 1)
                    )
            )
        }
    }
}

@Suppress("FINAL_UPPER_BOUND")
sealed class CtrlExecution<
        TAggregate : CtrlAggregate<TAggregate>,
        out TEvent : CtrlEvent<TAggregate>,
        out TInvalid : CtrlInvalidation
        > {
    data class Validated<TAggregate : CtrlAggregate<TAggregate>, out TEvent : CtrlEvent<TAggregate>>(
        val event: CtrlEvent<TAggregate>
    ) : CtrlExecution<TAggregate, TEvent, Nothing>()

    data class Invalidated<TAggregate : CtrlAggregate<TAggregate>>(
        val items: Array<CtrlInvalidInput>
    ) : CtrlExecution<TAggregate, Nothing, CtrlInvalidation>() {
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
