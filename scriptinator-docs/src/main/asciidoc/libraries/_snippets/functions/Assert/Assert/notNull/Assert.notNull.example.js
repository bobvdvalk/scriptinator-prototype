// tag::docs[]
var Assert = library("Assert");                 // <1>
var HTTP = library("HTTP");

var data = HTTP.get(                            // <2>
    "https://httpbin.org/anything?foo=bar"
).body().asJson();

Assert.notNull(                                 // <3>
    "args.foo",
    data.args.foo
);

// end::docs[]
