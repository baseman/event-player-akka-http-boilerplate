import React from 'react';

const MyTaskItem = (item, onCommandQueue) => {

    function thenablePost(command) {
        var postUrl = "**agregate**/" + command.aggregateId.val + "/cmd/" ;
        return fetch(postUrl, {
            method: 'post',
            headers: {
                "Content-type": command.mediaType
            },
            body: command
        }).then((response) => {
            if (response.status !== 200) {
                throw Error("status [" + response.status + "] message: [" + response.statusText + "]");
            }

            return response.json()
        }).then((data) => {
            //todo: apply to UI
        })
    }

    function onCommandExecute() {
        var command = null; //todo: pull from kotlin definition
        onCommandQueue(command, thenablePost)
    }

    return (<div className="Account-Task-Item">
        {item.toString()}
        <button onClick={onCommandExecute}>Execute</button>
    </div>)
};

export default MyTaskItem;