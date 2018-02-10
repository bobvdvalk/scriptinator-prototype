// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var httpClient = HTTP.client();         // <2>

var basicAuthClient = HTTP.client({     // <3>
    basicAuth: {
        username: "my-user",
        password: "super_secret123"
    }
});

var tokenAuthClient = HTTP.client({     // <4>
    bearerAuth: {
        token: "or9ut04gp59ug9380ur042ut349ir349tk4r43t"
    }
});

// end::docs[]

var Assert = Script.library("Assert");

Assert.notNull("HTTP library exists", HTTP);
Assert.notNull("Client is created", httpClient);
Assert.notNull("Client is created", basicAuthClient);
Assert.notNull("Client is created", tokenAuthClient);

Assert.equal(
    "Basic auth is applied",
    200,
    basicAuthClient.get("https://httpbin.org/basic-auth/my-user/super_secret123").code()
);

Assert.equal(
    "Token auth is applied",
    "Bearer or9ut04gp59ug9380ur042ut349ir349tk4r43t",
    tokenAuthClient.get("https://httpbin.org/headers").body().asJson().headers.Authorization
);