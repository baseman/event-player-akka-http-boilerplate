import React from 'react';

import MyItems from "./MyItems";

import {MyDtoConsumer} from "./context/aggregate/store/MyDtoStore"
import {CommandConsumer} from "./context/aggregate/store/CommandStore";

import {MySyncProxy} from "./context/aggregate/proxy/MyDtoProxy"
import {CommandProxy} from "./context/aggregate/proxy/CommandProxy";

function MyAppConsumer({children}) {
    return (<CommandConsumer>
            {(command) => {

                let commandProxy = CommandProxy.init({
                    onQueue: command.onQueue,
                    onProcessing: command.onProcessing,
                    onProcessed: command.onProcessed,
                    onError: command.onError
                });

                return <MyDtoConsumer> {
                    (my) => {

                        MySyncProxy.init({
                            pollMilliseconds: 3000,
                            onConnected: my.onConnected,
                            onDisconnected: my.onDisconnected,
                            onSync: my.onSync,
                            onError: my.onError
                        });

                        children({my, command, commandProxy})
                    }
                }
                </MyDtoConsumer>
            }
        }
     </CommandConsumer>)
}

const MyDto = () => (
    <div className="Account-Items">
        <MyAppConsumer>
            {({my, command, commandProxy}) =>
                <div>
                    **{my.isOnline ? "Online" : "Offline"}**
                    {/*todo: myDtoProxy.isRequesting*/}
                    {my.err ? my.err.toString() : ""}
                    <MyItems items={my.items} onCommandQueue={commandProxy.queue}/>
                </div>
            }
            </MyAppConsumer>
    </div>
);

export default MyDto;