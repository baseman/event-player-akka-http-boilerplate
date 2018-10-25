import React from 'react';

import {MyItemsListConsumer} from "./context/MyItemsListStore"
import MyItem from "./MyItem";
import MyTaskItem from "./MyTaskItem";
import {CommandConsumer} from "./context/aggregate/CommandStore";

function fThenableGetItems() {
    const postUrl = "**agregate**/{userVal}";
    return fetch(postUrl, {
        method: 'get'
    }).then((response) => {
        if (response.status !== 200) {
            throw Error("status [" + response.status + "] message: [" + response.statusText + "]");
        }

        return response.json()
    })
}

function MyAppConsumer({children}) {
    return (
        <CommandConsumer>
            {(command) => {

                return <MyItemsListConsumer> {
                    (my) => {

                        //todo: needs initial call
                        //todo: needs schedule
                        //todo: **** my.onSync(fThenableGetItems);

                        children({my, command})
                    }
                }
                </MyItemsListConsumer>
            }
            }
        </CommandConsumer>
    )
}

const MyItems = () => (
    <div className="Account-Items">
        <MyAppConsumer>
            {({my, command}) =>
                my.items.map(item =>
                    <div>

                        {/*todo: my dto status*/}
                        {/*todo: myDtoProxy.isOnline*/}
                        {/*todo: my.isLoading*/}
                        {/*todo: my.err*/}
                        <MyItem item={item}/>

                        {/*todo: command status*/}
                        {/*todo: commandProxy.isOnline*/}
                        {/*todo: command.isPosting*/}
                        {/*todo: command.err*/}
                        <MyTaskItem item={item} onTaskComplete={command.onQueue}/>
                    </div>
                )
            }
            </MyAppConsumer>
    </div>
);

export default MyItems;