package hungphan.Shorten.MainService.Controller;

import hungphan.Shorten.MainService.Entity.BaseResponse;
import hungphan.Shorten.MainService.Entity.Url;
import hungphan.Shorten.MainService.Entity.UrlDto;
import hungphan.Shorten.MainService.service.IUrlServicce;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class UrlShortenController {
    @Autowired
    private IUrlServicce urlServicce;

    @PostMapping("/generate")
    public ResponseEntity<?> generateShortLink(@Valid UrlDto urlDto) {
        Url url = urlServicce.generateShortLink(urlDto);

        UrlDto retUrl = new UrlDto();
        retUrl.setShortLink(url.getShortLink());
        retUrl.setUrlOriginal(url.getOriginalUrl());
        //retUrl.setExpirationDate(url.getExpirationDate());

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage("oke");
        baseResponse.setStatusCode(200);
        baseResponse.setData(retUrl);

        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirectToOriginaUrl(@PathVariable String shortLink, HttpServletResponse response) throws IOException {
        BaseResponse baseResponse = new BaseResponse();
        if(StringUtils.isAllEmpty(shortLink)) {
            baseResponse.setStatusCode(400);
            baseResponse.setMessage("ShortLink is empty");
            return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
        }
        Url urlRet = urlServicce.getEncodedUrl(shortLink);
        if(urlRet == null) {
            baseResponse.setMessage("Url does not exit or expired");
            baseResponse.setStatusCode(400);
            return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
        }
        if(urlRet.getExpirationDate().isBefore(LocalDateTime.now())) {
            baseResponse.setStatusCode(200);
            baseResponse.setMessage("url expired, Please try generate short link");
            return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
        }
        response.sendRedirect(urlRet.getOriginalUrl());
        return null;
    }
}
