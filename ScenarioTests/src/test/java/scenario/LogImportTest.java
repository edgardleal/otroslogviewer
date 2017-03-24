package scenario;

import org.assertj.swing.launcher.ApplicationLauncher;
import org.assertj.swing.testng.testcase.AssertJSwingTestngTestCase;
import org.awaitility.Awaitility;
import org.testng.annotations.Test;
import pl.otros.logview.gui.LogViewMainFrame;
import scenario.components.ConfirmClose;
import scenario.components.LogViewPanel;
import scenario.components.MainFrame;
import scenario.components.OpenPanel;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.stream.IntStream;


/**
 * Reaping keys on mac
 * <p>
 * http://osxdaily.com/2011/08/04/enable-key-repeat-mac-os-x-lion/
 * defaults write -g ApplePressAndHoldEnabled -bool false
 * <p>
 * LogViewMainFrame:228 to work faster have to comment this line:
 * //    Toolkit.getDefaultToolkit().getSystemEventQueue().push(new EventQueueProxy());
 */
public class LogImportTest extends AssertJSwingTestngTestCase {

  @Test
  public void testImport1SmallFile() throws Exception {
    final File file = File.createTempFile("otrosTest", "");
    logEvents(file, 10);

    final MainFrame mainFrame = new MainFrame(robot());
    final OpenPanel openPanel = mainFrame.welcomeScreen().clickOpenLogs();

    final LogViewPanel logViewPanel = openPanel
      .addFile(file)
      .importFiles();

    Awaitility.await()
      .atMost(10, TimeUnit.SECONDS)
      .until(() -> logViewPanel.logsTable().visibleLogsCount() == 10);

    mainFrame.tabBar().tab().close();

    ConfirmClose.close(robot());

    mainFrame.welcomeScreen().waitFor();
  }

  @Test
  public void testImport2SmallFiles() throws Exception {
    final File file1 = File.createTempFile("otrosTest", "");
    logEvents(file1, 10);
    final File file2 = File.createTempFile("otrosTest", "");
    logEvents(file1, 10);

    final MainFrame mainFrame = new MainFrame(robot());
    final OpenPanel openPanel = mainFrame.welcomeScreen().clickOpenLogs();

    final LogViewPanel logViewPanel = openPanel
      .addFile(file1)
      .addFile(file2)
      .importFiles();

    Awaitility.await()
      .atMost(10, TimeUnit.SECONDS)
      .until(() -> logViewPanel.logsTable().visibleLogsCount() == 20);

    mainFrame.tabBar().tab().close();

    ConfirmClose.close(robot());

    mainFrame.welcomeScreen().waitFor();
  }

  @Test
  public void testImport10kEvents() throws Exception {
    final File file = File.createTempFile("otrosTest", "");
    logEvents(file, 10_000);

    final MainFrame mainFrame = new MainFrame(robot());
    final OpenPanel openPanel = mainFrame.welcomeScreen().clickOpenLogs();

    final LogViewPanel logViewPanel = openPanel
      .addFile(file)
      .importFiles();

    Awaitility.await()
      .atMost(30, TimeUnit.SECONDS)
      .until(() -> logViewPanel.logsTable().visibleLogsCount() == 10_000);

    mainFrame.tabBar().tab().close();

    ConfirmClose.close(robot());

    mainFrame.welcomeScreen().waitFor();
  }

  @Test
  public void testImport100kEvents() throws Exception {
    final File file = File.createTempFile("otrosTest", "");
    logEvents(file, 100_000);

    final MainFrame mainFrame = new MainFrame(robot());
    final OpenPanel openPanel = mainFrame.welcomeScreen().clickOpenLogs();

    final LogViewPanel logViewPanel = openPanel
      .addFile(file)
      .importFiles();

    Awaitility.await()
      .atMost(30, TimeUnit.SECONDS)
      .until(() -> logViewPanel.logsTable().visibleLogsCount() == 100_000);

    mainFrame.tabBar().tab().close();

    ConfirmClose.close(robot());

    mainFrame.welcomeScreen().waitFor();
  }

  private void logEvents(File file, int count) throws IOException {
    final Logger logger = Logger.getLogger("some logger");
    logger.setUseParentHandlers(false);
    logger.addHandler(new FileHandler(file.getAbsolutePath()));
    IntStream
      .rangeClosed(1, count)
      .forEach(i -> logger.info("Message " + i));
  }

  @Override
  protected void onSetUp() {
    System.setProperty("runForScenarioTest", "true");

    ApplicationLauncher.application(LogViewMainFrame.class).start();
  }

}
