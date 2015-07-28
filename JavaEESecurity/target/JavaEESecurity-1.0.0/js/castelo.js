var carregarUsuarios = function (callback) {
    $.ajax({
        url: 'webapi/usuario',
        dataType: 'xml',
        success: function (data) {
            $("#usuarios tbody").html("");
            $(data).find("usuario").each(function () {
                var id = $(this).find("id").text();
                var userid = $(this).find("userid").text();
                var nome = $(this).find("nome").text();
                var password = $(this).find("password").text();
                adicionarLinha(id, userid, nome, password);
            });

            if (callback) {
                callback(data);
            }
        }
    });
};

var adicionarLinha = function (id, userid, nome, password) {
    var tr = $("<tr/>");
    tr.append($("<td/>").append(id));
    tr.append($("<td/>").append(userid));
    tr.append($("<td/>").append(nome));
    tr.append($("<td/>").append(password));
    $("#usuarios tbody").append(tr);
}

var openWebSocket = function () {
    var appctx = document.location.pathname.split("/")[1];
    var contextPath = document.location.host + "/" + appctx;
    var ws = new WebSocket("ws://" + contextPath + "/secretpoint");

    ws.onopen = function () {
        $("#wsresp").html("");
        console.log("WebSocket opened.");
        $("#wsresp").append("WebSocket opened<br/>");
        ws.send(contextPath + ";" + $("#user").val()); // send contextPath;userid
    };
    ws.onmessage = function (evt) {
        console.log("message received: " + evt.data);
        $("#wsresp").append("<b>" + evt.data + "</b><br/>");
        ws.close();
    };
    ws.onerror = function (evt) {
        console.log("WebSocket error.");
        $("#wsresp").append("WebSocket error: " + evt.data);
    };
    ws.onclose = function () {
        console.log("WebSocket closed.");
        $("#wsresp").append("WebSocket closed<br/>");
    };
    if (!ws) {

    }
};

var sendWSMessage = function () {
    openWebSocket();
};
