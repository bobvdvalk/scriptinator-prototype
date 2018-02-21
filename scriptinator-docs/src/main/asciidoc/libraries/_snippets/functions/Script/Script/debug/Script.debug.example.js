// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.get(                // <2>
    "https://httpbin.org/anything"
);

Script.debug(response.code());          // <3>

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "Status is OK",
    200,
    response.code()
);
