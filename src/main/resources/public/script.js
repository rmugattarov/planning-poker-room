$(function() {
    function updateData() {
        $.get("update", function(data, status) {$("#voteStatus").html(data)})
    };
    updateData();
    window.setInterval(updateData, 2000);
});