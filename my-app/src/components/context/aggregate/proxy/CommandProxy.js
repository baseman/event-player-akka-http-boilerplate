import config from "../../../../config/poll.config";

function thenablePost(item) {

    let command = item.command;
    let postUrl = config.host + "/my/cmd";

    if(item.aggregateId){
        postUrl += "/" + item.aggregateId.val
    }

    return fetch(postUrl, {
        method: "post",
        headers: {
             "Content-Type": command.constructor.Companion.mediaType
        },
        body: JSON.stringify(command)
    }).then((response) => {
        if (response.status !== 200) {
            throw Error("status [" + response.status + "] message: [" + response.statusText + "]");
        }
    })
}

let processNext = () => {
    _options.onProcessing();
    let processItem = _options.nextProcessItem;
    return thenablePost(_options.nextProcessItem).then((/*result*/) => {
        _options.onProcessed();
    }).catch((err) => {
        _options.onError(err, processItem)
    })
};

let _options = null;

let CommandProxy = {
    init: (options) => {
        _options = options;

        if (_options.items.length === 0 || _options.isProcessing) {
            return;
        }

        processNext();
    }
};

export {CommandProxy};