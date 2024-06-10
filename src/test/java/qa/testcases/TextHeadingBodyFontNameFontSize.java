package qa.testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import qa.base.Base;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class TextHeadingBodyFontNameFontSize extends Base {

    public WebDriver driver;
    private final String testResultDir = "C:\\Users\\Riseup Labs\\eclipse-workspace\\FontName_FontSize_FontColor\\src\\test\\java\\TestResult";
    @SuppressWarnings("unused")
	private final int MAX_HEADING_LEVEL = 6; // Maximum heading level (h1 to hn)

    @BeforeClass
    public void setup() throws InterruptedException {
        driver = initializeBrowserAndOpenApplicationURL(prop.getProperty("browserName"));
        Thread.sleep(5000);
        createDirectoryIfNotExists(testResultDir);
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created: " + directoryPath);
            } else {
                System.out.println("Failed to create directory: " + directoryPath);
            }
        }
    }

    @Test
    public void extractTextAndHeading() {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));

        List<WebElement> headings = driver.findElements(By.xpath("//h1 | //h2 | //h3 | //h4 | //h5 | //h6"));
        List<WebElement> paragraphs = driver.findElements(By.tagName("p"));

        int headingIndex = 0;
        int paragraphIndex = 0;

        while (headingIndex < headings.size() && paragraphIndex < paragraphs.size()) {
            WebElement heading = headings.get(headingIndex);
            WebElement paragraph = paragraphs.get(paragraphIndex);

            String headingText = heading.getText().trim();
            String headingFontName = heading.getCssValue("font-family");
            String headingFontSize = heading.getCssValue("font-size");
            String headingXPath = getElementXPath(driver, heading);
            writeTextToFile("Heading: " + headingText + " Font Name: " + headingFontName +
                    " Font Size: " + headingFontSize + " XPath: " + headingXPath, "HeadingBody.txt");

            String paragraphText = paragraph.getText().trim();
            String paragraphFontName = paragraph.getCssValue("font-family");
            String paragraphFontSize = paragraph.getCssValue("font-size");
            String paragraphXPath = getElementXPath(driver, paragraph);
            writeTextToFile("Body: " + paragraphText + " Font Name: " + paragraphFontName +
                    " Font Size: " + paragraphFontSize + " XPath: " + paragraphXPath, "HeadingBody.txt");

            headingIndex++;
            paragraphIndex++;
        }

        // If there are remaining headings or paragraphs
        while (headingIndex < headings.size()) {
            WebElement heading = headings.get(headingIndex);
            String headingText = heading.getText().trim();
            String headingFontName = heading.getCssValue("font-family");
            String headingFontSize = heading.getCssValue("font-size");
            String headingXPath = getElementXPath(driver, heading);
            writeTextToFile("Heading: " + headingText + " Font Name: " + headingFontName +
                    " Font Size: " + headingFontSize + " XPath: " + headingXPath, "HeadingBody.txt");
            headingIndex++;
        }

        while (paragraphIndex < paragraphs.size()) {
            WebElement paragraph = paragraphs.get(paragraphIndex);
            String paragraphText = paragraph.getText().trim();
            String paragraphFontName = paragraph.getCssValue("font-family");
            String paragraphFontSize = paragraph.getCssValue("font-size");
            String paragraphXPath = getElementXPath(driver, paragraph);
            writeTextToFile("Body: " + paragraphText + " Font Name: " + paragraphFontName +
                    " Font Size: " + paragraphFontSize + " XPath: " + paragraphXPath, "HeadingBody.txt");
            paragraphIndex++;
        }
    }

    private String getElementXPath(WebDriver driver, WebElement element) {
        return (String)((JavascriptExecutor) driver).executeScript(
                "function absoluteXPath(element) {" +
                        "var comp, comps = [];" +
                        "var parent = null;" +
                        "var xpath = '';" +
                        "var getPos = function(element) {" +
                        "var position = 1, curNode;" +
                        "if (element.nodeType == Node.ATTRIBUTE_NODE) {" +
                        "return null;" +
                        "}" +
                        "for (curNode = element.previousSibling; curNode; curNode = curNode.previousSibling) {" +
                        "if (curNode.nodeName == element.nodeName) {" +
                        "++position;" +
                        "}" +
                        "}" +
                        "return position;" +
                        "};" +

                        "if (element instanceof Document) {" +
                        "return '/';" +
                        "}" +

                        "for (; element && !(element instanceof Document); element = element.nodeType == Node.ATTRIBUTE_NODE ? element.ownerElement : element.parentNode) {" +
                        "comp = comps[comps.length] = {};" +
                        "switch (element.nodeType) {" +
                        "case Node.TEXT_NODE:" +
                        "comp.name = 'text()';" +
                        "break;" +
                        "case Node.ATTRIBUTE_NODE:" +
                        "comp.name = '@' + element.nodeName;" +
                        "break;" +
                        "case Node.PROCESSING_INSTRUCTION_NODE:" +
                        "comp.name = 'processing-instruction()';" +
                        "break;" +
                        "case Node.COMMENT_NODE:" +
                        "comp.name = 'comment()';" +
                        "break;" +
                        "case Node.ELEMENT_NODE:" +
                        "comp.name = element.nodeName;" +
                        "break;" +
                        "}" +
                        "comp.position = getPos(element);" +
                        "}" +

                        "for (var i = comps.length - 1; i >= 0; i--) {" +
                        "comp = comps[i];" +
                        "xpath += '/' + comp.name.toLowerCase();" +
                        "if (comp.position !== null) {" +
                        "xpath += '[' + comp.position + ']';" +
                        "}" +
                        "}" +

                        "return xpath;" +

                        "} return absoluteXPath(arguments[0]);", element);
    }

    private void writeTextToFile(String text, String fileName) {
        // Remove XPath from the text
        text = text.replaceAll(" XPath: .*", "");

        String filePath = testResultDir + File.separator + fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(text + "\n");
            System.out.println("Text saved to file: " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
