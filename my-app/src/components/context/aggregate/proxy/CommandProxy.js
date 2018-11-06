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

let processNext = () => {
    _options.onProcessing();
    return thenablePost(_options.nextProcessItem).then((/*result*/) => {
        _options.onProcessed();
    }).catch((err) => {
        _options.onError(err)
    }).finally(() => {
        _options.onProcessed();
    });
};

let _options = null;

let CommandProxy = {
    init: (options) => {
        _options = options;

        if (_options.items !== 0 || _options.isProcessing) {
            return;
        }

        processNext();
    }
};

export {CommandProxy};