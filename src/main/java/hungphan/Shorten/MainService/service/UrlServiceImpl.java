package hungphan.Shorten.MainService.service;

import com.google.common.hash.Hashing;
import hungphan.Shorten.MainService.Entity.Url;
import hungphan.Shorten.MainService.Entity.UrlDto;
import hungphan.Shorten.MainService.repository.UrlRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class UrlServiceImpl implements IUrlServicce{

    @Autowired
    private UrlRepository urlRepository;
    @Override
    public Url generateShortLink(UrlDto urlDto) {
        String encodeUrl = encodeUrl(urlDto.getUrlOriginal());

        Url url = new Url();
        url.setCreationDate(LocalDateTime.now());
        url.setOriginalUrl(urlDto.getUrlOriginal());
        url.setShortLink(encodeUrl);
        url.setExpirationDate(getExpirationDate(urlDto.getExpirationDate(), url.getCreationDate()));
        Url urlSave = saveShortLink(url);
        if(urlSave != null) return urlSave;
        return null;
    }

    private LocalDateTime getExpirationDate(String expirationDate, LocalDateTime creationDate) {
        if(StringUtils.isBlank(expirationDate)) {
            return creationDate.plusSeconds(60);
        }
        LocalDateTime expiration = LocalDateTime.parse(expirationDate);
        return expiration;
    }

    private String encodeUrl(String url) {
        String encodeUrl = "";
        LocalDateTime dateTime = LocalDateTime.now();
        encodeUrl = Hashing.murmur3_32().hashString(url.concat(dateTime.toString()), StandardCharsets.UTF_8).toString();
        return encodeUrl;
    }

    @Override
    public Url saveShortLink(Url url) {
        Url urlsave = urlRepository.save(url);
        return urlsave;
    }

    @Override
    public Url getEncodedUrl(String url) {
        Url retUrl = urlRepository.findByShortLink(url);
        return retUrl;
    }

    @Override
    public void deleteShortLink(Url url) {
        urlRepository.delete(url);
    }
}
