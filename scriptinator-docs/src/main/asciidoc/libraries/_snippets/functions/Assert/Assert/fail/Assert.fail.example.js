// tag::docs[]
var Assert = library("Assert");                 // <1>
var HTTP = library("HTTP");

var data = HTTP.get(                            // <2>
    "https://now.httpbin.org/"
).body().asJson();

if (data.now.slang_date !== "today") {
    Assert.fail("It is today");                 // <3>
}

// end::docs[]
