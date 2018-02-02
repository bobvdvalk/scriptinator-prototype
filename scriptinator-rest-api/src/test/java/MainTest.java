import org.junit.Assert;
import org.junit.Test;

public class MainTest {

    @Test
    public void simpleTest() {
        String var = "I am stupid!";
        String var2 = "I am stupid!";

        Assert.assertEquals(var, var2);
    }
}
