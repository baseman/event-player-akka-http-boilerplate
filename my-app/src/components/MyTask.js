import React from 'react';

import My from 'my';

const MyTask = ({item, onCommandQueue}) => {

    function onTaskComplete() {
        //todo: pull from kotlin definition
        let command = null;

        //todo: bind input params to command
        onCommandQueue(command)
    }

    return (<div className="Account-Task-Item">
        {item.toString()}
        <button onClick={onTaskComplete}>Execute</button>
    </div>)
};

export default MyTask;