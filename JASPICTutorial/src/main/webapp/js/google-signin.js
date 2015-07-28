window.secure = true; //turn on/off secure HTTPS connection

var getResourceUrl = function (secure, extraPath) {
    var scheme = secure ? "https://" : "http://";
    var appctx = document.location.pathname.split("/")[1];
    var contextPath = document.location.host + "/" + appctx;
    return scheme + contextPath + extraPath;
};

// Sends the token to the server, which will validate it
// and populate the user data which is returned
var googleSignInSuccess = function () {

    window.service = "google";
    var auth2 = gapi.auth2.getAuthInstance();
    var id_token = auth2.currentUser.get().getAuthResponse().id_token;

    $.ajax({
        type: "POST",
        url: getResourceUrl(window.secure, "/UserDataServlet"),
        dataType: 'text',
        data: {
            idtoken: id_token,
            service: "google"
        },
        success: function (data) {
            
            var userData = $.parseJSON(data);
            
            var user = userData.userid;
            if (!user) {
                auth2.signOut();
                window.service = null;
                location.href = "index.xhtml";
                alert("Login failed!");
            } else {
                $("#user").html(user + " (Google)");
                $("#remote-user-role").html(userData.role.remote == "true" ? "YES" : "NO");
                $("#privileged-user-role").html(userData.role.privileged == "true" ? "YES" : "NO");
                $("#all-role").html(userData.role.all == "true" ? "YES" : "NO");
                
                $("#user-data").html("<img src='"+userData.avatar+"' width='100'/><br>"+userData.name);
                
                console.log('Signed in Google as: ' + user);
            }
        }
    });
};

var googleSignInFail = function (error) {
    console.log(error);
    alert("Google Login failed! Error: " + error);
};

// Sends a signout flag to the server
var googleSignOut = function () {
    if (window.service === "google" && confirm("Sign-out from Google?")) {
        var auth2 = gapi.auth2.getAuthInstance();
        auth2.signOut().then(function () {

            $.ajax({
                type: "POST",
                url: getResourceUrl(window.secure, "/faces/index.xhtml"),
                data: {
                    signout: "true"
                },
                success: function (data) {
                    $("#user").html("not logged in");
                    $("#remote-user-role").html("not logged in");
                    $("#privileged-user-role").html("not logged in");
                    $("#all-role").html("not logged in");
                    console.log('User signed out.');
                    location.href = "index.xhtml";
                }
            });

        });
    }
};

// this is a standard google function which draws the button
var renderGoogleButton = function () {
    gapi.signin2.render('signin-button', {
        'scope': 'https://www.googleapis.com/auth/plus.login',
        'width': 250,
        'height': 50,
        'longtitle': true,
        'theme': 'dark',
        'onsuccess': googleSignInSuccess,
        'onfailure': googleSignInFail
    });
};
