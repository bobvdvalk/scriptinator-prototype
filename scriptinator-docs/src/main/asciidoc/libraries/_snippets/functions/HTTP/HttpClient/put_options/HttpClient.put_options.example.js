// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.put({               // <2>
    url: "https://httpbin.org/put",
    bearerAuth: {
        "token": "aGVsbG8gd29ybGQ="
    }
});

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "The auth is sent",
    "Bearer aGVsbG8gd29ybGQ=",
    response.body().asJson().headers.Authorization
);