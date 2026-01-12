import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { publishState } from "./stateChannel";

let client = null;
let sessionId = null;

/**
 * 웹소켓에 연결하고 세션을 등록합니다.
 * @param {string} currentSessionId - 로그인 시 받은 세션 ID
 * @param {function} onState - 서버로부터 상태 업데이트를 받았을 때 호출될 콜백
 * @param {function} onSessionEnd - 세션이 종료되었을 때 호출될 콜백
 */
export function connectWS(currentSessionId, onState, onSessionEnd) {
    if (client && client.active) {
        console.log("WebSocket client is already active.");
        return;
    }

    sessionId = currentSessionId;

    client = new Client({
        webSocketFactory: () => new SockJS("/api/ws-connect"),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
            console.log("WebSocket Connected!");

            client.publish({
                destination: "/publish/session/register",
                body: JSON.stringify({ sessionId: sessionId }),
            });
            console.log(`Session registered with ID: ${sessionId}`);

            // 상태 업데이트를 받으면,
            client.subscribe("/subscribe/state", (msg) => {
                const state = JSON.parse(msg.body);
                // 1. 현재 탭의 화면을 직접 업데이트하기 위해 콜백을 호출합니다.
                onState(state);
                // 2. 다른 탭(디스플레이)에 전파하기 위해 stateChannel을 사용합니다.
                publishState(state);
            });

            client.subscribe(`/subscribe/session/${sessionId}`, (msg) => {
                const body = JSON.parse(msg.body);
                if (body.status === "TERMINATED") {
                    console.log("Session terminated by server.");
                    onSessionEnd();
                    disconnectWS();
                }
            });

            requestCurrentState();
        },
        onDisconnect: () => {
            console.log("WebSocket Disconnected!");
        },
        onStompError: (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        }
    });

    client.activate();
}

/**
 * 웹소켓 연결을 종료합니다.
 */
export function disconnectWS() {
    if (client && client.active) {
        client.deactivate();
        client = null;
        sessionId = null;
        console.log("WebSocket client deactivated.");
    }
}

/**
 * 리모트 컨트롤 명령을 서버로 전송합니다.
 * @param {string} type - 명령 타입 (예: 'NEXT_PLAYER')
 * @param {object} payload - 명령에 필요한 데이터
 */
export function sendCommand(type, payload = "") {
    if (!client || !client.connected) return;

    client.publish({
        destination: "/publish/command",
        body: JSON.stringify({ type, payload }),
    });
}

/**
 * 서버에 현재 상태를 요청합니다.
 */
export function requestCurrentState() {
    if (!client || !client.connected) return;
    client.publish({ destination: "/publish/state", body: "" });
}
