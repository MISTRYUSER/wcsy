package com.example.reggie.Controller;

import com.example.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;
/**
 * 文件上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${upload.filePath}")
    private String basePath;

    // 文件上传
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        // file是一个临时文件，需要转存到磁盘中的某个指定位置，否则本次请求完成后，临时文件file会删除
        //   upload方法名中的参数名 必须是file（文件上传表单的 中name属性值必须是file,name="file"）
        log.info("上传的文件为: "+file.toString());

        // 原始文件名
        String originFileName = file.getOriginalFilename();   // 设 fileName为 abc.jpg
        String suffix = originFileName.substring(originFileName.lastIndexOf("."));  // suffix = .jpg

        // 使用UUID重新生成文件名，防止文件名重复，造成后面上传的文件覆盖前面上传的文件
        String fileName = UUID.randomUUID().toString() + suffix;

        // 创建一个目录对象 dir
        File dir = new File(basePath);

        if (!dir.exists()){
            dir.mkdirs();
        }

        try {
            // 将临时文件转存到指定 位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.success(fileName);
    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response)  {

        try {
            // 通过输入流来读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath +name));

            //  通过输出流将文件写回到浏览器，并在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            int  len = 0;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}