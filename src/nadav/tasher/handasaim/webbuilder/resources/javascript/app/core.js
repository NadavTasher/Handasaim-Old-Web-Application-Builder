let messageIndex = 0;

function setClassroom(classroomName) {
    setCookie(classCookie, classroomName);
    // Corner
    setCorner(classroomName, schedule.day);
    // Parse Object
    clear(scheduleHolder);
    for (let classroomIndex = 0; classroomIndex < schedule.classrooms.length; classroomIndex++) {
        if (schedule.classrooms[classroomIndex].name === classroomName) {
            let classroom = schedule.classrooms[classroomIndex];
            for (let subjectIndex = 0; subjectIndex < classroom.subjects.length; subjectIndex++) {
                let subject = classroom.subjects[subjectIndex];
                addSubject(scheduleHolder, subject);
            }
        }
    }
}

function nextMessage() {
    if (schedule.messages.length > 0) {
        setMessage(schedule.messages[messageIndex]);
        if (messageIndex < schedule.messages.length - 1) {
            messageIndex++;
        } else {
            messageIndex = 0;
        }
    }
}