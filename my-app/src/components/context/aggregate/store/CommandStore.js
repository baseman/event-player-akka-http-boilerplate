import {Component} from "react";
import React from "react";

const CommandContext = React.createContext();
const CommandConsumer = CommandContext.Consumer;

class CommandStore extends Component {
    state = {
        //todo: possible create js map of commands per aggregateId to parallelize cmd exec
        items: [],
        deadLetterItems: [],
        isProcessing: false,
        err: null
    };

    queue = (command) => {
        this.setState((state) => ({
            items: state.items.concat(command), //add items to end of queue
            deadLetterItems: state.deadLetterItems,
            isProcessing: state.isProcessing,
            err: null
        }));
    };

    processing = () => {
        this.setState((state) => ({
            items: state.items,
            deadLetterItems: state.deadLetterItems,
            isProcessing: true,
            err: null
        }));
    };

    processed = () => {
        this.setState((state) => {
            state.items.shift(); //remove next completed command from queue

            return ({
                items: state.items,
                deadLetterItems: state.deadLetterItems,
                isProcessing: false,
                err: null
            })
        })
    };

    error = (err, command) => {
        this.setState((state) => {

            return {
                items: state.items.filter(item => item !== command),
                deadLetterItems: state.deadLetterItems.concat({err: err, command: command}), //queue error and dead letter item
                isProcessing: false,
                err: err
            }
        });
    };

    // todo: retry = (command) => {
    //     this.setState((state) => {
    //
    //         let retryIndex = 0;
    //         let deadLetterItems = [];
    //         for(let i =0; i < state.deadLetterItems.length; i++){
    //             if(state.deadLetterItems.command !== command){
    //                 deadLetterItems.push(state.deadLetterItems[i])
    //             }
    //             else{
    //                 retryIndex = i;
    //             }
    //         }
    //
    //         return {
    //             items: state.items.concat(state.deadLetterItems[retryIndex].command),
    //             deadLetterItems: deadLetterItems,
    //             isProcessing: false,
    //             err: null
    //         }
    //     });
    // };

    removeDeadLetter = (command) => {
        this.setState((state) => {

            return {
                items: state.items,
                deadLetterItems: state.deadLetterItems.filter((item) => item !== command),
                isProcessing: false,
                err: null
            }
        });
    };

    render() {
        return (
            <CommandContext.Provider value={{
                items: this.state.items,
                nextProcessItem: this.state.items[0],
                deadLetterItems: this.state.deadLetterItems,
                isProcessing: this.state.isProcessing,
                err: this.state.err,
                onQueue: this.queue,
                onProcessing: this.processing,
                onProcessed: this.processed,
                onError: this.error,
                // onRetry: this.retry,
                onRemoveDeadLetter: this.removeDeadLetter,
                //todo: possible onSync -- save draft command? -- sync with commandProxy
            }}>
                {this.props.children}
            </CommandContext.Provider>
        )
    }
}

export {CommandStore, CommandConsumer}