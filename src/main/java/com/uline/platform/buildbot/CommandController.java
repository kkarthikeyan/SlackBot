package com.uline.platform.buildbot;

import net.gpedro.integrations.slack.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommandController {
    final Logger logger = LoggerFactory.getLogger(CommandController.class);

    @PostMapping("/buildbot/command")
    public String processCommand(@ModelAttribute CommandRequest commandRequest) {
        if(!isAuthorised(commandRequest)) {
            return new SlackMessage("Error, not authorized").prepare().toString(); //TODO: HTTP status code to 403
        }
        
        if(isApprovalRequired(commandRequest)) {
            sendApprovalRequest(commandRequest);
            return new SlackMessage("Your request is pending approval").prepare().toString(); //TODO: add approver channel details
        }
        
        return executeCommand(commandRequest).prepare().toString();
    }

    private SlackMessage executeCommand(CommandRequest commandRequest) {
        SlackMessage msg = new SlackMessage("Work in progress, try later " +
                "<@" + commandRequest.getUser_name() + "|" + commandRequest.getUser_id() + ">");
        msg.addAttachments(new SlackAttachment(commandRequest.toString()).setText(commandRequest.toString()));

        return msg;
    }

    private void sendApprovalRequest(CommandRequest commandRequest) {
        SlackMessage msg = new SlackMessage("<@" + commandRequest.getUser_name() + "|" + commandRequest.getUser_id() + "> has requsted");

        msg.setChannel("#kartiktest");
        msg.addAttachments(new SlackAttachment(commandRequest.toString()).setText(commandRequest.toString()));

        SlackAction approveBtn = new SlackAction("approval","Approve", SlackActionType.BUTTON,"approved");
        approveBtn.setStyle(SlackActionStyle.PRIMARY);
        SlackAction rejecteBtn = new SlackAction("approval","Reject", SlackActionType.BUTTON,"rejected");
        rejecteBtn.setStyle(SlackActionStyle.DANGER);

        SlackAttachment approval = new SlackAttachment("Issue displaying approval tasks")
                .setText("Provide your approval")
                .setColor("#3AA3E3")
                .setCallbackId("tempcallbackId")
                .addAction(approveBtn)
                .addAction(rejecteBtn);

        msg.addAttachments(approval);

        logger.info(msg.prepare().toString());

        final String webHookUrl = "https://hooks.slack.com/services/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";
        SlackApi slack = new SlackApi(webHookUrl);
        slack.call(msg);
    }

    private boolean isApprovalRequired(CommandRequest commandRequest) {
        return true;
    }

    private boolean isAuthorised(CommandRequest commandRequest) {
        if("8DpmO1I9oQeElBxmgmnMR01q".equals(commandRequest.getToken())) {
            return true;
        }

        return false;
    }
}
