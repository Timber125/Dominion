var createChat = function(message, author){
    var obj = {};

    obj.service_type = "chat";
    obj.author = author;
    obj.message = message;

    return obj;
}

var createDominion = function (phase, operation, repeats) {
    var obj = {};

    obj.service_type = "dominion";
    obj.phase = phase;
    obj.operation = operation;
    obj.repeats = repeats.toString();

    return obj;
}

var cardClicked = function(cardname, id){
    var obj = {};

    obj.service_type = "dominion";
    obj.operation = "cardoffer";
    obj.cardname = cardname;
    obj.id = id.toString();

    return obj;
}

var endPhase = function(){
    var obj = {};

    obj.service_type = "dominion";
    obj.operation = "endphase";

    return obj;
}

var cardBuy = function(cardname){
    var obj = {};

    obj.service_type = "dominion";
    obj.operation = "buy";
    obj.cardname = cardname;

    return obj;
}

var databaseProtocol = function(func, username, password){
    var obj = {};

    obj.service_type= "database";
    obj.function = func;
    obj.username = username;
    obj.password = password;

    return obj;
}