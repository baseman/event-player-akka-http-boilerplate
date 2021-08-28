import React from "react";

const MyItem = ({ item }) => (
    <div className="Account-Item">
        {item.toString()}
    </div>
);
const MyItems = ({ items })  => (
    items.map(item =>
        <div>
            <MyItem item={item}/>
        </div>
    )
);

export default MyItems;