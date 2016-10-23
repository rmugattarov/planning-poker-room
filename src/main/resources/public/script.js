$(function() {
    function updateData() {
	if(!$.cookie("userName")) {
		location.reload();
	}
        $.get("update", function(data, status) {$("#voteStatus").html(data)});
    };
    updateData();
    window.setInterval(updateData, 2000);
});
