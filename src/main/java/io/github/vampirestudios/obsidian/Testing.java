package io.github.vampirestudios.obsidian;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Testing {

    public static void main(String[] args) throws ScriptException, IOException, URISyntaxException, NoSuchMethodException {

        Path jsPath = Paths.get(Testing.class.getResource("/fibonacci.js").toURI());

        // GraalVM
        System.setProperty("polyglot.js.nashorn-compat", "true");

        ScriptEngine graalEngine = new ScriptEngineManager().getEngineByName("graal.js");
        try (BufferedReader reader = Files.newBufferedReader(jsPath)) {
            graalEngine.eval(reader);

            Invocable invocable = (Invocable) graalEngine;
            Object result = invocable.invokeFunction("factorialize", 10);

            System.out.println(result);
            System.out.println(result.getClass());
        }
    }

}
