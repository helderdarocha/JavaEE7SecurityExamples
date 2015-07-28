window.secure = true; //turn on/off secure HTTPS connection

var getResourceUrl = function (secure, extraPath) {
    var scheme = secure ? "https://" : "http://";
    var appctx = document.location.pathname.split("/")[1];
    var contextPath = document.location.host + "/" + appctx;
    return scheme + contextPath + extraPath;
}

// Standard Facebook Connect function
var facebookInit = function (facebookAppID) {
    window.fbAsyncInit = function () {
        FB.init({
            appId: facebookAppID,
            cookie: true,
            oauth: true,
            xfbml: true,
            version: 'v2.4'
        });
        checkLoginState();
    };

    (function (d, s, id) {
        var js, fjs = d.getElementsByTagName(s)[0];
        if (d.getElementById(id)) {
            return;
        }
        js = d.createElement(s);
        js.id = id;
        js.src = "//connect.facebook.net/en_US/sdk.js";
        fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));
}

// Standard Facebook Connect function
function checkLoginState() {
    FB.getLoginStatus(function (response) {
        statusChangeCallback(response);
    });
}
// Standard Facebook function which calls our sign-in function
// It does all the authentication. The server needs the token.
function statusChangeCallback(response) {
    console.log('statusChangeCallback');
    console.log(response);

    if (response.status === 'connected') {
        window.service = "facebook";
        testAPI();
        facebookSignIn(response); // this is our sign-in function
    } else if (response.status === 'not_authorized') {
        window.service = "null";
        document.getElementById('status').innerHTML = 'Please log into this app.';
    } else {
        window.service = "null";
        document.getElementById('status').innerHTML = 'Please log into Facebook.';
    }
}

// test function
function testAPI() {
    console.log('Welcome!  Fetching your information.... ');
    FB.api('/me', function (response) {
        console.log('Successful login for: ' + response.name);
        document.getElementById('status').innerHTML =
                'Thanks for logging in, ' + response.name + '!';
    });
}

// This will send the authenticated token to the server,
// so the user data (email) can be mapped to a group and role
var facebookSignIn = function (response) {
    $.ajax({
        type: "POST",
        url: getResourceUrl(window.secure, "/UserDataServlet"),
        dataType: 'text',
        data: {
            accessToken: response.authResponse.accessToken,
            service: "facebook"
        },
        success: function (data) {
            var userData = $.parseJSON(data);

            var user = userData.userid;
            if (!user) {
                // signout
                // location.href = "index.xhtml";
                alert("Login failed!");
            } else {
                $("#user").html(user + " (Facebook)");
                $("#remote-user-role").html(userData.role.remote == "true" ? "YES" : "NO");
                $("#privileged-user-role").html(userData.role.privileged == "true" ? "YES" : "NO");
                $("#all-role").html(userData.role.all == "true" ? "YES" : "NO");

                $("#user-data").html("<img src='" + userData.avatar + "' width='100'/><br>" + userData.name);

                console.log('Signed in Facebook as: ' + user);
            }

        }
    });
}

// This will send a signout flag to the server
var facebookSignOut = function () {
    if (window.service === "facebook" && confirm("Sign-out from your site and Facebook?")) {
        FB.logout(function (response) {
            alert("You are logged out from your site and facebook!")
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
                    window.service = null;
                    location.href = "index.xhtml";
                }
            });
        });
    }
}