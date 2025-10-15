// src/test/java/checks/JUnitEngineCheck.java
package checks;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class JUnitEngineCheck {
    @Test
    public void sanity() {
        System.out.println("SANITY RUN");
        assertTrue(true);
    }
}