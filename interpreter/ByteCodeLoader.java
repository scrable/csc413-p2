
package interpreter;

import interpreter.bytecode.ByteCode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class ByteCodeLoader extends Object {

    private BufferedReader byteSource;

    /**
     * Constructor Simply creates a buffered reader.
     * YOU ARE NOT ALLOWED TO READ FILE CONTENTS HERE
     * THIS NEEDS TO HAPPEN IN LOADCODES.
     */
    public ByteCodeLoader(String file) throws IOException {
        this.byteSource = new BufferedReader(new FileReader(file));
    }

    /**
     * This function should read one line of source code at a time.
     * For each line it should:
     * Tokenize string to break it into parts.
     * Grab THE correct class name for the given ByteCode from CodeTable
     * Create an instance of the ByteCode class name returned from code table.
     * Parse any additional arguments for the given ByteCode and send them to
     * the newly created ByteCode instance via the init function.
     */
    public Program loadCodes() {
        Program p = new Program();
        ArrayList<String> args = new ArrayList<>();

        String line = null;
        try {
            line = byteSource.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (line != null) {
            StringTokenizer tok = new StringTokenizer(line);
            //we need to clear the arguments so the next loop run will only get the arguments
            //for that particular bytecode
            //we dont want args to hold ALL args, only one bytecode's args
            args.clear();

            String codeClass = CodeTable.getClassName(tok.nextToken());
            while (tok.hasMoreTokens()) {
                args.add(tok.nextToken());
            }

            ByteCode code = null;
            try {
                Class c = Class.forName("interpreter.bytecode.bytecodes." + codeClass);
                code = (ByteCode) c.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }

            //bytecode will never be null because we are assuming the source .cod file
            //contains no errors and is 100% correct, meaning the class will always be found
            //so we assert here to prevent an exception
            assert code != null;

            //initialize each bytecode and add it to the program object
            code.initCode(args);
            p.setCode(code);

            try {
                line = byteSource.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        p.resolveAddrs();
        return p;
    }
}
