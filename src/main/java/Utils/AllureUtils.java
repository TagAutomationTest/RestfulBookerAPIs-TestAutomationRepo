package Utils;

import io.qameta.allure.Allure;
import io.restassured.response.Response;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

public class AllureUtils {
    private static final String AllureResultDirectory = "test-outputs/allure-results";
    private static final String AllureReportDirectory = "test-outputs/allure-report";
    private static final String PortNumber = PropertiesUtils.getPropertyValue("portNumber");

    private AllureUtils() {
        super();
    }

    public static void logStep(String stepName) {
        Allure.step(stepName);
    }

    /**
     * Log a parent step (optionally with child steps).
     *
     * @param parentName   Name of the parent step
     * @param childActions List of child step names (can be null or empty)
     */
    public static void logStepWithChildren(String parentName,
                                           Map<String, Consumer<String>> childActions) {
        if (childActions != null && !childActions.isEmpty()) {
            Allure.step(parentName, () -> {
                childActions.forEach((childName, action) -> {
                    Allure.step(childName, () -> {
                        try {
                            action.accept(childName); // run assertion + log
                        } catch (AssertionError | Exception e) {
                            // mark this child as failed
                            throw e;
                        }
                    });
                });
            });
        } else {
            Allure.step(parentName);
        }
    }

    public static void attachJson(String name, String content) {
        Allure.addAttachment(name, "application/json", content);
    }

    // Log step and attach JSON content
    public static void attachJsonStep(String stepName, String jsonContent) {
        Allure.step(stepName, () -> {
            Allure.addAttachment(stepName + " - Payload", "application/json", jsonContent);
        });
    }

    public static void attachComparison(String field, String expected, String actual) {
        String comparison = String.format(
                "Field: %s%nExpected: %s%nActual: %s%nStatus: %s",
                field,
                expected,
                actual,
                expected.equals(actual) ? "✅ MATCH" : "❌ MISMATCH"
        );
    }

    // 🔥 Log full API request + response
    public static void logRequestAndResponse(String stepName, String requestBody, Response response) {
        Allure.step(stepName, () -> {
            if (requestBody != null) {
                attachJson("📤 Request Body", requestBody);
            }
            if (response != null) {
                attachJson("📥 Response Body", response.asPrettyString());
                attachJson("📄 Response Headers", response.getHeaders().toString());
                attachJson("✅ Status Code", String.valueOf(response.statusCode()));
            }
        });
    }

