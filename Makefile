DOC_DIR = ./doc/latex

.PHONY: all clean doc clean-doc

all: doc

doc:
	cd ${DOC_DIR} && make all

clean: clean-doc

clean-doc:
	cd ${DOC_DIR} && make clean
