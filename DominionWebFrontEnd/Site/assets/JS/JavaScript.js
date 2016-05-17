//JavaScript
$(document).ready(function () {
    init();
});

/*Functions*/

var socket; // Socket connection

function init() {
    
    // Initialise socket connection
    if (window.WebSocket) {
        socket = new WebSocket("ws://172.31.32.64/", ["1", "YURI"]);
        socket.onopen = onSocketConnected();
        socket.onclose = onSocketDisconnect();
        socket.onmessage = onSocketMessage();
        socket.onerror = onSocketError();
    } else {
        alert("The browser does not support websocket.");
    }
    socket.send("{a:b}");
};

// Socket message
function onSocketMessage(message) {
    console.log('Message: ' + message.data);
};

// Socket error
function onSocketError(error) {
    console.log('Error: ' + error.data);
};

// Socket connected
function onSocketConnected() {
    console.log("Connected to socket server");
};

// Socket disconnected
function onSocketDisconnect() {
    console.log("Disconnected from socket server");
}; 

