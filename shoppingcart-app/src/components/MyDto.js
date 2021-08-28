import React from 'react';

import MyTask from "./tasks/MyTask";
import MyItems from "./MyItems";

import {MyDtoConsumer} from "./context/root/store/MyDtoStore"

import {MyDtoProxy} from "./context/root/proxy/MyDtoProxy"
import {CommandProxy} from "./context/root/proxy/CommandProxy";
import {CommandConsumer} from "./context/root/store/CommandStore";

function MyAppConsumer({children}) {
    return (<CommandConsumer>
            {commandStore => {

                CommandProxy.init(commandStore);

                return <MyDtoConsumer>
                    { myDtoStore => {

                        MyDtoProxy.init({
                            onConnected: myDtoStore.onConnected,
                            onDisconnected: myDtoStore.onDisconnected,
                            onSync: myDtoStore.onSync,
                            onError: myDtoStore.onError
                        });

                        return children({myDtoStore, commandStore})
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
            {({myDtoStore, commandStore}) =>{

                return (<div>
                    <MyTask item={null} onCommandQueue={commandStore.onQueue}/>

                    **{myDtoStore.isOnline ? "Online" : "Offline"}**
                    {/*todo: myDtoProxy.isRequesting*/}
                    {myDtoStore.err ? myDtoStore.err.toString() : ""}
                    <MyItems items={myDtoStore.items} onCommandQueue={commandStore.onQueue}/>
                </div>)
            }
            }
            </MyAppConsumer>
    </div>
);

export default MyDto;