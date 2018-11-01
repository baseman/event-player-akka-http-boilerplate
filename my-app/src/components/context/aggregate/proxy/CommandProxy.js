import config from "../../../../config/poll.config";

function thenablePost(command) {
    const postUrl = config.host + "/" + command.aggregateId.val + "/cmd/";
    return fetch(postUrl, {
        method: "post",
        headers: {
            "Content-type": command.mediaType
        },
        body: command
    }).then((response) => {
        if (response.status !== 200) {
            throw Error("status [" + response.status + "] message: [" + response.statusText + "]");
        }

        return response.json()
    })
}

let isPosting = false;
let processNext = (fThenablePost) => {

    let items = this.state.items;

    if (items.length === 0 || isPosting) {
        return;
    }

    isPosting = true;
    _options.onProcessing();
    return thenablePost(items[0]).then((/*result*/) => {
        items.shift(); //remove completed command from queue
    }).catch((err) => {
        _options.onError(err)
    }).finally(() => {

        isPosting = false;

        //todo: *** replace with generator to drain refillable queue
        _options.onProcessed(items);
        if (items.length > 0) {
            processNext(fThenablePost);
        }
        //todo: ***

    });
};

let queue = (command, fThenablePost) => {
    _options.onQueue(command);
    processNext(fThenablePost)
};

let _options = null;

let CommandProxy = {
    init: (options) => {
        _options = options;
        return {
            queue: queue
        }
    }
};

export {CommandProxy};