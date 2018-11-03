import React from 'react';

import MyTask from "./MyTask";
import MyItems from "./MyItems";

import {MyDtoConsumer} from "./context/aggregate/store/MyDtoStore"

import {MyDtoProxy} from "./context/aggregate/proxy/MyDtoProxy"
import {CommandProxy} from "./context/aggregate/proxy/CommandProxy";
import {CommandConsumer} from "./context/aggregate/store/CommandStore";

function MyAppConsumer({children}) {
    return (<CommandConsumer>
            {commandStore => {

                let commandProxy = CommandProxy.init({
                    onQueue: commandStore.onQueue,
                    onProcessing: commandStore.onProcessing,
                    onProcessed: commandStore.onProcessed,
                    onError: commandStore.onError
                });

                return <MyDtoConsumer>
                    { myDtoStore => {

                        MyDtoProxy.init({
                            onConnected: myDtoStore.onConnected,
                            onDisconnected: myDtoStore.onDisconnected,
                            onSync: myDtoStore.onSync,
                            onError: myDtoStore.onError
                        });

                        return children({myDtoStore, commandStore, commandProxy})
                    }
                }
                </MyDtoConsumer>
            }
        }
     </CommandConsumer>)
}

const MyDto = () => (
    <div className="My-Items">
        <MyAppConsumer>
            {({myDtoStore, commandStore, commandProxy}) =>
                <div>
                    <MyTask item={null} onCommandQueue={commandProxy.queue}/>

                    **{myDtoStore.isOnline ? "Online" : "Offline"}**
                    {/*todo: myDtoProxy.isRequesting*/}
                    {myDtoStore.err ? myDtoStore.err.toString() : ""}
                    <MyItems items={myDtoStore.items} onCommandQueue={commandProxy.queue}/>
                </div>
            }
            </MyAppConsumer>
    </div>
);

export default MyDto;