    public static void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content);
    }

    public static void attacheLogsToAllureReport() {
        try {
            File logFile = FilesUtils.getLatestFile(LogsUtil.LOGS_PATH);
            if (!logFile.exists()) {
                LogsUtil.warn("Log file does not exist: " + LogsUtil.LOGS_PATH);
                return;
            }
            Allure.addAttachment("logs.log", Files.readString(Path.of(logFile.getPath())));
            LogsUtil.info("Logs attached to Allure report");
        } catch (Exception e) {
            LogsUtil.error("Failed to attach logs to Allure report: " + e.getMessage());
        }
    }

    public static void attachScreenshotToAllure(String screenshotName, String screenshotPath) {
        try {
            Allure.addAttachment(screenshotName, Files.newInputStream(Path.of(screenshotPath)));
        } catch (Exception e) {
            LogsUtil.error("Failed to attach screenshot to Allure report: " + e.getMessage());
        }
    }

    public static void openAllureAfterExecution() {
        try {
            // First generate the report
            generateAllureReport();
            // serveAllureReport();
            serveAllureReportOnStaticPort();

        } catch (Exception e) {
            LogsUtil.error("Failure during opening Allure report after execution: " + e.getMessage());
        }
    }

    public static void generateAllureReport() throws InterruptedException, IOException {
        try {
            LogsUtil.info("Generating Allure report...");
            ProcessBuilder generateBuilder = new ProcessBuilder("cmd.exe", "/c", "allure generate "
                    + AllureResultDirectory + " --clean -o " + AllureReportDirectory);
            Process generateProcess = generateBuilder.start();
            int generateExitCode = generateProcess.waitFor();

            if (generateExitCode != 0) {
                LogsUtil.error("Failed to generate Allure report. Exit code: " + generateExitCode);
                return;
            }

            LogsUtil.info("Allure report generated successfully.");
        } catch (Exception e) {
            LogsUtil.error("Unexpected error occurred while generating Allure report: " + e.getMessage());
            throw e;
        }
    }

    public static void serveAllureReport() {
        try {
            LogsUtil.info("Serving Allure report...");
            new ProcessBuilder("cmd.exe", "/c", "allure serve " + AllureResultDirectory).start();
            LogsUtil.info("Allure report is being served.");

        } catch (IOException e) {
            LogsUtil.error("IOException occurred while serving Allure report: " + e.getMessage());
        } catch (Exception e) {
            LogsUtil.error("Unexpected error occurred while serving Allure report: " + e.getMessage());
        }
    }

    // allure open -p 8085 test-outputs/allure-report
    public static void serveAllureReportOnStaticPort() throws IOException {
        try {
            LogsUtil.info("Serving Allure report...");
            new ProcessBuilder("cmd.exe", "/c", "allure open -p " + PortNumber + " " + AllureReportDirectory).start();
            LogsUtil.info("Allure report is being served.");

        } catch (Exception e) {
            LogsUtil.error("Unexpected error occurred while serving Allure report: " + e.getMessage());
            throw e;
        }
    }

    public static void killAllureOnlyOnPort(int port) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                // Get PID from netstat
                Process findPid = Runtime.getRuntime().exec("cmd /c netstat -ano | findstr :" + port);
                BufferedReader pidReader = new BufferedReader(new InputStreamReader(findPid.getInputStream()));
                String line;

                while ((line = pidReader.readLine()) != null) {
                    if (line.contains("LISTENING") || line.contains("ESTABLISHED")) {
                        String[] parts = line.trim().split("\\s+");
                        String pid = parts[parts.length - 1];

                        // Get process name by PID
                        Process findProc = Runtime.getRuntime().exec("cmd /c tasklist /FI \"PID eq " + pid + "\"");
                        BufferedReader procReader = new BufferedReader(new InputStreamReader(findProc.getInputStream()));
                        String procLine;
                        while ((procLine = procReader.readLine()) != null) {
                            if (procLine.toLowerCase().contains("java") || procLine.toLowerCase().contains("allure")) {
                                System.out.println("Killing PID " + pid + " running Allure/java");
                                Runtime.getRuntime().exec("cmd /c taskkill /PID " + pid + " /F");
                            }
                        }
                    }
                }

            } else {
                // macOS/Linux version
                String[] findCmd = {"/bin/sh", "-c", "lsof -i :" + port + " | grep LISTEN"};
                Process find = Runtime.getRuntime().exec(findCmd);
                BufferedReader reader = new BufferedReader(new InputStreamReader(find.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains("java") || line.toLowerCase().contains("allure")) {
                        String[] parts = line.trim().split("\\s+");
                        String pid = parts[1]; // usually second column is PID
                        System.out.println("Killing PID " + pid + " running Allure/java");
                        Runtime.getRuntime().exec("kill -9 " + pid);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error trying to kill port " + port);
            throw e;
        }
    }

    public static void createAllureEnvironmentFile() {
        Properties props = new Properties();
        props.setProperty("OS", System.getProperty("os.name"));
        props.setProperty("Environment", "QA");
        props.setProperty("Browser", "Chrome");
        props.setProperty("AppVersion", "1.0.5");

        // Ensure allure-results directory exists
        File resultsDir = new File(AllureResultDirectory);
        if (!resultsDir.exists()) {
            resultsDir.mkdir();
        }

        try (FileOutputStream fos = new FileOutputStream(AllureResultDirectory + "/environment.properties")) {
            props.store(fos, "Allure Environment Properties");
            System.out.println("Environment file created at: " + resultsDir.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyAll() {
        Path sourceDir = Paths.get("src/main/resources/allure");
        Path targetDir = Paths.get(AllureResultDirectory);

        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
                for (Path file : stream) {
                    Path targetPath = targetDir.resolve(file.getFileName());
                    Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Copied: " + file + " -> " + targetPath);
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ Failed to copy Allure files: " + e.getMessage());
        }
    }

}





