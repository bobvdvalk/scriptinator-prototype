// tag::docs[]
var HTTP = library("HTTP");             // <1>

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

var baseUrlClient = HTTP.client({       // <5>
    baseUrl: "https://httpbin.org"
});

var headersClient = HTTP.client({
    headers: [
        "Custom-Header: test"
    ]
});

// end::docs[]

var Assert = library("Assert");

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

Assert.equal(
    "Url is appended to baseUrl",
    "https://httpbin.org/anything/should/append",
    baseUrlClient.get("/anything/should/append").body().asJson().url
);

var testHeaders = headersClient.get({
    url: "https://httpbin.org/headers",
    headers: [
        "Another-Custom-Header: asdf"
    ]
}).body().asJson();

Assert.equal(
    "Client headers are added",
    "test",
    testHeaders.headers['Custom-Header']
);

Assert.equal(
    "Request headers are added",
    "asdf",
    testHeaders.headers['Another-Custom-Header']
);