package de.bripkens.nashorn;

import org.apache.commons.io.IOUtils;
import org.junit.Before;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ben Ripkens <ben.ripkens@codecentric.de>
 */
public abstract class AbstractNashornTest {

  protected ScriptEngine engine;

  @Before
  public void before() throws ScriptException {
    engine = newEngine();
  }

  public ScriptEngine newEngine() {
    ScriptEngineManager manager = new ScriptEngineManager();
    return manager.getEngineByName("nashorn");
  }

  public static String slurp(String filename) {
    InputStream in = AbstractNashornTest.class.getResourceAsStream(filename);
    try {
      return IOUtils.toString(in, "UTF-8");
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  public static String join(String... parts) {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < parts.length; i++) {
      if (i > 0) {
        builder.append('\n');
      }
      builder.append(parts[i]);
    }

    return builder.toString();
  }

}
