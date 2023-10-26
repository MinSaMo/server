var url = "ws://localhost:8080/ws";
var client = Stomp.client(url);

function connect_callback() {
    client.subscribe("/topic/log/ai/caption", onAiMessage);
    client.subscribe("/topic/log/ai/emerge", onAiMessage);
    client.subscribe("/topic/log/ai/emerge_check", onAiMessage);
    client.subscribe("/topic/log/ai/emerge_occur", onAiMessage);
    client.subscribe("/topic/log/ai/complete_todo", onAiMessage);
    client.subscribe("/topic/log/ai/reply", onAiMessage);

    client.subscribe("/topic/log/client/script", onClientMessage);
    client.subscribe("/topic/log/client/intense", onClientMessage);
    client.subscribe("/topic/log/client/prompt", onClientMessage);
    client.subscribe("/topic/log/client/reply", onClientMessage);


    client.subscribe("/topic/service/reply", onServiceMessage);
    client.subscribe("/topic/service/emerge", onServiceMessage);
}

function onAiMessage(data) {
    let ul = document.getElementById("ai_log");
    let li = document.createElement("li");
    li.innerText = data;
    ul.appendChild(li);
}

function onClientMessage(data) {
    let ul = document.getElementById("client_log");
    let li = document.createElement("li");
    li.innerText = data;
    ul.appendChild(li);
}

function onServiceMessage(data) {
    let ul = document.getElementById("reply_log");
    let li = document.createElement("li");
    li.innerText = data;
    ul.appendChild(li);

}

function error_callback() {
    alert("Error on STOMP");
}

client.connect({}, connect_callback, error_callback);