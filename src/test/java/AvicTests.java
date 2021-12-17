import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.By.xpath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AvicTests {
    private WebDriver driver;

    @BeforeTest
    public void profileSetUp(){
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
    }

    @BeforeMethod
    public void testSetUp(){
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://avic.ua/");
    }

    @Test (priority = 1)
    public void checkPageIsNavigatedWithExpectedTitle(){
        driver.findElement(xpath("//li[@class='parent js_sidebar-item']/a[contains(@href, 'elektronika')]")).click();
        driver.findElement(xpath("//div[@class='brand-box__info']//a[contains(@href, 'myishi')]")).click();
        String actual = driver.getTitle();
        String expected = "Мыши - ⚡Купить компьютерную мышь⚡ по низкой цене в Киеве, Харькове с доставкой по всей Украине : Avic";
        assertEquals(actual, expected);
    }

    @Test (priority = 2)
    public void checkThatFilterResultsContainsSearchWord(){
        driver.findElement(xpath("//li[@class='parent js_sidebar-item']/a[contains(@href, 'elektronika')]")).click();
        driver.findElement(xpath("//div[@class='brand-box__info']//a[contains(@href, 'myishi')]")).click();

        String pathToSeeAll = "//div[@class=\"filter-wrapp\"]/div[@class='filter__items checkbox']/a[text()='Показать все'][1]";
        WebElement seeAll = driver.findElement(xpath(pathToSeeAll));
        Actions moveToSeeAll = new Actions(driver);
        moveToSeeAll.moveToElement(seeAll);
        moveToSeeAll.perform();
        seeAll.click();
        // ничего лучше не придумала как испольховать индекс так, как таких кнопки 4, предки с одинаковыми именами

        String pathToRazer = "//div[@class='filter-area js_filter_parent']//a[contains(@href, razer)][text()='Razer']";
        WebElement razer = driver.findElement(xpath(pathToRazer));
        Actions moveToRazer = new Actions(driver);
        moveToRazer.moveToElement(razer);
        moveToRazer.perform();
        razer.click();

        new WebDriverWait(driver, 30).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        List<WebElement> elementList = driver.findElements(xpath("//div[@class='prod-cart__descr']"));
        for (WebElement webElement : elementList) {
            assertTrue(webElement.getText().contains("Razer"));
        }
    }

    @Test (priority = 3)
    public void checkThatResultOfFilterSortingAscendingPrice(){
        driver.findElement(xpath("//li[@class='parent js_sidebar-item']/a[contains(@href, 'telefonyi-i-aksessuaryi')]")).click();
        driver.findElement(xpath("//div[@class='brand-box__title']//a[contains(@href,'smartfonyi')]")).click();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

//        WebElement testDropDown = driver.findElement(By.cssSelector(".js-select.sort-select.js_sort_select.select2-hidden-accessible"));
//        Select dropdown = new Select(testDropDown);
//        dropdown.selectByValue("priceasc");

//        driver.findElement(By.cssSelector(".js-select.sort-select.js_sort_select.select2-hidden-accessible")).click();
        driver.findElement(xpath("//div[@class='category-top']//span[@class='select2 select2-container select2-container--sort select2-container--default']//span[@class='select2-selection select2-selection--single']")).click();
//        driver.findElement(By.cssSelector(".select2-selection.select2-selection--single")).click();
//        driver.findElement(By.cssSelector(".select2.select2-container.select2-container--sort.select2-container--default.select2-container--above.select2-container--focus")).click();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.findElement(xpath("//li[text()='От дешевых к дорогим']")).click();

        List<WebElement> elementList = driver.findElements(xpath("//div[@class='prod-cart__prise-new']"));
        for(int i = 0; i < elementList.size()-1; i++) {
            int currentPrice = Integer.valueOf(elementList.get(i).getText().split(" ")[0]);
            int nextPrice = Integer.valueOf(elementList.get(i+1).getText().split(" ")[0]);
            assertTrue(currentPrice <= nextPrice, "List is not ascending:"+elementList.get(i).getText().split(" ")[0]+">"+elementList.get(i+1).getText().split(" ")[0]);
        }
    }

    @Test(priority = 4)
    public void check (){
        driver.findElement(xpath("//span[@class='sidebar-item']")).click();//каталог товаров
        driver.findElement(xpath("//ul[contains(@class,'sidebar-list')]//a[contains(@href, 'apple-store')]")).click();//Apple Store
        driver.findElement(xpath("//div[@class='brand-box__title']/a[contains(@href,'iphone')]")).click();//iphone
        new WebDriverWait(driver, 30).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));//wait for page loading
        driver.findElement(xpath("//a[@class='prod-cart__buy'][contains(@data-ecomm-cart,'Starlight (MLPG3)')]")).click();//add to cart iphone
        WebDriverWait wait = new WebDriverWait(driver, 30);//ждем пока не отобразится попап с товаром добавленным в корзину
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("js_cart")));
        driver.findElement(xpath("//div[@class='btns-cart-holder']//a[contains(@class,'btn--orange')]")).click();//продолжить покупки
        driver.findElement(xpath("//div[@class='header-bottom__cart active-cart flex-wrap middle-xs js-btn-open']"));
        driver.findElement(xpath("//i[@class='icon icon-close js-btn-close']"));
        new WebDriverWait(driver, 30).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        String actualProductsCountInCart =
                driver.findElement(xpath("//div[contains(@class,'header-bottom__cart')]//div[contains(@class,'cart_count')]"))
                        .getText();//получили 1 которая в корзине (один продукт)
        assertEquals(actualProductsCountInCart, "");
    }



    @AfterMethod
    public void tearDown(){
        driver.quit();
    }

}
