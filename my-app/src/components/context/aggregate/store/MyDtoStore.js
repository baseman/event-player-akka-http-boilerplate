import React, { Component } from 'react';

const MyDtoContext = React.createContext();
const MyDtoConsumer = MyDtoContext.Consumer;

// todo: RootContext
class MyDtoStore extends Component {
    state = {
        items: [],
        isOnline: false,
        err: null
    };

    connected = () => {
        if (this.state.isOnline) {
            return;
        }

        this.setState((state) => ({
            items: state.items,
            isOnline: true,
            err: null
        }));
    };

    disconnected = () => {
        this.setState((state) => ({
            items: state.items,
            isOnline: false,
            err: null
        }));
    };

    sync = (items) => {
        this.setState((/*state*/) => ({
            items: items,
            isOnline: true,
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

    render() {
        return (
            <MyDtoContext.Provider value={{
                items: this.state.items,
                isOnline: this.state.isOnline,
                err: this.state.err,
                onConnected: this.connected,
                onDisconnected: this.disconnected,
                onSync: this.sync,
                onError: this.error
            }}>
                {this.props.children}
            </MyDtoContext.Provider>
        )
    }
}

export {MyDtoStore, MyDtoConsumer}