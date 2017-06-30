NAME = finalterm
SRC = tex/${NAME}.tex
OBJ = ${NAME}.pdf
OUT_DIR = out
CMD = pdflatex
OPTS = -interaction=nonstopmode -jobname=${NAME} -output-directory=${OUT_DIR}
RMDIR = ${RM} -rf

.PHONY: all clean init

all: ${OBJ}

${OBJ}: ${SRC} init
	${CMD} ${OPTS} ${SRC}

init: ${OUT_DIR}

${OUT_DIR}:
	mkdir -p ${OUT_DIR}

clean:
	rm -rf ${OUT_DIR}
