const
    classCookie = "class",
    installCookie = "popupInstall";

const
    topBarColor = "#000000",
    topColor = "#00827E",
    bottomColor = "#00649C";

let messageInterval, scheduleInterval;
let messageIndex = 0;

let schedule;

function load() {
    setup();
}

function setClassroom(classroomName) {

    function addSubject(subject) {
        if (subject.name.length > 0) {
            let subjectView = document.createElement("div");
            let top = document.createElement("p");
            let bottom = document.createElement("div");
            let time = document.createElement("p");
            let teachers = document.createElement("div");

            for (let t = 0; t < subject.teachers.length; t++) {
                let teacher = document.createElement("p");
                teacher.innerHTML = subject.teachers[t];
                teachers.appendChild(teacher);
            }

            top.innerHTML = "\u200F" + subject.hour + ". " + subject.name;
            time.innerHTML = minuteToTime(subject.start_minute) + " - " + minuteToTime(subject.end_minute);
            hide(bottom);
            subjectView.onclick = function () {
                if (!visible(bottom)) {
                    show(bottom);
                } else {
                    hide(bottom);
                }
            };
            bottom.appendChild(teachers);
            bottom.appendChild(time);
            subjectView.appendChild(top);
            subjectView.appendChild(bottom);
            get("schedule").appendChild(subjectView);
        }
    }

    function setCorner(topText, bottomText) {
        get("cornerTop").innerText = topText;
        get("cornerBottom").innerText = bottomText;
    }

    setCookie(classCookie, classroomName);
    // Corner
    setCorner(classroomName, schedule.day);
    // Parse Object
    clear("schedule");
    for (let classroomIndex = 0; classroomIndex < schedule.classrooms.length; classroomIndex++) {
        if (schedule.classrooms[classroomIndex].name === classroomName) {
            let classroom = schedule.classrooms[classroomIndex];
            for (let subjectIndex = 0; subjectIndex < classroom.subjects.length; subjectIndex++) {
                let subject = classroom.subjects[subjectIndex];
                addSubject(subject);
            }
        }
    }
}

function setup() {
    function setupBackground() {
        document.body.style.backgroundImage = 'linear-gradient(to bottom,' + topColor + ', ' + bottomColor + ')';
        document.body.style.backgroundColor = topColor;
    }

    function setupTheme() {
        theme(topColor);
    }

    function setupDashboard() {

    }

    setupTheme();
    setupBackground();
    setupDashboard();
    scheduleInterval = window.setInterval(updateSchedule, 1000 * 60 * 5);
    updateSchedule();
}

function updateSchedule() {
    function setupMessages() {
        function nextMessage() {
            function setMessage(messageText) {
                get("message").innerText = messageText;
            }

            if (schedule.messages.length > 0) {
                setMessage(schedule.messages[messageIndex]);
                if (messageIndex < schedule.messages.length - 1) {
                    messageIndex++;
                } else {
                    messageIndex = 0;
                }
            }
        }

        if (schedule.messages.length > 0) {
            nextMessage();
            window.clearInterval(messageInterval);
            messageInterval = window.setInterval(nextMessage, 10 * 1000);
            show("message");
        } else {
            hide("message");
        }
    }

    loadSchedule((s) => {
        schedule = s;
        setupMessages();
    });
}