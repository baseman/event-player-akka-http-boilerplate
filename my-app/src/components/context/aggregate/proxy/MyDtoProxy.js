import config from "../../../../config/poll.config";

let isRequesting = false;

function thenableGetItems() {

    const getUrl = config.host + "/my/items";
    return fetch(getUrl, {
        method: "get"
    }).then((response) => {
        if (response.status !== 200) {
            throw Error("status [" + response.status + "] message: [" + response.statusText + "]");
        }

        return response.json()
    })
}

let syncItems = () => {
    if (isRequesting) {
        return
    }

    isRequesting = true;

    thenableGetItems().then((result) => {
        _options.onSync(result)
    }).catch((err) => {
        _options.onError(err);
    }).finally(() => {
        isRequesting = false;
    })
};

// function tearDownPoll() {
//     clearTimeout(schedule);
//     _options.onDisconnected();
// }

let schedule = null;
let _options = null;
function initPoll(options) {
    if (schedule != null) {
        return
    }

    _options = options;

    schedule = setInterval(() => {
        syncItems();
    }, config.pollMilliseconds);

    options.onConnected();

    syncItems();
}

const MyDtoProxy = {
    init: initPoll
    // syncItems: syncItems, //enable if you want to syncManually
    //disconnected: tearDownPoll // enable if you want to disconnected manually
};

export {MyDtoProxy};