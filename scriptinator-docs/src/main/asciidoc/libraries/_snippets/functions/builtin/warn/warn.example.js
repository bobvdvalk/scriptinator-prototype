// tag::docs[]
var argument = argument();                  // <1>

if (argument == null) {                     // <2>
    warn("No argument given, using {}.");
    argument = {};
}

// end::docs[]

var Assert = library("Assert");

Assert.equal(
    "Argument is {}",
    {},
    argument
);
