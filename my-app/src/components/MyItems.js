import React from "react";
import MyTask from "./MyTask";

const MyItem = ({ item }) => (
    <div className="Account-Item">
        {item.toString()}
    </div>
);
const MyItems = ({ items, onCommandQueue })  => (
    items.map(item =>
        <div>
            <MyItem item={item}/>
            {/*<MyTask item={item} onCommandQueue={onCommandQueue}/>*/}
        </div>
    )
);

export default MyItems;