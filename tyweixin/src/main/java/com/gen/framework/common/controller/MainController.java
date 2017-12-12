package com.gen.framework.common.controller;

import com.gen.framework.common.config.MainGlobals;
import com.gen.framework.common.vo.ResponseVO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

@Controller
@RequestMapping("/")
public class MainController {
    private final Logger logger = LoggerFactory.getLogger(MainController.class);
    @Autowired
    private MainGlobals mainGlobals;

    @GetMapping("/404")
    public String to404(){
        return "pages/manager/common/404";
    }

    @PostMapping("/rs/do-upload")
    @ResponseBody
    public ResponseVO doUpload(MultipartFile file){
        FileOutputStream fos=null;
        try{
            File rsDir=new File(mainGlobals.getRsDir());
            if(!rsDir.exists()){
                rsDir.mkdirs();
            }
            File destFile=new File(rsDir,file.getOriginalFilename());
            fos=new FileOutputStream(destFile);
            IOUtils.write(file.getBytes(),fos);
            return new ResponseVO(1,"上传成功",null);
        }catch (Exception e){
            logger.error("MainController->doUpload",e);
        }finally {
            if(fos!=null)IOUtils.closeQuietly(fos);
        }
        return new ResponseVO(-2,"上传失败",null);
    }

    @GetMapping("/rs/{filename:.+}")
    public void toInputImg(@PathVariable String filename, HttpServletResponse res){
        OutputStream os=null;
        byte[] bytes=null;
        try {
            res.setContentType("image/jpeg");
            File file=new File(mainGlobals.getRsDir(),filename);
            if(file.exists()){

                os=res.getOutputStream();
                bytes=FileUtils.readFileToByteArray(file);
                if(bytes!=null){
                    IOUtils.write(bytes,os);
                }
            }
        }catch (Exception e){
            logger.error("MainController->toInputImg",e);
        }finally {
            IOUtils.closeQuietly(os);
            bytes = null;

        }
    }
}
