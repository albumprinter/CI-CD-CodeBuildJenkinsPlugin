package dev.lsegal.jenkins.codebuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.amazonaws.services.codebuild.model.ResourceNotFoundException;
import com.amazonaws.services.codebuild.model.StopBuildRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hudson.model.Descriptor;
import hudson.model.Executor;
import hudson.model.TaskListener;
import hudson.slaves.AbstractCloudComputer;
import hudson.slaves.AbstractCloudSlave;
import hudson.slaves.CloudRetentionStrategy;
import hudson.slaves.ComputerLauncher;

class CodeBuilderAgent extends AbstractCloudSlave {
  private static final Logger LOGGER = LoggerFactory.getLogger(CodeBuilderAgent.class);
  private static final long serialVersionUID = -6722929807051421839L;
  private final transient CodeBuilderCloud cloud;

  /**
   * Creates a new CodeBuilderAgent node that provisions a
   * {@link CodeBuilderComputer}.
   *
   * @param cloud    a {@link CodeBuilderCloud} object.
   * @param name     the name of the agent.
   * @param launcher a {@link hudson.slaves.ComputerLauncher} object.
   * @throws hudson.model.Descriptor$FormException if any.
   * @throws java.io.IOException                   if any.
   */
  public CodeBuilderAgent(@Nonnull CodeBuilderCloud cloud, @Nonnull String name, @Nonnull ComputerLauncher launcher)
      throws Descriptor.FormException, IOException {
    super(name, "AWS CodeBuild Agent", "/build", 1, Mode.NORMAL, cloud.getLabel(), launcher,
        new CloudRetentionStrategy(cloud.getAgentTimeout() / 60 + 1), Collections.emptyList());
    this.cloud = cloud;
  }

  /**
   * Get the cloud instance associated with this builder
   *
   * @return a {@link CodeBuilderCloud} object.
   */
  public CodeBuilderCloud getCloud() {
    return cloud;
  }

  /** {@inheritDoc} */
  @Override
  public AbstractCloudComputer<CodeBuilderAgent> createComputer() {
    return new CodeBuilderComputer(this);
  }

  private void StopExecutors(List<Executor> executors) {
    for (final Executor executor : executors) {
      LOGGER.info("[CodeBuilder]: Interrupting executor {} from OFFLINE agent {}", executor.getNumber(), getDisplayName());
      executor.interrupt();
      LOGGER.info("[CodeBuilder]: Executor interrupted successfully");
    }
  }

  /** {@inheritDoc} */
  @Override
  protected void _terminate(TaskListener listener) throws IOException, InterruptedException {
    listener.getLogger().println("[CodeBuilder]: Terminating agent: " + getDisplayName());

    if (getLauncher() instanceof CodeBuilderLauncher) {
      String buildId = ((CodeBuilderComputer) getComputer()).getBuildId();
      if (StringUtils.isBlank(buildId)) {
        return;
      }

      try {
        StopExecutors(((CodeBuilderComputer) getComputer()).getExecutors());
        LOGGER.info("[CodeBuilder]: Stopping CodeBuild build ID {} running as OFFLINE agent {}", buildId, getDisplayName());
        cloud.getClient().stopBuild(new StopBuildRequest().withId(buildId));
      } catch (NullPointerException e) {
        LOGGER.info("[CodeBuilder]: It looks like CodeBuild build ID {} was already stopped.", buildId);
      } catch (ResourceNotFoundException e) {
        // this is fine. really.
      } catch (Exception e) {
        LOGGER.error("[CodeBuilder]: Failed to stop CodeBuild build ID {}", buildId, e);
      }
    }
  }
}
