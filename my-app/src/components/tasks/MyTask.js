import React, {Component} from 'react';

import My from 'my';
const MyCreateCommand = My.my.artifact.myeventplayer.common.command.MyCreateCommand;

class MyTask extends Component{

    onTaskComplete = () => {
        //todo: can we simplify in my-js project with js friendly factory methods?
        //todo: can we simplify package implementation with kotlin-js-frontend?

        let cmd = new MyCreateCommand(this.myChangeInput.value);
        this.props.onCommandQueue(cmd)
    };

    //({item, onCommandQueue})


    render(){
        return (<div className="Account-Task-Item">
            <span>My Task</span>
            {this.props.item ? this.props.item.toString() : "No Item"}
            <input type="text" ref={input => this.myChangeInput = input} />
            <button onClick={this.onTaskComplete}>Execute</button>
        </div>)
    }
};

export default MyTask;