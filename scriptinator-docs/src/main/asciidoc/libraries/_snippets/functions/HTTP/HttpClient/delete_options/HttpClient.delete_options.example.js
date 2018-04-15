// tag::docs[]
var HTTP = library("HTTP");             // <1>

var response = HTTP.delete({            // <2>
    url: "https://httpbin.org/delete",
    headers: [
        "Nounce: 4552"
    ]
});

// end::docs[]

var Assert = library("Assert");

Assert.equal(
    "The header is sent",
    "4552",
    response.body().asJson().headers.Nounce
);