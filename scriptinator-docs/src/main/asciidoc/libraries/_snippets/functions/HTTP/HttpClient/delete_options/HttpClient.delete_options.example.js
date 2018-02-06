// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.delete({            // <2>
    url: "https://httpbin.org/delete",
    headers: [
        "Nounce: 4552"
    ]
});

// end::docs[]

var Assert = Script.library("Assert");

Assert.equal(
    "The header is sent",
    "4552",
    response.body().asJson().headers.Nounce
);