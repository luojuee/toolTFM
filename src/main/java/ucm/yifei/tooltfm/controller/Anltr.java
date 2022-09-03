package ucm.yifei.tooltfm.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ucm.yifei.tooltfm.model.CfgGenerator;
import ucm.yifei.tooltfm.model.TRpath;
import ucm.yifei.tooltfm.utils.FileAndDeleteFinder;
import ucm.yifei.tooltfm.visitors.Java8CustomVisitor;

import java.io.IOException;

@RestController
@RequestMapping("/anltr")
public class Anltr {
    CfgGenerator cfg = new CfgGenerator();
    Java8CustomVisitor visitor = new Java8CustomVisitor();
    TRpath tr = new TRpath();

    @ResponseBody
    @RequestMapping("/getData")
    public String getData(@RequestParam(name = "gra") String gra){
        cfg = new CfgGenerator();
        visitor = cfg.analyze(gra);
        cfg.createCFGGraph(visitor);

        JSONObject jsonObject = new JSONObject();
        tr = new TRpath(visitor);
        tr.generateTRpath();

        String t = tr.printTRpath();
        jsonObject.put("path",t);

        cfg.createUserJavaFile(visitor,gra);

        return jsonObject.toString();
    }

    @ResponseBody
    @RequestMapping("/getCover")
    public String getCover(@RequestParam(name = "gra") String gra,@RequestParam(name = "code") String code) throws IOException {
        String result = cfg.calculateCoverage(visitor, tr, code);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("coverResult", result);

        FileAndDeleteFinder.tempFileDelete();
        return jsonObject.toString();
    }
    @ResponseBody
    @RequestMapping("/getCoverEVO")
    public String getCoverEVO(@RequestParam(name = "gra") String gra,@RequestParam(name = "code") String code) throws IOException {
        FileAndDeleteFinder.tempFileDelete();
        String result = cfg.calculateCP(visitor,"EVO", tr);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("coverResult", result);

        FileAndDeleteFinder.tempFileDelete();
        return jsonObject.toString();
    }
    @ResponseBody
    @RequestMapping("/getCoverDiffblue")
    public String getCoverDiffblue(@RequestParam(name = "gra") String gra,@RequestParam(name = "code") String code) throws IOException {
        FileAndDeleteFinder.tempFileDelete();
        String result = cfg.calculateCP(visitor, "Diffblue", tr);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("coverResult", result);

        FileAndDeleteFinder.tempFileDelete();
        return jsonObject.toString();
    }
}
