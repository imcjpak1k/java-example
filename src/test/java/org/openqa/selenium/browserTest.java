package org.openqa.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * xpath정보
 * https://tobuymacbookpro.tistory.com/44
 *
 */
public class browserTest {

    /**
     * 크롬브라우저 실행 및 종료
     */
    @Test
    public void open() {
//        System.setProperty("webdriver.chrome.driver","/Users/cjpak/tools/selenium/chromedriver");
        ChromeOptions options = new ChromeOptions();
        ChromeDriver driver = new ChromeDriver(options);
        driver.get("http://naver.com");
        System.out.printf("" +
                        "" +
                        "브라우저 제목 : %s\n" +
                        "url : %s\n" +
                        "session id : %s\n"
                , driver.getTitle()
                , driver.getCurrentUrl()
                , driver.getSessionId()

        );

        // 페이지이동
        driver.navigate().to("https://finance.naver.com/");

        try {
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }


        driver.quit();
    }
    /**
     * 페이지이동
     */
    @Test
    public void navigation() {
        ChromeOptions options = new ChromeOptions();
        ChromeDriver driver = new ChromeDriver(options);
        driver.get("http://naver.com");
        printInfo(driver);

        // 페이지이동
        // 증권링크를 선택해서 이동하기
        driver.findElement(By.linkText("증권")).click();
//        driver.navigate().to("https://finance.naver.com/");
        printInfo(driver);

        // 뒤로이동
        driver.navigate().back();
        printInfo(driver);

        // 앞으로이동
        driver.navigate().forward();
        printInfo(driver);

        // 새로고침
        driver.navigate().refresh();
        printInfo(driver);

        driver.quit();
    }

    private void printInfo(ChromeDriver driver) {
        System.out.printf("" +
                        "브라우저 제목 : %s\n" +
                        "url : %s\n" +
                        "session id : %s\n"
                , driver.getTitle()
                , driver.getCurrentUrl()
                , driver.getSessionId()
        );

        try {
            // 2초 대기
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        }
    }

    /**
     * 음.. 이부분은 모르겠음...
     */
    @Test
    public void alerts() {
        long sleep_millis = 1_000 * 2;

        ChromeDriver driver = new ChromeDriver();
        driver.get("https://www.selenium.dev/documentation/webdriver/browser/alerts/");

        driver.findElement(By.linkText("See an example alert")).click();

        try {
            Thread.sleep(sleep_millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Alert alert = driver.switchTo().alert();
        String text = alert.getText();
        System.out.println(text);
        alert.accept();

        // 스크립트 실행
        driver.executeScript("alert((arguments[0]/1000)+'초 후 브라우저가 종료됩니다.');", sleep_millis);

        //Store the alert in a variable for reuse
        text = alert.getText();
        System.out.println(text);

        try {
            Thread.sleep(sleep_millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Press th Ok Button
        alert.accept();


        //Press the Cancel button
//        alert.dismiss();

        driver.quit();
    }

    @Test
    public void confirm() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.selenium.dev/documentation/webdriver/browser/alerts/");

//        driver.findElement(By.xpath("//a[text()='See a sample confirm']")).click();
//        driver.findElement(By.xpath("/html/body/div/div[1]/div/main/div/p[4]/a")).click();
//        /html/body/div/div[1]/div/main/div/p[4]/a
        driver.findElement(By.linkText("See a sample confirm")).click();


        Alert alert = driver.switchTo().alert();
        System.out.printf("confirm message : %s \n", alert.getText());

        try {
            Thread.sleep(1_000l * 2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Press the Cancel button
        alert.dismiss();

        driver.quit();
    }

    @Test
    public void prompt() throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.selenium.dev/documentation/webdriver/browser/alerts/");

        // link click
        driver.findElement(By.linkText("See a sample prompt")).click();

        Alert alert = driver.switchTo().alert();

        // 음.. 왜 안되지..??
        alert.sendKeys("메세지를 입력합니다.");

        Thread.sleep(1000l * 2);

        alert.accept();

        driver.quit();
    }


}
