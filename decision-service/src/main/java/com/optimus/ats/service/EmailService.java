package com.optimus.ats.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Template;

@ApplicationScoped
public class EmailService {
    static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Inject Mailer mailer; 

    @Inject
    Template statusEmail;

    @Inject
    Template statusChangeEmail;

    @ConfigProperty(name = "mail.to.list")
    String mailToList;

    public boolean sendEmailNotification(String empEmail, String empName, String csEmpId ){
        String[] emails = mailToList.split(",");
              
        String msg = statusEmail.data("name",empName).data("id",csEmpId).render();
        try{
            mailer.send(Mail.withHtml(emails[0], "[Alert] Approval Request - Submission", msg).addTo(empEmail));
        }catch(Exception e){
            log.error("Error in sendEmailNotification", e);
        }
        
        
        return true;
    }

    public boolean sendActionUpdateEmail(String empEmail, String empName, String csEmpId, String status ){
        String[] emails = mailToList.split(",");
              
        String msg = statusChangeEmail.data("name",empName).data("id",csEmpId).data("status",status).render();
       
        try{
            mailer.send(Mail.withHtml(emails[0], "[Alert] Status Change Update", msg).addTo(empEmail));
        }catch(Exception e){
            log.error("Error in sendEmailNotification", e);
        }
        return true;
    }


}
