package org.openqa.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Comparator;
import java.util.Set;

public class CookiesTest {

    @Test
    public void addCookies() throws InterruptedException {
        WebDriver driver = new ChromeDriver();

        driver.get("http://www.example.com");
        driver.manage().addCookie(new Cookie("key1", "value1"));
        driver.manage().addCookie(new Cookie("key2", "value2"));
        driver.manage().addCookie(new Cookie("key3", "value3"));

        Thread.sleep(1_000 * 2);

        driver.quit();
    }

    @Test
    public void getCookiesNamed() throws InterruptedException {
        WebDriver driver = new ChromeDriver();

        driver.get("http://www.example.com");
        // 쿠키추가
        driver.manage().addCookie(new Cookie("날씨", "맑음"));
        driver.manage().addCookie(new Cookie("요일", "목요일"));

        // 추키 가져오기
        Cookie cookie1 = driver.manage().getCookieNamed("날씨");
        System.out.println(cookie1);
        System.out.printf("%s : %s\n", cookie1.getName(), cookie1.getValue());

        Thread.sleep(1_000 * 2);
        driver.quit();
    }

    @Test
    public void getAllCookies() throws InterruptedException {
//        WebDriver driver = new ChromeDriver();
        ChromeDriver driver = new ChromeDriver();

        driver.get("http://www.example.com");
        // 쿠키추가
        driver.manage().addCookie(new Cookie("날씨", "맑음"));
        driver.manage().addCookie(new Cookie("요일", "목요일"));
        driver.manage().addCookie(new Cookie("key1", "value1"));
        driver.manage().addCookie(new Cookie("key2", "value2"));
        driver.manage().addCookie(new Cookie("key3", "value3"));

        Set<Cookie> cookies = driver.manage().getCookies();
        cookies.stream()
                .sorted(Comparator.comparing(Cookie::getName))
                .forEach(e -> {
                    System.out.println(e);
                    try {
                        driver.executeScript(String.format("alert('name:%s, value:%s');", e.getName(), e.getValue()), 1_000l * 0.5);
                        Alert alert = driver.switchTo().alert();
                        Thread.sleep((long) (1_000 * 2));
                        alert.accept();
                    } catch (InterruptedException ex) {
//                        throw new RuntimeException(ex);
                    }
                });
//        cookies.forEach(System.out::println);

        Thread.sleep(1_000 * 5);
        driver.quit();
    }
}

