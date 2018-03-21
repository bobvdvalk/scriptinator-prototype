package io.chapp.scriptinator.webcontrollers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/docs")
public class DocsWebController {

    @RequestMapping
    @ResponseBody // This is not a template
    public String showDocsMessage() {
        return "It seems you were looking for the documentation but it was not deployed. Please check https://scriptinator.io/docs for the latest version.";
    }
}
