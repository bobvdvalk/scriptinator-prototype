// tag::docs[]
var Assert = library("Assert");                 // <1>
var HTTP = library("HTTP");

var data = HTTP.get(                            // <2>
    "https://httpbin.org/ip"
).body().asJson();

Assert.notNullOrEmpty("ip", data.origin);       // <3>

// end::docs[]
