let switcher, topView, bottomView, topContent, about, corner, message, cornerTop, cornerBottom,
    scheduleHolder;
const
    topBarColor = "#000000",
    topColor = "#00827E",
    bottomColor = "#00649C";

function presetup() {
    switcher = document.getElementById("switcher"),
        topView = document.getElementById("top"),
        bottomView = document.getElementById("bottom"),
        topContent = document.getElementById("topContent"),
        about = document.getElementById("about"),
        corner = document.getElementById("corner"),
        message = document.getElementById("message"),
        cornerTop = document.getElementById("cornerTop"),
        cornerBottom = document.getElementById("cornerBottom"),
        scheduleHolder = document.getElementById("schedule");
}

function setup() {
    presetup();
    setupTheme();
    setupBackground();
    setupCorner();
    setupTop();
    setupMessages();
    setupSwitcher();
    hide(topView);
}

function setupBackground() {
    document.body.style.backgroundImage = 'linear-gradient(to bottom,' + topColor + ', ' + bottomColor + ')';
    document.body.style.backgroundColor = topColor;
}

function setupSwitcher() {
    switcher.add;
    for (let c = 0; c < schedule.classrooms.length; c++) {
        const grade = schedule.classrooms[c].grade;
        const element = document.createElement("p");
        element.classList.add("switcherButton");
        element.onclick = function () {
            setClassroom(schedule.classrooms[c].name);
            hide(switcher);
            hide(topView);
        };
        element.innerHTML = schedule.classrooms[c].name;
        switcher.appendChild(element);
    }
}

function setupMessages() {
    nextMessage();
    setupTimer();
}

function setupTheme() {
    const meta = document.createElement("meta");
    meta.name = "theme-color";
    meta.content = topColor;
    document.head.appendChild(meta);
}

function setupTop() {
    topView.style.backgroundColor = topBarColor + "C8";
}

function setupCorner() {
    corner.style.backgroundColor = topColor + "80";
}

function setupTimer() {
    window.setInterval(nextMessage, 10 * 1000);
}

function setMessage(messageText) {
    message.innerText = messageText;
}

function setTop(view) {
    hideAll(topContent);
    show(view);
}

function setCorner(topText, bottomText) {
    cornerTop.innerText = topText;
    cornerBottom.innerText = bottomText;
}

function toggleTop() {
    if (isHidden(topView)) {
        hideAll(topContent);
        show(topView);
    } else {
        hide(topView);
    }
}

function toggleAbout() {
    if (isHidden(about)) {
        setTop(about);
    } else {
        hideAll(topContent);
    }
}

function toggleSwitcher() {
    if (isHidden(switcher)) {
        setTop(switcher);
    } else {
        hideAll(topContent);
    }
}

function addSubject(holder, subject) {
    if (subject.name.length > 0) {
        let lessonView = document.createElement("div");
        let top = document.createElement("p");
        let bottom = document.createElement("table");
        let time = document.createElement("p");
        let teachers = document.createElement("div");

        bottom.classList.add("lessonViewBottom");
        // bottomTable.classList.add("lessonViewBottomTable");
        teachers.classList.add("lessonViewTeachers");
        time.classList.add("lessonViewTime");
        top.classList.add("lessonViewText");
        lessonView.classList.add("lessonView");

        for (let t = 0; t < subject.teachers.length; t++) {
            let teacher = document.createElement("p");
            teacher.classList.add("lessonViewTeacher");
            teacher.innerHTML = subject.teachers[t];
            teachers.appendChild(teacher);
        }

        top.innerHTML = "\u200F" + subject.hour + ". " + subject.name;
        time.innerHTML = minuteToTime(subject.start_minute) + " - " + minuteToTime(subject.end_minute);

        hide(bottom);
        lessonView.onclick = function () {
            if (isHidden(bottom)) {
                showByDisplay(bottom, flex);
            } else {
                hide(bottom);
            }
        };

        bottom.appendChild(time);
        bottom.appendChild(teachers);
        lessonView.appendChild(top);
        lessonView.appendChild(bottom);
        holder.appendChild(lessonView);
    }
}

function refresh() {
    document.location.reload(true);
}