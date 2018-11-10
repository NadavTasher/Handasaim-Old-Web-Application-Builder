const
    block = "block",
    flex = "flex";

function hideAll(view) {
    let children = view.childNodes;
    for (let i = 0; i < children.length; i++) {
        if (children[i] !== undefined && children[i].style !== undefined)
            hide(children[i]);
    }
}

function hide(view) {
    view.style.visibility = "hidden";
    view.style.display = "none";
    view.style.height = "0";
}

function showByDisplay(view, display) {
    view.style.visibility = "visible";
    view.style.display = display;
    view.style.height = "auto";
}

function show(view) {
    showByDisplay(view, block);
}

function minuteToTime(minute) {
    const minutes = minute % 60;
    let time = "";
    time += (minute - minutes) / 60;
    time += ":";
    time += (minutes < 10) ? "0" : "";
    time += minutes;
    return time;
}

function clear(view) {
    while (view.firstChild) {
        view.removeChild(view.firstChild);
    }
}

function isHidden(view) {
    return view.style.visibility === "hidden" || view.style.visibility === "";
}

function setCookie(cname, cvalue) {
    cvalue = encodeURIComponent(cvalue);
    const exdays = 365;
    const d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    const expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";domain=" + window.location.hostname + ";path=/";
}

function getCookie(cname) {
    const name = cname + "=";
    const ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return decodeURIComponent(c.substring(name.length, c.length));
        }
    }
    return "";
}

function findGetParameter(parameterName) {
    let result = null,
        tmp = [];
    location.search
        .substr(1)
        .split("&")
        .forEach(function (item) {
            tmp = item.split("=");
            if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
        });
    return result;
}