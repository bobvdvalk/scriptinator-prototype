package main

import "time"

type Schedule struct {
	Id         int64
	ProjectId  int64
	Argument   string
	ScriptName string
	CronString string
	LastRun    time.Time
}
