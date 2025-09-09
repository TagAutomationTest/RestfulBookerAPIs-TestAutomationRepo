package Listners;

import Utils.*;

import clients.RestClient;
import org.testng.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static Utils.PropertiesUtils.loadProperties;

public class TestNGListeners implements IExecutionListener, ITestListener, IInvokedMethodListener, ISuiteListener {

    File allure_results = new File("test-outputs/allure-results");
    File logs = new File("test-outputs/Logs");
    File screenshots = new File("test-outputs/screenshots");
    private static final Map<String, Throwable> configFailures = new ConcurrentHashMap<>();
    int passed = 0, failed = 0, skipped = 0;

    @Override
    public void onExecutionStart() {

        LogsUtil.info("Test Execution started");
        loadProperties();
        FilesUtils.deleteFiles(allure_results);
        FilesUtils.cleanDirectory(logs);
        FilesUtils.createDirectory(allure_results);
        FilesUtils.createDirectory(logs);
        AllureUtils.copyAll();
        try {
            AllureUtils.killAllureOnlyOnPort(Integer.parseInt(PropertiesUtils.getPropertyValue("portNumber")));
        } catch (IOException ex) {
            LogsUtil.info("Failure during KillAllure");
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void onExecutionFinish() {

        LogsUtil.info("===== Execution Summary =====");
        LogsUtil.info("Total Passed: " + passed);
        LogsUtil.info("Total Failed: " + failed);
        LogsUtil.info("Total Skipped: " + skipped);
        LogsUtil.info("Test Execution finished");
        EmailUtil.sendEmilReport(passed, failed, skipped);

        LogsUtil.info("Generating and serving Allure report...");
        AllureUtils.openAllureAfterExecution();

    }


    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isConfigurationMethod()) {
            LogsUtil.info("⚙️ Preparing configuration for ", method.getTestMethod().getMethodName());
        }
        if (method.isTestMethod()) {
            LogsUtil.info("🚀 Starting test: ", testResult.getName());
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        AllureUtils.attacheLogsToAllureReport();
    }

    @Override
    public void onStart(ISuite suite) {
        try {
            Class.forName("clients.RestClient"); // 🔑 Forces JVM to load & run static block
            System.out.println("🔧 RestClient initialized via TestNG Listener");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("❌ Failed to load RestClient", e);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LogsUtil.info("Test case", result.getName(), "passed");
        passed++;
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LogsUtil.info("Test case", result.getName(), "failed");
        failed++;
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LogsUtil.info(" test case : ", result.getName(), "⏭️ Skipped", "With failure" + result.getThrowable().getMessage());
        skipped++;
    }


}