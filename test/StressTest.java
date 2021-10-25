import irc.ISentence;
import jvn.JvnCoordImpl;
import jvn.JvnServerImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class StressTest {

    ISentence sentence;

    @BeforeEach
    void Init() throws Exception {
        new JvnCoordImpl();


        JvnServerImpl js = JvnServerImpl.jvnGetServer();
        sentence = (ISentence)js.jvnLookupObject("IRC");
    }

    @Test
    public void simpleReadWrite(){
        sentence.write("test");
        Assertions.assertEquals("test",sentence.read());
    }

}