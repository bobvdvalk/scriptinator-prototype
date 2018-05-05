/*
 * Copyright © 2018 Scriptinator (support@scriptinator.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.chapp.scriptinator.webcontrollers;

import io.chapp.scriptinator.model.Webhook;
import io.chapp.scriptinator.services.ScriptService;
import io.chapp.scriptinator.services.WebhookArgumentParser;
import io.chapp.scriptinator.services.WebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping("webhook")
public class WebhookHandlerWebController {
    private final ScriptService scriptService;
    private final WebhookArgumentParser webhookArgumentParser;
    private final WebhookService webhookService;

    public WebhookHandlerWebController(ScriptService scriptService, WebhookArgumentParser webhookArgumentParser, WebhookService webhookService) {
        this.scriptService = scriptService;
        this.webhookArgumentParser = webhookArgumentParser;
        this.webhookService = webhookService;
    }

    @RequestMapping("{webhookUuid}")
    @ResponseBody
    public ResponseEntity<WebhookResponse> trigger(@PathVariable String webhookUuid, HttpServletRequest request) throws IOException {
        Webhook webhook = webhookService.getByUuid(webhookUuid);
        scriptService.runWebhooked(webhook, webhookArgumentParser.parseRequest(request));

        boolean ackResponse = webhook.getSendResponse();
        return new ResponseEntity<>(
                ackResponse ? new WebhookResponse(true) : null,
                ackResponse ? HttpStatus.OK : HttpStatus.NO_CONTENT
        );
    }

    private static class WebhookResponse {
        private final boolean success;

        private WebhookResponse(boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}
