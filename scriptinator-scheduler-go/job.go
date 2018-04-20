package main

type Job struct {
	Id                    int64
	DisplayName           string
	ScriptId              int64
	Argument              string
	TriggeredByScheduleId int64
}
