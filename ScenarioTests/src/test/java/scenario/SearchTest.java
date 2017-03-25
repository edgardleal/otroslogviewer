package scenario;

import org.testng.annotations.Test;
import scenario.components.LogViewPanel;
import scenario.components.MainFrame;
import scenario.components.OpenPanel;
import scenario.components.WelcomeScreen;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class SearchTest extends OtrosLogViewerBaseTest {

  @Test
  public void testSearchString() throws Exception {
    final int count = 11;

    final LogViewPanel logViewPanel = createFileAndImport(count);

    final MainFrame mainFrame = new MainFrame(robot());
    mainFrame
      .setSearchModeByString()
      .enterSearchQuery("Message 1")
      .searchNext();
    await().ignoreExceptions().until(() -> logViewPanel.logsTable().me().requireSelectedRows(0));
    mainFrame.searchNext();
    await().ignoreExceptions().until(() -> logViewPanel.logsTable().me().requireSelectedRows(9));
    mainFrame.searchNext();
    await().ignoreExceptions().until(() -> logViewPanel.logsTable().me().requireSelectedRows(10));
    mainFrame.searchNext();
    await().ignoreExceptions().until(() -> logViewPanel.logsTable().me().requireSelectedRows(0));
  }

  @Test
  public void testSearchRegex() throws Exception {
    final int count = 25;

    final LogViewPanel logViewPanel = createFileAndImport(count);

    final MainFrame mainFrame = new MainFrame(robot());
    mainFrame
      .setSearchModeByRegex()
      .enterSearchQuery("Message.*3")
      .searchNext();
    await().ignoreExceptions().until(() -> logViewPanel.logsTable().me().requireSelectedRows(2));
    mainFrame.searchNext();
    await().ignoreExceptions().until(() -> logViewPanel.logsTable().me().requireSelectedRows(12));
    mainFrame.searchNext();
    await().ignoreExceptions().until(() -> logViewPanel.logsTable().me().requireSelectedRows(22));
    mainFrame.searchNext();
    await().ignoreExceptions().until(() -> logViewPanel.logsTable().me().requireSelectedRows(2));
    mainFrame.searchNext();
  }



  private LogViewPanel createFileAndImport(int count) throws IOException, InterruptedException {
    final File file = File.createTempFile("otrosTest", "");
    logEvents(file, count);

    final MainFrame mainFrame = new MainFrame(robot());
    final OpenPanel openPanel = mainFrame
            .welcomeScreen()
            .waitFor()
            .clickOpenLogs();

    final LogViewPanel logViewPanel = openPanel
      .addFile(file)
      .importFiles();

    await()
      .atMost(10, TimeUnit.SECONDS)
      .until(() -> logViewPanel.logsTable().visibleLogsCount() == count);
    return logViewPanel;
  }

}
