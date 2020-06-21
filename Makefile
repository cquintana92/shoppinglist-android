.PHONY: compile compile_release fmt fmt_check help

all: help

compile: ## Build a debug version
	./gradlew assembleDebug

compile_release: ## Build a release version
	./gradlew assembleRelease

fmt_check: ## Check the Kotlin format
	ktlint "app/src/**/*.kt" '!**/*Test.kt'

fmt: ## Correct the kotlin format
	ktlint -F "app/src/**/*.kt" '!**/*Test.kt'

help: ## Display this help screen
	@grep -h -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'