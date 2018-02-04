// tag::docs[]
var HTTP = Script.library("HTTP");      // <1>

var httpClient = HTTP.client();         // <2>

var url = "https://httpbin.org/get";    // <3>
var response = httpClient.get(url);
var body = response.asJson();

Script.info(body);
// end::docs[]

var Assert = Script.library("Assert");

Assert.notNull("HTTP library exists", HTTP);
Assert.equal(
    "Response is parsed",
    "httpbin.org",
    body.headers.Host
);