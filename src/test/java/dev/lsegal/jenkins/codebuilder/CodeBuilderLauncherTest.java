package dev.lsegal.jenkins.codebuilder;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CodeBuilderLauncherTest {
  @Test
  public void startupRetryScript_retries_agent_and_always_exits_zero() {
    String script = CodeBuilderLauncher.startupRetryScript("jenkins-agent -noreconnect -url \"http://j/\"");
    assertTrue(script.contains("while [ $attempt -lt 60 ]"));
    assertTrue(script.contains("jenkins-agent -noreconnect -url \"http://j/\" && break"));
    assertTrue(script.contains("-ge 30 ] && break"));
    assertTrue(script.endsWith("exit 0"));
  }
}
