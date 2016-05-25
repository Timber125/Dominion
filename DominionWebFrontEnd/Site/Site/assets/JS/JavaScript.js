//JavaScript
$(document).ready(function () {
    
});

/*Variables*/

var nickname = "Webclient";
var ip = "localhost";
var port = "13338";
var cardId = 0;
var numberOfCardsInHand = 0;
var numberOfCardsOnTable = 0;
var socket; // Socket connection
var session; // The received client session
var environment_data = {};

/*Functions*/

var processdata = function(data){
    // Eerste message is enkel en alleen om de sessie te initializen.
    // Een easy manier om dit op te vangen, is kijken of session undefined is. Zoja, define session, en return (lees: fuck de rest en drop de package.)
    if(session == undefined){
        session = data.secret_session_token;
        console.log("session initialized: " + session);
		sendPackage(createChat("!rename " + nickname,"guest"))
        return; 
    }else{
		if(data.action == "sysout")
		{
			var message = data.sysout;
			$("textarea#chatmessage")[0].value += message + "\n"
			console.log(data.sysout);
		}else if(data.action == "dominion"){
		
			if(data.act == "control"){
				if(data.subject == "environment"){
					if(data.control == "init"){


						var proposed_identifier = data.identifier;
						var proposed_stackname = data.stack;
						var proposed_count = data.update;

                        if(data.stack == "Treasure") {

                        initializeTreasures(proposed_count,proposed_identifier);
                        updateEnvironmentCount("copper",60);
                        updateEnvironmentCount("silver",50);
                        updateEnvironmentCount("gold",40);

                        }

						else if(data.stack == "Victory"){
							
						initializeVictories(proposed_count,proposed_identifier);
                        updateEnvironmentCount("estate",8);
                        updateEnvironmentCount("duchy",8);
                        updateEnvironmentCount("province",10);
                         }

                        else if(data.stack=="deck_and_discard"){
                        initializeDeckAndDiscard(proposed_count,proposed_identifier);
                        updateEnvironmentCount("deck",10);
                        updateEnvironmentCount("discardpile",0);
                        }

                        else {
                            var cardname = data.stack;
                            console.log("received action card:"+cardname);
                            initializeActionCards(proposed_count,proposed_identifier,cardname);
                            updateEnvironmentCount(cardname,10);
                        }

						
					   }
                    else if(data.control == "updatecount"){
                        var stackname = data.stack;
                        var update = data.update;
                        updateEnvironmentCount(stackname, update);
                     }
                    else if(data.control == "updateview"){
                        var stackname = data.stack;
                        var update = data.update;
                        updateEnvironmentView(stackname, update);
                    }
                    else if(data.control == "clickable"){
                        makeEnvironmentClickable(data.items);
                    }
                    else if(data.control == "unclickable"){

                    }
				    }

                    else if(data.subject=="hand"){
                    if (data.control=="clickable"){
                        makeSomeHandCardsClickable(data.items);
                    }
                    else if (data.control=="unclickable"){
                        makeAllHandCardsUnclickable(data.items);

                    }
                    }
                    else if(data.subject == "phase"){
                    if(data.control=="clickable"){
                        $("#endphasebutton")[0].onclick=scopedEndPhaseEventHandler();
                    }else if(data.control == "unclickable"){
                        $("#endphasebutton")[0].onclick="";
                    }
                    }
                    else if(data.subject =="table"){
                    console.log("received subject table");
                    if(data.control=="add"){
                    var gain = data.cardname;
                    addCardToTable(gain);
                    }
                    else if(data.control=="clear"){
                        console.log("should be cleared");
                    removeAllCardsFromTable();
                    }

                    }   
                }
			
            else if(data.act=="gain"){
                var gain = data.gain;
                addCardToHand(gain,cardId);
                cardId++;
                }
            else if(data.act=="lose"){
                if(data.lose == "all"){
                    removeCardFromHand("all");
                } 
            else{
                var loseid= parseInt(data.loseID);
                removeCardFromHand(loseid);
                }

                }
            else if(data.act=="turninfo"){

                var money = data.money;
                var actions = data.actioncount;
                var purchases = data.purchasecount;
                $("ul#amounts > li")[0].innerHTML ="Actions: <strong id=\"amounts_actions\">" + actions + "</strong>";
                $("ul#amounts > li")[1].innerHTML ="Buys: <strong id=\"amounts_buys\">" + purchases + "</strong>";
                $("ul#amounts > li")[2].innerHTML ="Money: <strong id=\"amounts_coins\">" + money + "</strong>";
                }
		    
		}

		else if(data.action=="menu"){
		    if(data.function=="register"){
		        if(data.success=="true"){
                    
		        }
		else{
                    
		      }
		    }
		else if(data.function=="login"){
		      if(data.success=="true"){
		            $("#menu").addClass("invisible");
		            $("#game").removeClass("invisible");
                    sendPackage(createChat("!rename " + nickname, "WebClient"));
		        }
		else{

		      }
		    }
		  }
	
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
        socket = new WebSocket("ws://" + ip + ":" + port + "/");
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
 var sendPackage = function(json){
      json.session = session;
	  socket.send(JSON.stringify(json));
      }  
 var chats = 0;
function sendToChatbox() { //verzenden chatbericht, onthouden en onder elkaar
    
    var a = document.getElementById("chatinput").value
    sendPackage(createChat(a,nickname));
	
}
function createCardObject(cardname, identifier){
	var object = {};
	object.cardname = cardname;
	object.identifier = identifier;
	object.source = "resources\\DominionCards\\" + cardname + ".jpg"; 
	return object; 
}
function keepCardInEnvironment(cardObject){
	environment_data[cardObject.identifier] = cardObject; 
}
function createCardObjectPersistent(cardname, identifier){
	var object = createCardObject(cardname, identifier);
	keepCardInEnvironment(object);
}
function createCardObjectWithEventHandlerPossibilities(cardname, identifier, data){
	var object = createCardObject(cardname, identifier);
	object.eventhandlercomponent = data;
    object.cardId = -1;
	keepCardInEnvironment(object);
}
function createCardObjectWithEventHandlerPossibilitiesWithId(cardname, identifier, data,cardId){
    var object = createCardObject(cardname, identifier);
    object.eventhandlercomponent = data;
    object.cardId = cardId;
    keepCardInEnvironment(object);
}
var initializeVictories = function (count, stackidentifier) {
    console.log($("ul#victory > li")[0]); // dit geeft een <a> object?
    createCardObjectWithEventHandlerPossibilities("province", "provinceStack", $("ul#victory > li")[0]);
    createCardObjectWithEventHandlerPossibilities("duchy", "duchyStack", $("ul#victory > li")[1]);
    createCardObjectWithEventHandlerPossibilities("estate", "estateStack", $("ul#victory > li")[2]);
    console.log(environment_data["provinceStack"])
    environment_data["provinceStack"].eventhandlercomponent.innerHTML = "<a href=\"#/\">" + "<img src=" + environment_data["provinceStack"].source + "></a>";
    environment_data["duchyStack"].eventhandlercomponent.innerHTML = "<a href=\"#/\">" +"<img src=" + environment_data["duchyStack"].source + "></a>";
    environment_data["estateStack"].eventhandlercomponent.innerHTML = "<a href=\"#/\">" +"<img src=" + environment_data["estateStack"].source + "></a>";
    console.log(environment_data["provinceStack"].eventhandlercomponent);
}

var initializeTreasures = function(count,stackidentifier){
    console.log($("ul#treasure > li")[0]); // dit geeft een <a> object?
    createCardObjectWithEventHandlerPossibilities("gold", "goldStack", $("ul#treasure > li")[0]);
    createCardObjectWithEventHandlerPossibilities("silver", "silverStack", $("ul#treasure > li")[1]);
    createCardObjectWithEventHandlerPossibilities("copper", "copperStack", $("ul#treasure > li")[2]);
    console.log(environment_data["goldStack"])
    environment_data["goldStack"].eventhandlercomponent.innerHTML = "<a href=\"#/\">" + "<img src=" + environment_data["goldStack"].source + "></a>";
    environment_data["silverStack"].eventhandlercomponent.innerHTML = "<a href=\"#/\">" +"<img src=" + environment_data["silverStack"].source + "></a>";
    environment_data["copperStack"].eventhandlercomponent.innerHTML = "<a href=\"#/\">" +"<img src=" + environment_data["copperStack"].source + "></a>";
    console.log(environment_data["goldStack"].eventhandlercomponent);

}
var initializeDeckAndDiscard = function(count,stackidentifier){
    console.log($("ul#deck_and_discard > li")[0]); // dit geeft een <a> object?
    createCardObjectWithEventHandlerPossibilities("back", "deckStack", $("ul#deck_and_discard > li")[0]);
    createCardObjectWithEventHandlerPossibilities("back", "discardStack", $("ul#deck_and_discard > li")[1]);
    console.log(environment_data["deckStack"])
    environment_data["deckStack"].eventhandlercomponent.innerHTML = "<a href=\"#/\">" + "<img src=" + environment_data["deckStack"].source + "></a>";
    environment_data["discardStack"].eventhandlercomponent.innerHTML = "<a href=\"#/\">" +"<img src=" + environment_data["discardStack"].source + "></a>";
    console.log(environment_data["deckStack"].eventhandlercomponent);

}

var actionCardsInitialized = 0;
var initializeActionCards = function(count,stackidentifier,cardname){
    console.log($("ul#actions > li")[actionCardsInitialized]); // dit geeft een <a> object?
    createCardObjectWithEventHandlerPossibilities(cardname, "actionStack"+actionCardsInitialized, $("ul#actions > li")[actionCardsInitialized]);
    environment_data["actionStack"+actionCardsInitialized].eventhandlercomponent.innerHTML = "<a href=\"#/\">" + "<img src=" + environment_data["actionStack"+actionCardsInitialized].source + "></a>";
    console.log(environment_data["actionStack"+actionCardsInitialized]);
    actionCardsInitialized ++;
}
var addCardToHand = function(gain,cardId){
    console.log($("ul#handcards > li")[numberOfCardsInHand]); // dit geeft een <a> object?
    createCardObjectWithEventHandlerPossibilitiesWithId(gain, "handcard"+numberOfCardsInHand, $("ul#handcards > li")[numberOfCardsInHand],cardId);
    environment_data["handcard"+numberOfCardsInHand].eventhandlercomponent.innerHTML = "<a href=\"#/\">" + "<img src=" + environment_data["handcard"+numberOfCardsInHand].source + "></a>";
    console.log(environment_data["handcard"+numberOfCardsInHand]);
    numberOfCardsInHand ++;
}
var addCardToTable = function(gain){
    console.log("a card should have been added");
    console.log($("ul#playedcards > li")[numberOfCardsOnTable]); // dit geeft een <a> object?
    createCardObjectWithEventHandlerPossibilitiesWithId(gain, "playedcard"+numberOfCardsOnTable, $("ul#playedcards > li")[numberOfCardsOnTable],0);
    environment_data["playedcard"+numberOfCardsOnTable].eventhandlercomponent.innerHTML = "<a href=\"#/\">" + "<img src=" + environment_data["playedcard"+numberOfCardsOnTable].source + "></a>";
    console.log(environment_data["playedcard"+numberOfCardsOnTable]);
    numberOfCardsOnTable ++;
}
var cardBuyEventHandler = function(cardname){
    var object = {};
    object = cardBuy(cardname);
    sendPackage(object);
}
var cardOfferEventHandler = function (cardId, cardname) {
    var object ={};
    object = cardClicked(cardname,cardId);
    sendPackage(object);
}
function scopedCardBuyEventHandler(cardname){
    return function(){
        cardBuyEventHandler(cardname);
    }
}
function scopedEndPhaseEventHandler(cardname){
    return function(){
        sendPackage(endPhase());
    }
}

function scopedCardOfferEventHandler(cardid, cardname){
    return function(){
        cardOfferEventHandler( cardid,  cardname);
    };
}
var makeEnvironmentClickable = function(cardnamelist){
    var list = cardnamelist.split(",");
    var length = list.length;
    for(var i = 0; i < length; i++){
        // 6 known ids
        if(list[i] == "copper"){
            list[i] = "copperStack";
        }else if(list[i] == "silver"){
            list[i] = "silverStack";
        }else if(list[i] == "gold"){
            list[i] = "goldStack";
        }else if(list[i] == "estate"){
            list[i] = "estateStack";
        }else if(list[i] == "duchy"){
            list[i] = "duchyStack";
        }else if(list[i] == "province"){
            list[i] = "provinceStack";
        }
        else if (list[i]=="curse"){
            continue;
        }
        else{
            // Action card lookup
            for(var actindex = 0; actindex < actionCardsInitialized; actindex ++){
                if(environment_data["actionStack"+actindex].cardname == list[i]){
                    list[i] = "actionStack" + actindex;
                    break;
                }
            }
        }
    }
    // Now make everything unclickable
    makeWholeEnvironmentUnclickable();

    // Now make only given list clickable
    console.log("making clickable: " + list);
    for(var j = 0; j < length; j++){
        if (list[j]=="curse"){
            continue;
        }
        environment_data[list[j]].eventhandlercomponent.children[0].onclick = scopedCardBuyEventHandler(environment_data[list[j]].cardname);

    }


}

var makeWholeEnvironmentUnclickable = function(){
    environment_data["copperStack"].eventhandlercomponent.children[0].onclick = function(){};
    environment_data["silverStack"].eventhandlercomponent.children[0].onclick = function(){};
    environment_data["goldStack"].eventhandlercomponent.children[0].onclick = function(){};
    environment_data["estateStack"].eventhandlercomponent.children[0].onclick = function(){};
    environment_data["duchyStack"].eventhandlercomponent.children[0].onclick = function(){};
    environment_data["provinceStack"].eventhandlercomponent.children[0].onclick = function(){};
    for(var i = 0; i < actionCardsInitialized; i++){
        environment_data["actionStack" + i].eventhandlercomponent.children[0].onclick = function(){};
    }

}

var makeSomeHandCardsClickable = function(cardnamelist){
    var list = cardnamelist.split(",");
    var length = list.length;
    for(var i = 0; i < length; i++){
        var cardnamecheck = list[i];
        for(var j = 0; j < numberOfCardsInHand; j++){
            if (environment_data["handcard"+j].cardname == cardnamecheck){
                environment_data["handcard"+j].eventhandlercomponent.children[0].onclick = scopedCardOfferEventHandler(environment_data["handcard"+j].cardId, environment_data["handcard"+j].cardname);
                
            }
        }
    }
}

var makeAllHandCardsUnclickable = function(cardnamelist){
if (cardnamelist =="all") {
    for(var j = 0; j < numberOfCardsInHand; j++){
        environment_data["handcard"+j].eventhandlercomponent.children[0].onclick = function(){};
    }
}
}

var removeCardFromHand = function(loseid){
    if(loseid == "all"){
        for(var i = 0; i < numberOfCardsInHand; i++){
            environment_data["handcard"+i].eventhandlercomponent.innerHTML = "";
            numberOfCardsInHand = 0;
        }
    }else{
        var wholeHand = [];
        var spilindex = -1;
        for(var i = 0; i < numberOfCardsInHand; i++){
           var cardObj = environment_data["handcard"+i];
            wholeHand[i] = cardObj;
            if(parseInt(cardObj.cardId) == parseInt(loseid)){
                spilindex = i;
            }
        }
        if(spilindex == -1){
            console.log("card with id " + loseid + " not found in current hand.");
        }else{
            for(var j = spilindex; j < numberOfCardsInHand; j++){
                wholeHand[j] = wholeHand[j+1];
            }
             // Refresh all the cards
            numberOfCardsInHand --;
             for(var i = spilindex; i < numberOfCardsInHand; i++){
                  createCardObjectWithEventHandlerPossibilitiesWithId(wholeHand[i].cardname, "handcard"+i, $("ul#handcards > li")[i],wholeHand[i].cardId);
                  environment_data["handcard"+i].eventhandlercomponent.innerHTML = "<a href=\"#/\">" + "<img src=" + environment_data["handcard"+i].source + "></a>";
                  environment_data["handcard"+i].eventhandlercomponent.children[0].onclick = wholeHand[i].eventhandlercomponent.children[0].onclick;
             }   
                // Remove the duplicate leftover
                environment_data["handcard"+numberOfCardsInHand].eventhandlercomponent.innerHTML = "";
        }

    }

}
var removeAllCardsFromTable = function(){
for(var i = 0; i < numberOfCardsOnTable; i++){
   environment_data["playedcard"+i].eventhandlercomponent.innerHTML = "";
}
numberOfCardsOnTable = 0;
}

/*var removeCardFromTable = function(loseid){
if(loseid == "all"){
        for(var i = 0; i < numberOfCardsOnTable; i++){
            environment_data["playedcard+i"].eventhandlercomponent.innerHTML = "";
            numberOfCardsOnTable = 0;
        }
    }else{
        var wholeTable = [];
        var spilindex = -1;
        for(var i = 0; i < numberOfCardsOnTable; i++){
           var cardObj = environment_data["playedcard"+i];
            wholeTable[i] = cardObj;
            if(parseInt(cardObj.cardId) == parseInt(loseid)){
                spilindex = i;
            }
        }
        if(spilindex == -1){
            console.log("card with id " + loseid + " not found on the table.");
        }else{
            for(var j = spilindex; j < numberOfCardsOnTable; j++){
                wholeTable[j] = wholeTable[j+1];
            }
             // Refresh all the cards
            numberOfCardsOnTable--;
             for(var i = spilindex; i < number; i++){
                  createCardObjectWithEventHandlerPossibilitiesWithId(wholeTable[i].cardname, "playedcard"+i, $("ul#playedcards > li")[i],wholeTable[i].cardId);
                  environment_data["playedcard"+i].eventhandlercomponent.innerHTML = "<a href=\"#/\">" + "<img src=" + environment_data["playedcard"+i].source + "></a>";
                  environment_data["playedcard"+i].eventhandlercomponent.children[0].onclick = wholeTable[i].eventhandlercomponent.children[0].onclick;
             }   
                // Remove the duplicate leftover
                environment_data["playedcard"+numberOfCardsInHand].eventhandlercomponent.innerHTML = "";
        }

    }

} */


var connectButtonHandler = function () {
    sendPackage(createChat("!lobbyconnect", nickname));
   
}

var readyButtonHandler = function () {
    sendPackage(createChat("!lobbyvote", nickname));
}

var menuConnectButton = function () {
    
    ip = $("input#ipfield").val();
    port = $("input#portfield").val();
    
    
    init();
}

var loginButton = function () {
    if ($("input#usernamefield") && ($("input#usernamefield").val() != "") ){
        nickname = $("input#usernamefield").val();
        sendPackage(databaseProtocol("login", nickname, $("input#passwordfield").val()));
    }
    $("#menu").addClass("invisible");
    $("#game").removeClass("invisible");
}

var registerButton = function () {
    if ($("input#usernamefield") && ($("input#usernamefield").val() != "") ){
        nickname = $("input#usernamefield").val();
        sendPackage(databaseProtocol("register", nickname, $("input#passwordfield").val()));
    }
}

var updateEnvironmentCount = function(stackname, update){
    if(stackname == "discardpile"){
        $("ul#deck_and_discardstack > li")[1].innerHTML=update;
        }
    else if(stackname == "deck"){
        $("ul#deck_and_discardstack > li")[0].innerHTML=update;
        }
    else if(stackname =="estate"){
        $("ul#victorystack > li")[2].innerHTML=update;
        }  
    else if(stackname=="duchy"){
        $("ul#victorystack > li")[1].innerHTML=update;
        }
    else if(stackname=="province"){
        $("ul#victorystack > li")[0].innerHTML=update;
        }
    else if(stackname=="copper"){
        $("ul#treasurestack > li")[2].innerHTML=update;
        }
    else if(stackname=="silver"){
        $("ul#treasurestack > li")[1].innerHTML=update;
        }
    else if(stackname=="gold"){
        $("ul#treasurestack > li")[0].innerHTML=update;
        }
    else if(stackname=="trashpile"){
        $("li#trashstackcount")[0].innerHTML=update;
        }

    else{
        console.log("environment-" + stackname + " has " + update + " cards ");
        for(var i = 0; i < actionCardsInitialized; i++){
            if(environment_data["actionStack"+i].cardname==stackname){
                $("li#actionstackcount"+i)[0].innerHTML=update;
                break;
            }
        }
    }
}

var updateEnvironmentView = function(stackname, update){
    if(stackname == "discardpile"){
        createCardObjectWithEventHandlerPossibilities(update, "discardStack", $("ul#deck_and_discard > li")[1]);
        environment_data["discardStack"].eventhandlercomponent.innerHTML = "<a href=\"#/\">" +"<img src=" + environment_data["discardStack"].source + "></a>";
    }
    else if(stackname=="trashpile"){
        createCardObjectWithEventHandlerPossibilities(update, "trashStack", $("ul#trash > li")[0]);
        environment_data["trashStack"].eventhandlercomponent.innerHTML = "<a href=\"#/\">" +"<img src=" + environment_data["trashStack"].source + "></a>";
    
    }
    else{
        console.log("environment-" + stackname + " should update display to " + update);
    }
}