package kr.co.deliverydispatch.controller;

import kr.co.cntt.core.util.AES256Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Controller
public class MainController {

    @Value("${api.tracker.key}")
    private String tKey;

    /**
     * 로그인 페이지
     *
     * @return
     */
    @GetMapping("/login")
    public String login() { return "/login/login"; }


    /**
     * 로그아웃
     *
     * @return
     */
    @GetMapping("/logout")
    public String logout() { return "redirect:/login"; }


    /**
     * 공사중 페이지
     *
     * @return
     */
    @GetMapping("/caution")
    public String caution() {
        return "/caution";
    }


    @GetMapping("/tracker")
    public String tracker(Model model) {
        String param = "token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0d190cmFja2VyIiwiYXVkaWVuY2UiOiJ3ZWIiLCJjcmVhdGVkIjoxNTIxNjAwMjIxMzc5fQ.fQYha8zo4g8i2xDhF6wpDYqawl-BQF-RcTQZ8vCl3iA&level=4&code=016&regOrderId=15";

        try {
            AES256Util aesUtil = new AES256Util(tKey);
            String encParam = aesUtil.aesEncode(param);

            model.addAttribute("strParam", param);
            model.addAttribute("encParam", encParam);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return "/tracker";
    }

}
