var url = "ws://localhost:8080/ws";
var client = Stomp.client(url);

function connect_callback() {
    client.subscribe("/sub/script", onScript);
    client.subscribe("/sub/intense", onIntense);
    client.subscribe("/sub/prompt", onPrompt);
    client.subscribe("/sub/reply", onReply);
}

function error_callback() {
    alert("Error on STOMP");
}

function onScript(message) {
    let es = document.getElementById("script_ul");
    let et = document.getElementById("timestamp_ul");
    let obj = JSON.parse(message.body);
    let tli = document.createElement("li");
    tli.innerHTML = obj.timestamp;
    let sli = document.createElement("li");
    sli.innerHTML = obj.script;
    et.appendChild(tli);
    es.appendChild(sli);
}

function onIntense(message) {
    let es = document.getElementById("intense_ul");
    let obj = JSON.parse(message.body);
    let tli = document.createElement("li");
    tli.innerHTML = obj.intense;
    es.appendChild(tli);
}

function onPrompt(message) {
    let es = document.getElementById("prompt_ul");
    let obj = JSON.parse(message.body);
    let tli = document.createElement("li");
    let prompt = obj.prompt;
    if (prompt.length >= 15) {
        prompt = prompt.slice(0, 15);
        prompt += "...";
    }
    tli.innerHTML = prompt;
    es.appendChild(tli);
}

function onReply(message) {
    let es = document.getElementById("reply_ul");
    let et = document.getElementById("response_time_ul");
    let obj = JSON.parse(message.body);
    let tli = document.createElement("li");
    let sli = document.createElement("li");
    let script = obj.script;
    if (script.length >= 15) {
        script = script.slice(0, 15);
        script += "...";
    }
    tli.innerHTML = script;
    sli.innerHTML = obj.responseTime;
    es.appendChild(tli);
    et.appendChild(sli);
}

client.connect({}, connect_callback, error_callback);

function onClick() {
    let input = document.getElementById("user_input");
    let str = input.value;
    client.send("/pub/script", {}, JSON.stringify({
        sender: "CLIENT",
        data: {
            script: str,
            dialogId: 1,
            isReal: false
        }
    }));
    input.value = "";
}