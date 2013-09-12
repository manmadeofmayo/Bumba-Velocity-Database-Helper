/**
 * Created by emcee on 9/10/13.
 */
var veryBestTime = 30;
var bestTime = 15;
var clickSpeed = 1000;
getRelativeValue = function(object) {
    var returnValue =  object.storedCps / object.price;
    //console.log(returnValue);
    return returnValue;
};
buyInSeconds = function(object, time) {
    return (Game.cookies + (Game.cookiesPs * time)) > object.price
}
timeToBuy = function(id) {
    return (Game.ObjectsById[id].price - Game.cookies) / (Game.cookiesPs + (1000/clickSpeed));
}

cookieClicker = function(omitIds) {

    if (typeof omitIds == 'undefined') {
        omitIds = [];
    }
    if (omitIds.length >= Game.ObjectsById.length) {
        console.log("For those who seek perfection there can be no rest on this side of the grave.");
        omitIds = [];
        timeOut = 10000;
        return setTimeout(cookieClicker, timeOut);
    }
    var cookiesPerDollar = -1;
    var bestId = -1;
    var veryBestId = -1;
    var veryBestCookiesPerDollar = -1;
    for (var i = 0, obj; obj=Game.ObjectsById[i]; i++) {
        if (omitIds.indexOf(i) != -1) {
            continue;
        }
        // more is better
        //console.log(obj.storedCps, obj.price);
        var value = getRelativeValue(obj);
        if (veryBestCookiesPerDollar < value && buyInSeconds(obj, veryBestTime)) {
            veryBestCookiesPerDollar = value;
            console.log("set very best: " + veryBestCookiesPerDollar);
            veryBestId = i;
        }
    }
    for (var i = 0, obj; obj=Game.ObjectsById[i]; i++) {
        if (omitIds.indexOf(i) != -1) {
            continue;
        }
        var value = getRelativeValue(obj);
        if (cookiesPerDollar <= value && buyInSeconds(obj, bestTime)) {
            cookiesPerDollar = value;
            console.log("set best: " + value);
            bestId = i;
        }
    }
    console.log(bestId + " " + veryBestId);
    if (veryBestId != -1) {
         if (!buyInSeconds(Game.ObjectsById[veryBestId], veryBestTime)) {
            // if we have to wait too long, buy something cheaper
            console.log("There is no such thing as innocence, only degrees of guilt.");
            omitIds.push(veryBestId);
        } else if (buyInSeconds(Game.ObjectsById[veryBestId], veryBestTime)) {

            if (Game.cookies > Game.ObjectsById[veryBestId].price) {
                console.log("Truth is Subjective. Bought " + Game.ObjectsById[veryBestId].name);
                Game.ObjectsById[veryBestId].buy();
                timeOut = 10;
                return setTimeout(cookieClicker.bind(omitIds),timeOut);
            } else {
                console.log("sleep is divine: " + timeToBuy(veryBestId));
                return setTimeout(cookieClicker.bind(omitIds),timeToBuy(veryBestId) * 1000);
            }
        }
    }
    if (bestId != -1) {
        if (buyInSeconds(Game.ObjectsById[bestId], bestTime)) {
            if (Game.cookies > Game.ObjectsById[bestId].price) {
                console.log("Prayer cleanses the soul, Pain cleanses the body.  Bought " + Game.ObjectsById[bestId].name);
                Game.ObjectsById[bestId].buy();
                timeOut = 10;
                return setTimeout(cookieClicker.bind(omitIds), 10);
            } else {
                console.log("To Question is to doubt.");
                console.log((Game.ObjectsById[bestId].price - Game.cookies) / Game.cookiesPs);
                timeOut = (Game.ObjectsById[bestId].price - Game.cookies) / Game.cookiesPs * 1000;
                return setTimeout(cookieClicker.bind(omitIds),timeOut);
            }
        } else {
            // nothing to do but wait
            omitIds.push(veryBestId);
            omitIds.push(bestId);
            var timeToWait = 1000;
            console.log("The patient hunter gets the prey.");
            timeOut = timeToWait;
        }
    }
    var timeOut = 1000;
    return setTimeout(cookieClicker.bind(this, omitIds), timeOut);
};
setInterval(Game.ClickCookie, clickSpeed); // chosen to keep away from autoclicker detection
cookieClicker();
