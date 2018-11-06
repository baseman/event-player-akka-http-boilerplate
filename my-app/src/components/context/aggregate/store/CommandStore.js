import {Component} from "react";
import React from "react";

const CommandContext = React.createContext();
const CommandConsumer = CommandContext.Consumer;

class CommandStore extends Component {
    state = {
        //todo: possible create js map of commands per aggregateId to parallelize cmd exec
        items: [],
        isProcessing: false,
        err: null
    };

    queue = (command) => {
        this.setState((state) => ({
            items: state.items.concat(command), //add items to end of queue
            isProcessing: state.isProcessing,
            err: state.err
        }));
    };

    processing = () => {
        this.setState((state) => ({
            items: state.items,
            isProcessing: true,
            err: null
        }));
    };

    processed = () => {
        this.setState((state) => {
            state.items.shift(); //remove next completed command from queue

            return ({
                items: state.items,
                isProcessing: false,
                err: null
            })
        })
    };

    error = (err) => {
        this.setState((state) => ({
            items: state.items,
            isProcessing: false,
            err: err
        }));
    };

    render() {
        return (
            <CommandContext.Provider value={{
                items: this.state.items,
                nextProcessItem: this.state.items[0],
                isProcessing: this.state.isProcessing,
                err: this.state.err,
                onQueue: this.queue,
                onProcessing: this.processing,
                onProcessed: this.processed,
                onError: this.error
                //todo: possible onSync -- save draft command? -- sync with commandProxy
            }}>
                {this.props.children}
            </CommandContext.Provider>
        )
    }
}

export {CommandStore, CommandConsumer}