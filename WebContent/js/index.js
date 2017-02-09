window.onload = function() {
    appInit();
    checkRadioButton();
//    var img = document.getElementById("img");
//    img.addEventListener("mousedown" , click);
};

function appInit() {
	console.log('appInit : OK');
};

function checkRadioButton() {
	var validate = false;
	if(document.form1.geneWay2.checked || document.form1.geneWay3.checked) validate = true;
}

function popup(link) {
	window.open(link, "window", "width=400, height=600, menubar=no, toolbar=no, scrollbars=yes");
}

function check() {
	if(!document.form1.geneWay1.checked && !document.form1.geneWay2.checked && !document.form1.geneWay3.checked) {
		window.alert("生成方式を選択してください。");
		return false;
	}
	if(document.form1.geneWay1.checked) return true;
	if(document.form1.geneWay2.checked || document.form1.geneWay3.checked) {
		if(document.form1.secuSalt.value.length > 20 && document.form1.secuSalt.value.length < 50 &&
				document.form1.ciphSeed.value.length > 20 && document.form1.ciphSeed.value.length < 50) return true;
	}
	window.alert("ランダムな値を記入する部分の値が20〜50文字の間に収まっていません。");
	return false;
}