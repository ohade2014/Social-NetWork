CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread -lboost_filesystem -lboost_thread

all: BGSclient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/ReciveMessage.o bin/SendMessage.o bin/BGSclient.o $(LDFLAGS)

BGSclient: bin/connectionHandler.o bin/ReciveMessage.o bin/SendMessage.o bin/BGSclient.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/SendMessage.o: src/SendMessage.cpp
	g++ $(CFLAGS) -o bin/SendMessage.o src/SendMessage.cpp

bin/ReciveMessage.o: src/ReciveMessage.cpp
	g++ $(CFLAGS) -o bin/ReciveMessage.o src/ReciveMessage.cpp

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
