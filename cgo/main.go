package main

// #include "hello.h"
import "C"

import (
	"fmt"
	"log"
)

func main() {
	if err := greet(); err != nil {
		log.Fatalf("Failed to call hello: %s", err)
	}
	log.Printf("Successfully called hello!\n");
}

func greet() error {
	_, err := C.Greet()
	if err != nil {
		return fmt.Errorf("call to hello failed: %s", err)
	}
	return nil
}
