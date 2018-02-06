// tag::docs[]
var HTTP = Script.library("HTTP");              // <1>

var response = HTTP.get(                        // <2>
    "https://httpbin.org/response-headers?Multi-Header=FooBar&Multi-Header=FizzBuzz"
);

Script.info(response.headers("Multi-Header"));  // <3>

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "Custom headers are set",
    ["FooBar", "FizzBuzz"],
    response.headers("Multi-Header")
);