// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.get({               // <2>
    url: "https://httpbin.org/basic-auth/user/p455w0rd",
    basicAuth: {
        username: "user",
        password: "p455w0rd"
    }
});

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "The request is authenticated",
    true,
    response.body().asJson().authenticated
);