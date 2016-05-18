//JavaScript
$(document).ready(function () {
    init();
});

/*Functions*/

var socket; // Socket connection
var session; // The received client session

var processdata = function(data){
    // Eerste message is enkel en alleen om de sessie te initializen.
    // Een easy manier om dit op te vangen, is kijken of session undefined is. Zoja, define session, en return (lees: fuck de rest en drop de package.)
    if(session == undefined){
        session = data.secret_session_token;
        console.log("session initialized: " + session);
        return; // fuck de rest en drop de package. 
    }else{
        // HANDLE DATA
        // Alle volgende pakketjes komen hier terecht. VANG ZE OP!
        // Check de protocollen in JSONUtilities om te zien wat voor pakketjes binnen komen met welke reden. 
        // Begin misschien met de chat te initialiseren. 
        // Haal alle pakketjes met act=sysout eruit, en console.log() de message. Als er al een textarea is voor de chat, voeg het dan daarin ;) 
        // De rest ga je wat moeten spieken van de desktop client, kijk misschien als voorbeeld naar de flow van ClientModelService in de client. 
    }

}

function init() {
    
    // Initialise socket connection
    if (window.WebSocket) {
        socket = new WebSocket("ws://127.0.0.1:13338/");
        socket.onopen = function onSocketConnected() {
            console.log("Connected to socket server");
        };
        socket.onclose = function onSocketDisconnect() {
            console.log("Disconnected from socket server");
        }; 

        socket.onmessage = function onSocketMessage(evt) {
            //console.log(evt);
            if(evt == undefined) console.log("undefined message received");
            else console.log('Message: ' + evt.data);
            processdata(JSON.parse(evt.data));
        };
        socket.onerror = function onSocketError(error) {
            console.log('Error: ' + error);
        };

        
    } else {
        alert("The browser does not support websocket.");
    }
    
    /* Dit is onze persoonlijke handshake
        We wachten tot de connectie "ready" is, en dan sturen we "{client:websocket}" om aan te geven dat we een WebConnectionHandler willen in de server. 
        Daarna krijgen we op gepaste wijze een session terug. 
    */
    var check = function(){
        if(socket.readyState == 1){
            // run when condition is met
            socket.send("{client:websocket}");
            console.log(socket.readyState);
            console.log("sent custom handshake initiation");
        }
        else {
            console.log("waiting");
            setTimeout(check, 1000); // check again in a second
        }
    }
    // Hier voert hij zichzelf uit, en hij blijft zichzelf elke seconde 1x uitvoeren tot de readyState 1 is, waarbij hij de handshake verzendt. 
    check();

    
};



