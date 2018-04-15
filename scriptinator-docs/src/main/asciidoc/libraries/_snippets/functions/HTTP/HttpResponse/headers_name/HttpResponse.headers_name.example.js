// tag::docs[]
var HTTP = library("HTTP");                     // <1>

var response = HTTP.get(                        // <2>
    "https://httpbin.org/response-headers?Multi-Header=FooBar&Multi-Header=FizzBuzz"
);

info(response.headers("Multi-Header"));         // <3>

// end::docs[]

var Assert = library("Assert");

Assert.equal(
    "Multiple values are set for header",
    ["FooBar", "FizzBuzz"],
    response.headers("Multi-Header")
);