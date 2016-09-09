package it.poliba.sisinflab.simlib.input.pathfile;

import it.poliba.sisinflab.simlib.exceptions.InvalidStatementException;
import it.poliba.sisinflab.simlib.input.Statement;
import it.poliba.sisinflab.simlib.input.StatementFileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Reads files made by PathTriple statements
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class TabPathsFileReader extends StatementFileReader {

    public TabPathsFileReader(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public TabPathsFileReader(File file) throws FileNotFoundException {
        super(file);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List readFile() throws IOException {
        System.out.println("Reading file: " + inputFile.getName());

        List<Statement> tripleList = Files.lines(Paths.get(inputFile.getPath()), Charset.forName("UTF-8"))
                .map(this::processLine)
                .flatMap(Collection::stream)
                .collect(toList());

        System.out.println("Statements: " + tripleList.size());

        return tripleList;

    }

    private List<PathTriple> processLine(String line){
        List<PathTriple> tripleList = new ArrayList<>();

        String lineFetch[] = line.replace(" ", "").split("\t");

        if(lineFetch.length != 3){
            throw new InvalidStatementException(line + " is not a valid PathTriple statement");
        }

        String subject = lineFetch[0];
        LinkedList<String> pathList = new LinkedList<>(asList(lineFetch[1].split("--")));

        //creating an array with all the objects (removing squared brackets)
        String objectsString = lineFetch[2].substring(1, lineFetch[2].length() - 1);
        String objectsFetch[] = objectsString.split(",");

        //creating triple objects
        for (String object : objectsFetch) {
            PathTriple t = new PathTriple(subject, pathList, object);
            tripleList.add(t);
        }

        if(subject == null || objectsFetch.length == 0 || pathList.size() == 0)
            throw new InvalidStatementException(line + " cannot be converted in a valid PathTriple object");

        return tripleList;
    }
}

