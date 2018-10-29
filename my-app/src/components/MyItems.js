import React from 'react';

import MyItem from "./MyItem";
import MyTaskItem from "./MyTaskItem";

import {MyDtoConsumer} from "./context/aggregate/store/MyDtoStore"
import {CommandConsumer} from "./context/aggregate/store/CommandStore";

import {MySyncProxy} from "./context/aggregate/proxy/MyDtoProxy"

function MyAppConsumer({children}) {
    return (<CommandConsumer>
            {(command) => {

                return <MyDtoConsumer> {
                    (myDtoStore) => {

                        MySyncProxy.init({
                            pollMilliseconds: 3000,
                            onConnected: myDtoStore.onConnected,
                            onDisconnected: myDtoStore.onDisconnected,
                            onSync: myDtoStore.onSync,
                            onError: myDtoStore.onError
                        });

                        children({myDtoStore, command})
                    }
                }
                </MyDtoConsumer>
            }
        }
     </CommandConsumer>)
}

const MyItems = ({ items, command })  => (
    items.map(item =>
        <div>
            <MyItem item={item}/>
            <MyTaskItem item={item} onTaskComplete={command.onQueue}/>
        </div>
    )
);

const MyDto = () => (
    <div className="Account-Items">
        <MyAppConsumer>
            {({my, command}) =>
                <div>
                    **{my.isOnline ? "Online" : "Offline"}**
                    {/*todo: myDtoProxy.isRequesting*/}
                    {my.err ? my.err.toString() : ""}
                    <MyItems items={my.items} command={command}/>
                </div>
            }
            </MyAppConsumer>
    </div>
);

export default MyDto;