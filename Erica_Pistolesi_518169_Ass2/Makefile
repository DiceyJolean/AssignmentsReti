JAVAC = javac
JAVA = java

TARGETS			=	Main

.PHONY = all test clean

%: %.class
	$(JAVA) $@

%.class: %.java
	$(JAVAC) $<

all		: $(TARGETS)

clean	:
	rm *.class
