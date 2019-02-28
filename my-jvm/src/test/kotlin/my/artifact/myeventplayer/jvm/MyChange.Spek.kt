package my.artifact.myeventplayer.jvm

import co.remotectrl.eventplayer.*
import my.artifact.myeventplayer.common.aggregate.MyAggregate
import my.artifact.myeventplayer.common.command.MyChangeCommand
import my.artifact.myeventplayer.common.command.MyCreateCommand
import my.artifact.myeventplayer.common.event.MyChangedEvent
import my.artifact.myeventplayer.common.event.MyCreatedEvent
import my.artifact.myeventplayer.jvm.assert.AssertUtil
import org.amshove.kluent.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class MyAggregateTest : Spek({

    describe("MyAggregate MyCreate") {

        val aggregateIdVal = 1
        val aggregateId = AggregateId<MyAggregate>(aggregateIdVal)

        val actual = MyAggregate(
                AggregateLegend(aggregateId, 1),
                "blah"
        )

        it("should try to validate Change Command command input") {
            AssertUtil.assertExecution(
                    MyCreateCommand("").executeOn(actual),
                    PlayExecution.Invalidated(items = arrayOf(
                            PlayInvalidInput("myInitialVal should not be empty")
                    ))
            )
        }

        val evtIdVal = 0
        val evtId = EventId<MyAggregate>(evtIdVal)

        val eventLegend = EventLegend(evtId, aggregateId, 2)

        it("should produce Changed Event on successful Commit Command") {

            AssertUtil.assertExecution(
                    MyCreateCommand("initial blah").executeOn(actual),
                    PlayExecution.Validated(event = MyCreatedEvent(eventLegend, "initial blah"))
            )
        }

        fun assertModel(actual: MyAggregate, expected: MyAggregate) {
            actual.myVal shouldEqual expected.myVal
            actual.legend.latestVersion shouldEqual expected.legend.latestVersion
        }

        it("should apply Changed Event to MyAggregate") {
            val evt = MyCreatedEvent(EventLegend(evtId, aggregateId, 2), "blah changed")

            val actualMutableAggregate = MutableAggregate(actual)
            evt.applyTo(actualMutableAggregate)

            AssertUtil.assertAggregateEvent(actualMutableAggregate.aggregate.legend, evt)

            val expected = MyAggregate(AggregateLegend(aggregateId, 2), "blah changed")

            assertModel(actualMutableAggregate.aggregate, expected)
        }

    }

    describe("MyAggregate MyChange") {

        val aggregateIdVal = 1
        val aggregateId = AggregateId<MyAggregate>(aggregateIdVal)

        val actual = MyAggregate(
                AggregateLegend(aggregateId, 1),
                "blah"
        )

        it("should try to validate Change my.artifact.myeventplayer.common.aggregate.command input") {

            AssertUtil.assertExecution(
                    MyChangeCommand("").executeOn(actual),
                    PlayExecution.Invalidated(items = arrayOf(
                            PlayInvalidInput("myInitialVal should not be empty")
                    ))
            )

        }

        val evtIdVal = 0
        val evtId = EventId<MyAggregate>(evtIdVal)

        val eventLegend = EventLegend(evtId, aggregateId, 2)

        it("should produce Changed my.artifact.myeventplayer.common.aggregate.event on successful Commit my.artifact.myeventplayer.common.aggregate.command") {

            AssertUtil.assertExecution(
                    MyChangeCommand("change blah").executeOn(actual),
                    PlayExecution.Validated(event = MyChangedEvent(eventLegend, "change blah"))
            )
        }

        fun assertModel(actual: MyAggregate, expected: MyAggregate) {
            actual.myVal shouldEqual expected.myVal
            actual.legend.latestVersion shouldEqual expected.legend.latestVersion
        }

        it("should apply Changed my.artifact.myeventplayer.common.aggregate.event to the MyAggregate") {
            val evt = MyChangedEvent(EventLegend(evtId, aggregateId, 2), "blah changed")

            val actualMutableAggregate = MutableAggregate(actual)
            evt.applyTo(actualMutableAggregate)

            AssertUtil.assertAggregateEvent(actualMutableAggregate.aggregate.legend, evt)

            val expected = MyAggregate(AggregateLegend(aggregateId, 2), "blah changed")

            assertModel(actualMutableAggregate.aggregate, expected)
        }

    }

})
