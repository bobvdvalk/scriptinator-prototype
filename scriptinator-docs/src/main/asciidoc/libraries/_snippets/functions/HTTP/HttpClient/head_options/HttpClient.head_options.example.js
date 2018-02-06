// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var response = HTTP.head({              // <2>
    url: "https://httpbin.org/headers",
    headers: [
        "Custom-Header: some value"
    ]
});

var contentLength = response.header("Content-Length");
var server = response.header("Server");
Script.info(contentLength);             // <3>
Script.info(server);

// end::docs[]

var Assert = Script.library("Assert");

Assert.notNull("HTTP library exists", HTTP);
Assert.equal(
    "The server is described",
    "meinheld/0.6.1",
    server
);
