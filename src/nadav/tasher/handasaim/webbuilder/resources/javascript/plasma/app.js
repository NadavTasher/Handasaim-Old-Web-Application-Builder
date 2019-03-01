const
    topColor = "#00827E",
    bottomColor = "#00649C";

let messageInterval, scheduleInterval, scrollInterval;
let scrollDirection = true;
let previousScroll = undefined, scrollPaused = false;
let messageIndex = 0;

let schedule;

function load() {
    setup();
}

function setupClassrooms() {

    function addSubject(column, subject) {
        if (subject.name.length > 0) {
            let subjectView = document.createElement("div");
            let name = document.createElement("p");
            // let teachers = document.createElement("p");
            //
            // for (let t = 0; t < subject.teachers.length; t++) {
            //     teachers.innerHTML += subject.teachers[t];
            // }

            name.innerText = subject.name;
            subjectView.appendChild(name);
            // subjectView.appendChild(teachers);
            column.appendChild(subjectView);
        }
    }

    function dayLength() {
        let maxLength = 0;
        for (let classroomIndex = 0; classroomIndex < schedule.classrooms.length; classroomIndex++) {
            let classroom = schedule.classrooms[classroomIndex];
            if (classroom.subjects.length > maxLength) maxLength = classroom.subjects.length;
        }
        return maxLength;
    }

    function setupTimes() {
        get("classname").appendChild(document.createElement("div"));
        let column = document.createElement("div");
        for (let h = 0; h < dayLength(); h++) {
            let timeHolder = document.createElement("div");
            let time = document.createElement("p");
            time.innerText = h;
            timeHolder.appendChild(time);
            column.appendChild(timeHolder);
        }
        get("subjects").appendChild(column);
    }

    clear("subjects");
    clear("classname");
    setupTimes();
    for (let classroomIndex = 0; classroomIndex < schedule.classrooms.length; classroomIndex++) {
        let classroom = schedule.classrooms[classroomIndex];
        let column = document.createElement("div");
        let nameHolder = document.createElement("div");
        let nameTitle = document.createElement("p");
        nameHolder.appendChild(nameTitle);
        nameTitle.innerText = classroom.name;
        get("classname").appendChild(nameHolder);
        for (let subjectIndex = 0; subjectIndex < classroom.subjects.length; subjectIndex++) {
            let subject = classroom.subjects[subjectIndex];
            addSubject(column, subject);
        }
        get("subjects").appendChild(column);
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
        function updateDateTime() {
            let now = new Date();
            get("time").innerText = now.getHours() + ":" + ((now.getMinutes() < 10) ? "0" + now.getMinutes() : now.getMinutes());
            get("date").innerText = now.getDate() + "." + (now.getMonth() + 1) + "." + now.getFullYear();
        }

        window.setInterval(updateDateTime, 500);
        updateDateTime();
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

    function setupAutoscroll() {
        function scroll() {
            function holdScroll() {
                function releaseScroll() {
                    scrollPaused = false;
                    scrollDirection = !scrollDirection;
                }

                scrollPaused = true;
                setTimeout(releaseScroll, 2000);
            }

            if (!scrollPaused) {
                previousScroll = get("subjects").scrollTop;
                if (scrollDirection)
                    get("subjects").scrollBy(0, 1);
                else
                    get("subjects").scrollBy(0, -10);
                if (get("subjects").scrollTop === previousScroll) {
                    holdScroll();
                }
            }
        }

        window.clearInterval(scrollInterval);
        scrollInterval = window.setInterval(scroll, 20);
    }

    loadSchedule((s) => {
        schedule = s;
        setupMessages();
        setupClassrooms();
        setupAutoscroll();
    });
}