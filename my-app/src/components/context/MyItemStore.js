import React, { Component } from 'react';

const MyItemContext = React.createContext();
const MyItemConsumer = MyItemContext.Consumer;

export class MyItemStore extends Component {
  state = {
    item: {}
  };

  execute = (command) => {
    //todo: command.execute -- handled by aggregate store offline/online/sync
    this.setState(state => ({ item: state.item }));
  };

  render() {
    // Pass down the state and the execute action
    return (
      <MyItemContext.Provider //todo: of AggregateContext.Provider
        value={{ item: this.state.item, onQueue: this.execute }}
      >
        {this.props.children}
      </MyItemContext.Provider>
    );
  }
}

export {MyItemStore as Default, MyItemConsumer}