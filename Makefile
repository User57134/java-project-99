GRADLE = cd app && ./gradlew

.PHONY: clean build setup run test lint

clean:
	$(GRADLE) clean

build:
	$(GRADLE) build

setup: clean build

run: setup
	$(GRADLE) bootRun --args='--spring.profiles.active=dev'

test:
	$(GRADLE) test

lint:
	$(GRADLE) spotlessCheck
