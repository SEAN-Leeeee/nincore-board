import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

let client = null;

export function connectWS(onState) {
    client = new Client({
        webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
        reconnectDelay: 2000,
        onConnect: () => {
            client.subscribe("/topic/state", (msg) => {
                onState(JSON.parse(msg.body));
            });
            client.publish({ destination: "/app/state.get", body: "" });
        },
    });

    client.activate();
}

export function sendCommand(type, payload = "") {
    if (!client || !client.connected) return;
    client.publish({
        destination: "/app/command",
        body: JSON.stringify({ type, payload }),
    });
}
