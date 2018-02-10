// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.patch({             // <2>
    url: "https://httpbin.org/patch",
    headers: [
        "Pi: 3.14"
    ]
});

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "The header is sent",
    "3.14",
    response.body().asJson().headers.Pi
);