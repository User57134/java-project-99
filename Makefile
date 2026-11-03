.PHONY: clean build setup run test lint

clean:
	./gradlew clean

build:
	./gradlew build

setup: clean build


run: setup
	./gradlew bootRun --args='--spring.profiles.active=dev'

test:
	./gradlew test

lint:
	./gradlew spotlessCheck
