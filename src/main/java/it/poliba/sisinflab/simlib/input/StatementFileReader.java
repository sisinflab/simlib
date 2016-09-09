package it.poliba.sisinflab.simlib.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Reads files listing some statements
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public abstract class StatementFileReader {

    public static final String TAB_TRIPLES = "tab_triples";
    public static final String TAB_PATHS = "tab_paths";

    protected File inputFile;

    public StatementFileReader(String fileName) throws FileNotFoundException {
        this.inputFile = new File(fileName);
    }

    public StatementFileReader(File file) throws FileNotFoundException {
        this.inputFile = file;
    }

    /**
     * Reads and parses the statements file
     *
     * @return a list of statements
     * @throws IOException if the file doesn't exist
     */
    public abstract List<Statement> readFile() throws IOException;

}
