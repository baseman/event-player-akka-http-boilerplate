import React, { Component } from 'react';

const MyItemsListContext = React.createContext();
const MyItemsListConsumer = MyItemsListContext.Consumer;

// todo: AggregateContext
class MyItemsListStore extends Component {
    state = {
        items: [],
        isLoading: false, //todo: determine if this should be onloading?
        err: null
    };

    sync = (fThenableGetItems) => {

        if (this.state.isLoading) {
            return;
        }

        let _err = null;
        let dtoItems = this.state.items;
        return fThenableGetItems().then(result => {
            dtoItems = result.items
        }).catch((err) => {
            _err = err
        }).finally(() => {
            this.setState((/*state*/) => ({
                items: dtoItems,
                isLoading: false,
                err: _err
            }));
        });
    };

    //todo: of AggregateContext.Provider
    render() {
        return (
            <MyItemsListContext.Provider value={{
                my: {
                    items: this.state.items,
                    isLoading: this.state.isLoading,
                    err: this.state.err,
                    onSync: this.sync
                }
            }}>
                {this.props.children}
            </MyItemsListContext.Provider>
        )
    }
}

export {MyItemsListStore, MyItemsListConsumer}