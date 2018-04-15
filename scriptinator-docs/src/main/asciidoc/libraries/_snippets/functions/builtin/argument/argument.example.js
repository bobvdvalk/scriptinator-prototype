// tag::docs[]
var argument = argument();                      // <1>

if (argument == null) {                         // <2>
    warn("No argument given");
} else {
    info("Argument given", argument);           // <3>
}

// end::docs[]
