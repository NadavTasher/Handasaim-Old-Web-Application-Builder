const topBarColor = "#808080",
  iconColor = "#dd8833",
  topColor = "#00827E",
  bottomColor = "#00649C";
const classCookie = "class",
  installCookie = "popupInstall";
const block = "block",
  inlineBlock = "inline-block",
  flex = "flex";

function onPageLoad() {
  hideTopBar();
  setupColors();
  parseSchedule();
  setupSwitcher();
  setClassroom((getCookie(classCookie) != "" && getCookie(classCookie) != "undefined") ? getCookie(classCookie) : schedule.classrooms[0].name);
  showInstallPopup();
}

function setupColors() {
  setupTopBarColor();
  setupTopColor();
  setupCornerColor();
  setupBackground();
}

function setupTopBarColor() {
  document.getElementById("topBar").style.backgroundColor = topBarColor;
}

function setupTopColor() {

  var meta = document.createElement('meta');
  meta.name = "theme-color";
  meta.content = topColor;
  document.getElementsByTagName('head')[0].appendChild(meta);
}

function setupCornerColor() {
  document.getElementById("corner").style.backgroundColor = topColor + "80";
}

function setupSwitcher() {
  var switcher = document.getElementById("classroomSwitcher");
  for (var c = 0; c < schedule.classrooms.length; c++) {
    const name = schedule.classrooms[c].name;
    var grade = schedule.classrooms[c].grade;
    var element = document.createElement("p");
    element.classList.add("switcherButton");
    element.onclick = function() {
      setClassroom(name);
      hideView(document.getElementById("classroomSwitcher"));
    };
    element.innerHTML = name;
    switcher.appendChild(element);
  }
}

function hideTopBar() {
  hideView(document.getElementById("topBar"));
}

function showTopBar() {
  showView(document.getElementById("topBar"));
}

function hideView(view) {
  view.style.visibility = "hidden";
  view.style.display = "none";
  view.style.height = "0";
}

function showViewByDisplay(view, display) {
  view.style.visibility = "visible";
  view.style.display = display;
  view.style.height = "auto";
}

function showView(view) {
  showViewByDisplay(view, block);
}

function setupBackground() {
  // document.body.style.backgroundColor = backColor;
  document.body.style.backgroundImage = 'linear-gradient(to bottom,' + topColor + ', ' + bottomColor + ')';
  document.body.style.backgroundColor = topColor;
}

function parseSchedule() {
  var content = document.getElementById("content");
  for (var c = 0; c < schedule.classrooms.length; c++) {
    var classroomView = document.createElement("div");
    classroomView.id = schedule.classrooms[c].name;
    classroomView.classList.add("classroomSchedule");
    for (var s = 0; s < schedule.classrooms[c].subjects.length; s++) {
      var lessonName = schedule.classrooms[c].subjects[s].name;
      var lessonHour = schedule.classrooms[c].subjects[s].hour;
      if (lessonName.length > 0) {
        const lessonView = document.createElement("div");
        const top = document.createElement("p");
        const bottom = document.createElement("div");
        const bottomTable = document.createElement("table");
        const time = document.createElement("p");
        const teachers = document.createElement("div");

        bottom.classList.add("lessonViewBottom");
        bottomTable.classList.add("lessonViewBottomTable");
        teachers.classList.add("lessonViewTeachers");
        time.classList.add("lessonViewTime");
        top.classList.add("lessonViewText");
        lessonView.classList.add("lessonView");

        for (var t = 0; t < schedule.classrooms[c].subjects[s].teachers.length; t++) {
          var teacher = schedule.classrooms[c].subjects[s].teachers[t];
          var teacherName = document.createElement("p");
          teacherName.classList.add("lessonViewTeacher");
          teacherName.innerHTML = teacher;
          teachers.appendChild(teacherName);
        }

        // bottomTable.cellSpacing = "10";

        top.innerHTML = "\u200F" + lessonHour + ". " + lessonName;
        time.innerHTML = minuteToTime(schedule.classrooms[c].subjects[s].start_minute) + " - " + minuteToTime(schedule.classrooms[c].subjects[s].end_minute);

        hideView(bottom);
        lessonView.onclick = function() {
          if (isHidden(bottom)) {
            showViewByDisplay(bottom, flex);
          } else {
            hideView(bottom);
          }
        };

        bottomTable.appendChild(time);
        bottomTable.appendChild(teachers);
        lessonView.appendChild(top);
        lessonView.appendChild(bottom);
        bottom.appendChild(bottomTable);
        classroomView.appendChild(lessonView);
      }
    }
    content.appendChild(classroomView);
    hideView(classroomView);
  }
}

function minuteToTime(minute) {
  var minutes = minute % 60;
  var time = "";
  time += (minute - minutes) / 60;
  time += ":";
  time += (minutes < 10) ? "0" : "";
  time += minutes;
  return time;
}

function setClassroom(classroomName) {
  setCookie(classCookie, classroomName);
  document.getElementById("cornerTop").innerHTML = classroomName;
  document.getElementById("cornerBottom").innerHTML = schedule.day;
  for (var i = 0; i < document.getElementsByClassName("classroomSchedule").length; i++) {
    hideView(document.getElementsByClassName("classroomSchedule")[i]);
  }
  showView(document.getElementById(classroomName));
}

function showInstallPopup() {
  if (getCookie(installCookie) != "true" && findGetParameter("install") != "false") {
    showView(document.getElementById("popupInstall"));
  }
}

function isHidden(view) {
  return view.style.visibility == "hidden";
}

function toggleMenu() {
  if (isHidden(document.getElementById("topBar"))) {
    hideView(document.getElementById("aboutView"));
    hideView(document.getElementById("classroomSwitcher"));
    showTopBar();
  } else {
    hideTopBar();
  }
}

function toggleAbout() {
  if (isHidden(document.getElementById("aboutView"))) {
    showView(document.getElementById("aboutView"));
  } else {
    hideView(document.getElementById("aboutView"));
  }
}

function toggleSwitcher() {
  if (isHidden(document.getElementById("classroomSwitcher"))) {
    showView(document.getElementById("classroomSwitcher"));
  } else {
    hideView(document.getElementById("classroomSwitcher"));
  }
}

function setCookie(cname, cvalue) {
  cvalue = encodeURIComponent(cvalue);
  var exdays = 365;
  var d = new Date();
  d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
  var expires = "expires=" + d.toUTCString();
  document.cookie = cname + "=" + cvalue + ";" + expires + ";domain=" + window.location.hostname + ";path=/";
}

function getCookie(cname) {
  var name = cname + "=";
  var ca = document.cookie.split(';');
  for (var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return decodeURIComponent(c.substring(name.length, c.length));
    }
  }
  return "";
}

function findGetParameter(parameterName) {
  var result = null,
    tmp = [];
  location.search
    .substr(1)
    .split("&")
    .forEach(function(item) {
      tmp = item.split("=");
      if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
    });
  return result;
}

function doneInstall() {
  setCookie(installCookie, "true");
  hideView(document.getElementById("popupInstall"));
}

function installApp() {
  doneInstall();
  document.location.href = "files/handasaim.mobileconfig";
}

function refresh() {
  document.location.reload(true);
}
