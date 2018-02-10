// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.get(                // <2>
    "https://httpbin.org/status/404"
);

Script.info(response.code());           // <3>

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "Status code is 404",
    404,
    response.code()
);