// tag::docs[]
function main(argument) {                   // <1>
    if (argument == null) {                 // <2>
        warn("No argument given, using {}.");
        argument = {};
    }
}
// end::docs[]
