package com.example.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.Service.UserService;
import com.example.reggie.Utils.SMSUtils;
import com.example.reggie.Utils.ValidateCodeUtils;
import com.example.reggie.common.Result;
import com.example.reggie.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Session;

import org.apache.commons.codec.language.AbstractCaverphone;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String Phone = user.getPhone();
        if (StringUtils.isNotEmpty(Phone)) {
            //生产随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code :{}", code);
            //使用阿里云提供的短信服务API完成发送短信
            // SMSUtils.sendMessage("瑞吉外卖","", Phone,code);

            //需要将生产的验证码保存到Session
            session.setAttribute(Phone, code);

            return Result.success("短信发送成功");

        }
        return Result.error("短信发送失败");
    }

    /**
     * 移动端登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session){
        log.info("userMap:{}"+map.toString());
        //获取手机号
        String Phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
        Object codeInSession = session.getAttribute(Phone);

        //进行验证码的比对（页面提交的验证码和Session中保存的验证码进行对比)
        if(codeInSession != null && codeInSession.equals(code)) {
            //对比成功登录成功

            //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,Phone);

            User user = userService.getOne(queryWrapper);
            if(user ==null){
                user = new User();
                user.setPhone(Phone);
                user.setStatus(1);
                userService.save(user);
                return Result.success(user);
            }
            session.setAttribute("user",user.getId());
            return Result.success(user);
        }
        return Result.error("登陆失败");
    }

    @PostMapping("/loginout")
    public Result<String> loginout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return Result.success("登出成功");

    }
}
