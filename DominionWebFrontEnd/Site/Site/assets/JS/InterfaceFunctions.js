var initializeVictories = function (count, stackidentifier) {
	console.log($("ul#victory > li")[0]);
    createCardObjectWithEventHandlerPossibilities("province", "provinceStack", $("ul#victory > li")[0]);
    createCardObjectWithEventHandlerPossibilities("duchy", "duchyStack", $("ul#victory > li")[1]);
    createCardObjectWithEventHandlerPossibilities("estate", "estateStack", $("ul#victory > li")[2]);
    environment_data["provinceStack"].eventhandlercomponent.src = environment_data["provinceStack"].source;
    environment_data["duchyStack"].eventhandlercomponent.src = environment_data["duchyStack"].source;
    environment_data["estateStack"].eventhandlercomponent.src = environment_data["estateStack"].source;
	console.log(environment_data["provinceStack"].eventhandlercomponent);
}
