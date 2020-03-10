package user;

/**
 * For classes that can be parsed into string, and be constructed with a string.
 * @author Ng Bob Shoaun
 */
public interface StringParsable {

    void unparse(String string);

    String parse();

}
