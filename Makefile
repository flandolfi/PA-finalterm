LATEX = ./doc/latex

.PHONY: all clean doc clean-doc

all: doc

doc:
	cd ${LATEX} && make all

clean: clean-doc

clean-doc:
	cd ${LATEX} && make clean
