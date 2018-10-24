import React from 'react';

import {MyItemsListConsumer} from "./context/MyItemsListStore"
import MyItem from "./MyItem";
import MyTaskItem from "./MyTaskItem";
import {CommandConsumer} from "./context/aggregate/CommandStore";

function MyAppConsumer({children}) {
    return (
        <CommandConsumer>
            {(command) => {

                //todo: if !commandProxy.isOnline
                //todo: if !command.isPosting
                //todo: if command.err
                //command.onProcessNext

                return <MyItemsListConsumer> {
                    (my) => {

                        //todo: if !myDtoProxy.isOnline
                        //todo: if !my.isLoading
                        //todo: if my.err

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
                        <MyItem item={item}/>
                        <MyTaskItem item={item} onTaskComplete={command.onQueue}/>
                    </div>
                )
            }
            </MyAppConsumer>
    </div>
);

export default MyItems;