.PHONY: clean build setup run test lint keys

GRADLE = cd app && ./gradlew

keys:
	mkdir -p app/src/main/resources/certs
	mkdir -p app/src/test/resources/certs
	openssl genrsa -out app/src/main/resources/certs/private.pem 2048
	openssl rsa -in app/src/main/resources/certs/private.pem -pubout -out app/src/main/resources/certs/public.pem
	cp app/src/main/resources/certs/* app/src/test/resources/certs/

clean:
	$(GRADLE) clean

build:
	$(GRADLE) build

setup: keys clean build

run: setup
	$(GRADLE) bootRun --args='--spring.profiles.active=dev'

test:
	$(GRADLE) test

lint:
	$(GRADLE) spotlessCheck
