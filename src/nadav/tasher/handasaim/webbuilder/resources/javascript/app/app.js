const
    classCookie = "class",
    installCookie = "popupInstall";

function onPageLoad() {
    setup();
    setClassroom((getCookie(classCookie) !== "" && getCookie(classCookie) !== "undefined") ? getCookie(classCookie) : schedule.classrooms[0].name);
    showInstallPopup();
}

function showInstallPopup() {
    if (getCookie(installCookie) !== "true" && findGetParameter("install") !== "false") {
        show(document.getElementById("popupInstall"));
    }
}

function doneInstall() {
    setCookie(installCookie, "true");
    hide(document.getElementById("popupInstall"));
}

function installApp() {
    doneInstall();
    document.location.href = "files/handasaim.mobileconfig";
}
