.PHONY: clean build setup run test lint

clean:
	./app/gradlew clean

build:
	./app/gradlew build

setup: clean build

run: setup
	./app/gradlew bootRun --args='--spring.profiles.active=dev'

test:
	./app/gradlew test

lint:
	./app/gradlew spotlessCheck
