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
Assert.equal(
    "Response is parsed",
    "httpbin.org",
    body.headers.Host
);
