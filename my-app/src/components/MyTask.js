import React from 'react';

const MyTask = ({item, onCommandQueue}) => {

    function onTaskComplete() {
        let command = null; //todo: pull from kotlin definition
        onCommandQueue(command)
    }

    return (<div className="Account-Task-Item">
        {item.toString()}
        <button onClick={onTaskComplete}>Execute</button>
    </div>)
};

export default MyTask;