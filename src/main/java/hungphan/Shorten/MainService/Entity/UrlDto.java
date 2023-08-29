package hungphan.Shorten.MainService.Entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public class UrlDto {

    @NotNull(message = "Url not null")
    @NotEmpty(message = "Url not empty")
    @URL(message = "Please enter the correct format")
    private String urlOriginal;
    private String expirationDate;
    private String shortLink;

    public UrlDto(String url, String expirationDate) {
        this.urlOriginal = url;
        this.expirationDate = expirationDate;
    }

    public UrlDto() {

    }

    public String getUrlOriginal() {
        return urlOriginal;
    }

    public void setUrlOriginal(String url) {
        this.urlOriginal = url;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getShortLink() {
        return shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }
}
