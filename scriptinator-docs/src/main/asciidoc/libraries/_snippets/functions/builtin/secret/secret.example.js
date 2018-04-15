// tag::docs[]
var HTTP = library('HTTP');                     // <1>

var token = secret('secret-token');             // <2>

var response = HTTP.post({                      // <3>
    url: "https://httpbin.org/post",
    bearerAuth: {
        token: token
    }
});

// end::docs[]
var Assert = library("Assert");

Assert.equal(
    "Secret is null",
    null,
    token
);

Assert.equal(
    "Response is 200",
    200,
    response.code()
);
