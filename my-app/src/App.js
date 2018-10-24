import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import MyItems from "./components/MyItems";
import {CommandStore} from "./components/context/aggregate/CommandStore";
import {MyItemsListStore} from "./components/context/MyItemsListStore";

function MyAppStore({children}) {
    return (
        <CommandStore>
            <MyItemsListStore>
                {children}
            </MyItemsListStore>
        </CommandStore>
    )
}

class App extends Component {
  render() {
      return (
          <div className="App">
              <header className="App-header">
                  <img src={logo} className="App-logo" alt="logo"/>
                  <p>
                      Edit <code>src/App.js</code> and save to reload.
                  </p>
                  <a
                      className="App-link"
                      href="https://reactjs.org"
                      target="_blank"
                      rel="noopener noreferrer"
                  >
                      Learn React
                  </a>
              </header>
              <MyAppStore>
                  <MyItems/>
              </MyAppStore>
          </div>
      );
  }
}

export default App;
