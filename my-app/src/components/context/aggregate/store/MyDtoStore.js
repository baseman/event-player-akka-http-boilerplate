import React, { Component } from 'react';

const MyDtoContext = React.createContext();
const MyDtoConsumer = MyDtoContext.Consumer;

// todo: AggregateContext
class MyDtoStore extends Component {
    state = {
        items: [],
        isOnline: false,
        err: null
    };

    connect = () => {
        if (this.state.isOnline) {
            return;
        }

        this.setState((state) => ({
            items: state.items,
            isOnline: true,
            err: null
        }));
    };

    disconnect = () => {
        this.setState((state) => ({
            items: state.items,
            isOnline: false,
            err: null
        }));
    };

    error = (err) => {
        this.setState((state) => ({
            items: state.items,
            isOnline: false,
            err: err
        }));
    };

    sync = (items) => {
        this.setState((/*state*/) => ({
            items: items,
            isOnline: true,
            err: null
        }));
    };

    //todo: of AggregateContext.Provider
    render() {
        return (
            <MyDtoContext.Provider value={{
                my: {
                    items: this.state.items,
                    isOnline: this.state.isOnline,
                    err: this.state.err,
                    onConnected: this.connect,
                    onDisconnected: this.disconnect,
                    onSync: this.sync,
                    onError: this.error
                }
            }}>
                {this.props.children}
            </MyDtoContext.Provider>
        )
    }
}

export {MyDtoStore, MyDtoConsumer}