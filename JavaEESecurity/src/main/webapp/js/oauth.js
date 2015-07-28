var onSignIn = function () {
    window.service = "google";
    var auth2 = gapi.auth2.getAuthInstance();
    var id_token = auth2.currentUser.get().getAuthResponse().id_token;

    var appctx = document.location.pathname.split("/")[1];
    var contextPath = document.location.host + "/" + appctx;

    var xhr = new XMLHttpRequest();
    xhr.open('POST', "http://" + contextPath + '/faces/userData.xhtml'); // servlet
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function () {
        var user = $(xhr.responseText).find("#userid").text();
        $("#user").html(user + " (Google)");
        console.log('Signed in Google as: ' + user);
    };
    xhr.send('idtoken=' + id_token + '&service=google');
};

var signOut = function () {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {

        var appctx = document.location.pathname.split("/")[1];
        var contextPath = document.location.host + "/" + appctx;

        var xhr = new XMLHttpRequest();
        xhr.open('POST', "http://" + contextPath + '/index.html'); // servlet
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.send('signout=true');
        $("#user").html("não logado");
        console.log('User signed out.');
    });

};

var onFailure = function(error) {
    console.log(error);
}

var renderButton = function () {
    gapi.signin2.render('signin-button', {
        'scope': 'https://www.googleapis.com/auth/plus.login',
        'width': 250,
        'height': 50,
        'longtitle': true,
        'theme': 'dark',
        'onsuccess': onSignIn,
        'onfailure': onFailure
    });
};
