package main

type Schedule struct {
	Id         int    `sql:"id"`
	Argument   string `sql:"argument"`
	ScriptName string `sql:"script_name"`
}
