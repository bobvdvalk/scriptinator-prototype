// tag::docs[]
var HTTP = library("HTTP");             // <1>

var response = HTTP.head({              // <2>
    url: "https://httpbin.org/headers",
    headers: [
        "Custom-Header: some value"
    ]
});

var contentLength = response.header("Content-Length");
var server = response.header("Server");
info(contentLength);                    // <3>
info(server);

// end::docs[]

var Assert = library("Assert");

Assert.notNullOrEmpty(
    "server description",
    server
);
