// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.get({               // <2>
    url: "https://httpbin.org/basic-auth/user/passwd",
    basicAuth: {
        username: "user",
        password: "passwd"
    }
});

// end::docs[]

var Assert = Script.library("Assert");

Assert.notNull("HTTP library exists", HTTP);
Assert.equal(
    "The request is authenticated",
    true,
    response.body().asJson().authenticated
);