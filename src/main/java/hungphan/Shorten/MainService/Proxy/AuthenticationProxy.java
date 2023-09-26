package hungphan.Shorten.MainService.Proxy;

import hungphan.Shorten.MainService.Entity.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AUTHEN-SERVICE")
public interface AuthenticationProxy {
    @PostMapping(value = "/authen")
    public ResponseEntity<BaseResponse> authen(@RequestBody String authHeader);
}
