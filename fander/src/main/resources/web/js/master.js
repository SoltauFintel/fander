if($(window).width() < 1600) {
	$(".geweih").hide();
} else {
	$(".geweih").show();
}

window.onresize = function() {
	if($(window).width() < 1600) {
		$(".geweih").hide();
	} else {
		$(".geweih").show();
	}
}
