package com.finalist.cmsc.knownvisitor.ntlm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.*;
import javax.servlet.http.*;

import jcifs.Config;
import jcifs.UniAddress;
import jcifs.http.NtlmSsp;
import jcifs.smb.*;
import jcifs.util.Base64;

import org.apache.commons.lang.StringUtils;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.knownvisitor.*;

/**
 * @author Freek Punt, Finalist IT Group
 * @author Jeoffrey Bakker, Finalist IT Group
 */
public class NtlmVisitorFilter extends VisitorFilter {

   private static final Logger log = Logging.getLoggerInstance(NtlmVisitorFilter.class);

   private static final String NTLM_HTTP_AUTH_USERNAME = "NtlmHttpAuthUsername";
   private static final String NTLM_HTTP_AUTH = "NtlmHttpAuth";


   public void init(FilterConfig filterConfig) {
      super.init(filterConfig);
      /*
       * Set jcifs properties we know we want; soTimeout and cachePolicy to
       * 10min.
       */
      Config.setProperty("jcifs.smb.client.soTimeout", "300000");
      Config.setProperty("jcifs.netbios.cachePolicy", "1200");
   }

   /**
    * Negotiate password hashes with MSIE clients using NTLM SSP
    * 
    * @param req The servlet request
    * @param resp The servlet response
    * @return True if the negotiation is complete, otherwise false
    */
   protected boolean negotiate(HttpServletRequest req, HttpServletResponse resp)
         throws IOException, ServletException {
      String msg = req.getHeader("Authorization");
      NtlmKnownVisitorModule module = (NtlmKnownVisitorModule) KnownVisitorModule.getInstance();
      boolean offerBasic = module.offerBasic(req);
      boolean offerNtlm = module.offerNtlm(req);
      log.debug("Message: " + msg);
      
      NtlmPasswordAuthentication ntlm = null;
      
      if (msg != null && (offerNtlm && msg.startsWith("NTLM ") || (offerBasic && msg.startsWith("Basic ")))) {
         String domainController = getDomainController();
         UniAddress dc = UniAddress.getByName(domainController, true);
         if (msg.startsWith("NTLM ")) {
            log.debug("Message starts with NTLM.");
            HttpSession ssn = req.getSession();
            byte[] challenge = SmbSession.getChallenge(dc);

            if ((ntlm = NtlmSsp.authenticate(req, resp, challenge)) == null) {
               return false;
            }
            /* negotiation complete,*/
            log.debug("negotiation complete");
         }
         else {
            ntlm = createBasicAuthToken(msg);
         }
         try {
            SmbSession.logon(dc, ntlm);
            if (log.isDebugEnabled()) {
               log.debug("NtlmHttpFilter: " + ntlm + " successfully authenticated against " + dc);
            }
         }
         catch (SmbAuthException sae) {
            if (log.isServiceEnabled()) {
               log.service("NtlmHttpFilter: " + ntlm.getName() + ": 0x"
                     + jcifs.util.Hexdump.toHexString(sae.getNtStatus(), 8) + ": " + sae);
            }
            if (sae.getNtStatus() == NtStatus.NT_STATUS_ACCESS_VIOLATION) {
               /*
                * Server challenge no longer valid for externally supplied
                * password hashes.
                */
               HttpSession ssn = req.getSession(false);
               if (ssn != null) {
                  ssn.removeAttribute(NTLM_HTTP_AUTH);
               }
            }
            else{
               if (sae.getNtStatus() != NtStatus.NT_STATUS_LOGON_FAILURE) {
                  log.info("SmbAuthException" + sae.getNtStatus() + " : " + sae.getMessage());
               }
            }
            sendChallenge(resp, module, offerBasic, offerNtlm);
            return false;
         }
         req.getSession().setAttribute(NTLM_HTTP_AUTH, ntlm);
         String username = ntlm.getUsername();

         addUserCookie(resp, username);

         justLoggedIn(req, username);
      }
      else {
         String cookie = getUserCookie(req.getCookies());
         if (cookie != null) {
            justLoggedIn(req, cookie);
            return true;
         }
         else {
            HttpSession ssn = req.getSession(false);
            if (ssn == null || (ntlm = (NtlmPasswordAuthentication) ssn.getAttribute(NTLM_HTTP_AUTH)) == null) {
               log.debug("Not NTLM authenticated, starting authentication.");
               sendChallenge(resp, module, offerBasic, offerNtlm);
               return false;
            }
         }
      }

      return ntlm != null;
   }

   private void addUserCookie(HttpServletResponse resp, String username) {
      Cookie cookie = new Cookie(NTLM_HTTP_AUTH_USERNAME, username);
      cookie.setMaxAge(0x7fffffff);
      resp.addCookie(cookie);
   }

   private String getUserCookie(Cookie cookies[]) {
      if (cookies != null) {
         for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            if (NTLM_HTTP_AUTH_USERNAME.equals(cookie.getName())) { 
               return cookie.getValue();
            }
         }
      }
      return null;
   }

   
   private void sendChallenge(HttpServletResponse resp, NtlmKnownVisitorModule module,
         boolean offerBasic, boolean offerNtlm) throws IOException {
      if (offerNtlm) {
         resp.setHeader("WWW-Authenticate", "NTLM");
      }
      if (offerBasic) {
         String realm = module.getRealm();
         if (StringUtils.isBlank(realm)) {
            realm = "CMS Container";
         }
         resp.addHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
      }
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      resp.setContentLength(0);
      resp.flushBuffer();
   }

   private NtlmPasswordAuthentication createBasicAuthToken(String msg)
         throws UnsupportedEncodingException {
      NtlmPasswordAuthentication ntlm;
      log.debug("Message starts with Basic.");
      String auth = new String(Base64.decode(msg.substring(6)), "US-ASCII");
      int index = auth.indexOf(':');
      String user = (index != -1) ? auth.substring(0, index) : auth;
      String password = (index != -1) ? auth.substring(index + 1) : "";
      index = user.indexOf('\\');
      if (index == -1)
         index = user.indexOf('/');
      
      String domain;
      if (index == -1) {
          domain = getDomain();
      } else {
          domain = user.substring(0, index);
      }

      user = (index != -1) ? user.substring(index + 1) : user;
      ntlm = new NtlmPasswordAuthentication(domain, user, password);
      return ntlm;
   }

   private String getDomainController() {
      return ((NtlmKnownVisitorModule) KnownVisitorModule.getInstance()).getDomainController();
   }

   private String getDomain() {
      return ((NtlmKnownVisitorModule) KnownVisitorModule.getInstance()).getDomain();
   }

}
