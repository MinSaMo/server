var url = "ws://localhost:8080/ws";
var client = Stomp.client(url);

function connect_callback() {
    client.subscribe("/topic/log/chat", onMessage);
}

function onMessage(data) {
    let ul = document.getElementById("ui_log");
    let li = document.createElement("li");
    li.innerText = data;
    ul.appendChild(li);
}

function error_callback() {
    alert("Error on STOMP");
}

client.connect({}, connect_callback, error_callback);