import React from 'react';

import My from 'my';

const MyTask = ({item, onCommandQueue}) => {

    function onTaskComplete() {
        //todo: bind input params to command
        let cmd = new My.my.artifact.myeventplayer.common.command.MyChangeCommand("blahChangeVal")
        onCommandQueue(cmd)
    }

    return (<div className="Account-Task-Item">
        <span>My Task</span>
        {item ? item.toString() : "No Item"}
        <button onClick={onTaskComplete}>Execute</button>
    </div>)
};

export default MyTask;