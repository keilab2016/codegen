$(document).ready(function(){
    appInit();
    $("td.pagename").click(function(){
    	$(this).text("いまでしょう！");
    	$(this).css("background","red");
    });
});