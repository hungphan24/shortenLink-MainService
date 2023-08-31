package hungphan.Shorten.MainService.service;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.hash.Hashing;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import hungphan.Shorten.MainService.Entity.Url;
import hungphan.Shorten.MainService.Entity.UrlDto;
import hungphan.Shorten.MainService.repository.UrlRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class UrlServiceImpl implements IUrlServicce{

    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public UrlDto generateShortLink(UrlDto urlDto) {
        String encodeUrl = encodeUrl(urlDto.getUrlOriginal());

        Url url = new Url();
        url.setCreationDate(LocalDateTime.now());
        url.setOriginalUrl(urlDto.getUrlOriginal());
        url.setShortLink(encodeUrl);
        url.setExpirationDate(getExpirationDate(urlDto.getExpirationDate(), url.getCreationDate()));
        Url urlSave = saveShortLink(url);
        if(urlSave != null) {
            UrlDto urlRet = new UrlDto();
            urlRet.setUrlOriginal(urlSave.getOriginalUrl());
            urlRet.setShortLink(urlSave.getShortLink());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            urlRet.setExpirationDate(urlSave.getExpirationDate().format(formatter));

            return urlRet;
        }
        return null;
    }

    private LocalDateTime getExpirationDate(String expirationDate, LocalDateTime creationDate) {
        if(StringUtils.isBlank(expirationDate)) {
            return creationDate.plusSeconds(160);
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
    public Url getEncodedUrl(String shortLink) {
        Url retUrl;
        if(redisTemplate.hasKey(shortLink)) {
            String data = redisTemplate.opsForValue().get(shortLink).toString();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()) // Register LocalDateTimeDeserializer
                    .create();

            retUrl = gson.fromJson(data, Url.class);
        } else {
            System.out.println("abcd");
            retUrl = urlRepository.findByShortLink(shortLink);
            //Gson gson = new Gson();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                    .create();
            String data = gson.toJson(retUrl);
            redisTemplate.opsForValue().set(shortLink, data);

        }
        return retUrl;

    }
    public static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formatter));
        }
    }
    public static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String dateTimeString = json.getAsString();
            return LocalDateTime.parse(dateTimeString, formatter);
        }
    }

    @Override
    public void deleteShortLink(Url url) {
        urlRepository.delete(url);
    }
}

