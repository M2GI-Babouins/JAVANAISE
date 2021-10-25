package irc;

public interface ISentence {

    @Action(name = "write")
    void write(String text);

    @Action(name = "read")
    String read();
}
