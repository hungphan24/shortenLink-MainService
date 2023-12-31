package hungphan.Shorten.MainService.service;

import hungphan.Shorten.MainService.Entity.Url;
import hungphan.Shorten.MainService.Entity.UrlDto;
import org.springframework.stereotype.Service;

public interface IUrlServicce {
    public UrlDto generateShortLink(UrlDto urlDto);
    public Url saveShortLink(Url url);
    public Url getEncodedUrl(String shortLink);
    public void deleteShortLink(Url url);
}
