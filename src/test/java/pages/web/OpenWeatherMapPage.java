package pages.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class OpenWeatherMapPage {
    private WebDriver driver;

    @FindBy(css = "#map-canvas")
    private WebElement weatherMap;

    @FindBy(css = ".leaflet-control-zoom-in")
    private WebElement zoomInButton;

    @FindBy(css = ".leaflet-control-zoom-out")
    private WebElement zoomOutButton;

    @FindBy(css = ".weather-map-page h1")
    private WebElement mapPageHeader;

    public OpenWeatherMapPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isWeatherMapDisplayed() {
        return weatherMap.isDisplayed();
    }

    public void zoomIn() {
        zoomInButton.click();
    }

    public void zoomOut() {
        zoomOutButton.click();
    }

    public String getMapPageTitle() {
        return driver.getTitle();
    }

    public String getMapPageHeader() {
        return mapPageHeader.getText();
    }

    public boolean isZoomControlsDisplayed() {
        return zoomInButton.isDisplayed() && zoomOutButton.isDisplayed();
    }
}