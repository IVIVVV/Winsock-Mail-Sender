import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.activation.MailcapCommandMap;
import javax.activation.CommandMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JFileChooser;

public class Action {

    private Screen gui;

    public Action() {
        this.gui = new Screen();
        prepareGui();
    }

    public Screen getGui() {
        return gui;
    }

    public static Session Login(String username, String pass) {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.port", 465);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.port", 465);

        PasswordAuthentication Auth = new PasswordAuthentication(username, pass);
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return Auth;
            }
        });
        return session;
    }

    public void prepareGui() {
        gui.getBtm_chooseFile().setActionCommand("chooseFile");
        gui.getBtm_chooseFile().addActionListener(new ButtonClick());
        gui.getBtn_send().setActionCommand("send");
        gui.getBtn_send().addActionListener(new ButtonClick());
    }

    public File chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        File f = null;
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            f = fileChooser.getSelectedFile();
        }
        return f;
    }

    private void showFile(File f) {
        gui.getLb_file().setText(f.getPath());
    }

    class ButtonClick implements ActionListener {

        File file;

        private String[] getListEmail(String str) {
            if (str.equals("")) {
                return null;
            } else if (!str.contains(",")) {
                String[] strArr = {str};
                return strArr;
            }
            return str.split(" ");
        }

        private void SendAction() {
            
            //fixing multipart error
            Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
            mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822"); 
            
            String username = gui.getTf_username().getText().trim();
            String pass = gui.getTf_password().getText().trim();
            String[] listTo = getListEmail(gui.getTf_to().getText().trim());
            String[] listCc = getListEmail(gui.getTf_cc().getText().trim());
            String[] listBcc = getListEmail(gui.getTf_bcc().getText().trim());
            String filePath = gui.getLb_file().getText().trim();
            String mess = gui.getTf_message().getText();
            String subject = gui.getTf_subject().getText();
            Session sess = Login(username, pass);
            SendMail sender = new SendMail(sess);
            System.out.println(filePath);
            sender.sendEmail(listTo, listCc, listBcc, mess, filePath, subject);
        }
        
        public void actionPerformed(ActionEvent ae) {
            if (ae.getActionCommand().equals("chooseFile")) {
                file = chooseFile();
                if (file != null) {
                    showFile(file);
                }
            } else if (ae.getActionCommand().equals("send")) {
                SendAction();
            }
        }
        
    }
    
}
