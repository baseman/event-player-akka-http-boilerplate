import {Component} from "react";
import React from "react";

const CommandContext = React.createContext();
const CommandConsumer = CommandContext.Consumer;

class CommandStore extends Component {
    state = {
        //todo: possible create js map of commands per aggregateId to parallelize cmd exec
        items: [],
        isPosting: false,
        err: null
    };

    processNext = (fThenablePost) => {

        //todo: move isOnline functionality outside
        if (!this.commandProxy.isOnline) {
            this.setState((state) => ({
                items: state.items,
                isPosting: false,
                err: null
            }));
            return;
        }

        let items = this.state.items;

        if (!(items.length > 0 && !this.state.isPosting)) {
            return;
        }

        let _err = null;
        this.setState((state) => ({
            items: state.items,
            isPosting: true,
            err: _err
        }));
        return fThenablePost(items[0]).catch((err) => {
            _err = err
        }).finally(() => {
            items.shift(); //remove completed command from queue

            //todo: if command.items.length > 0 call this.processNext
            //todo: if command.items.length == 0 call command.processed
            this.setState((/*state*/) => ({
                items: items,
                isPosting: false,
                err: _err
            }));
        });
    };

    queue = (command, fThenablePost) => {
        this.setState((state) => ({
            items: state.items.concat(command),
            isPosting: state.isPosting,
            err: state.err
        }));

        this.processNext(fThenablePost)
    };

    render() {

        return (
                <CommandContext.Provider value={{
                    command: {
                        items: this.state.items,
                        isPosting: this.state.isPosting,
                        err: this.state.err,
                        onQueue: this.queue,
                        onProcessNext: this.processNext,
                        //todo: possible onSync -- save draft command? -- sync with commandProxy
                    }
                }}>
                    {this.props.children}
                </CommandContext.Provider>
        )
    }
}

export {CommandStore, CommandConsumer}