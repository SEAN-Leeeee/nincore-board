import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

let client = null;

export function connectWS(onState) {
    client = new Client({
        webSocketFactory: () => new SockJS("/api/ws-connect"),
        reconnectDelay: 2000,
        onConnect: () => {
            client.subscribe("/subscribe/state", (msg) => {
                onState(JSON.parse(msg.body));
            });

            client.publish({ destination: "/publish/state", body: "" });
        },
    });

    client.activate();
}

export function sendCommand(type, payload = "") {
    if (!client || !client.connected) return;

    client.publish({
        destination: "/publish/command",
        body: JSON.stringify({ type, payload }),
    });
}
