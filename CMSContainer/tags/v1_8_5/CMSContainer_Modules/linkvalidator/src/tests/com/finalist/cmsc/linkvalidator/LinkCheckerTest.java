package com.finalist.cmsc.linkvalidator;

import org.apache.commons.httpclient.*;

import com.finalist.util.http.HttpUtil;

import junit.framework.TestCase;


public class LinkCheckerTest extends TestCase {

   private LinkChecker checker;

   @Override
   protected void setUp() throws Exception {
      super.setUp();
      HttpClient cl = HttpUtil.getHttpClient(null, false, 3000, 0);
      checker = new LinkChecker(cl);
   }
   
   public void testIsValid() {
      // If we can't reach this then the server is not allowed to go online or the internet is down.
      assertTrue(checker.isValid("http://www.google.com"));
      // dns name not available
      assertFalse(checker.isValid("http://doesnotexist.cmscontainer.org/"));
      // page not available 
      assertFalse(checker.isValid("http://www.cmscontainer.org/doesnotexist"));
      // 500 error
      assertFalse(checker.isValid("http://www.cmscontainer.org/error/throwerror.jsp"));
      // redirect
      assertTrue(checker.isValid("http://demo.cmscontainer.org"));
      // redirect to querystring (CMSC-1164)
      assertTrue(checker.isValid("http://www.c2000.nl")); // http://www.c2000.nl/?menuitemID[]=104
      // secure site with self-signed certificate
      assertTrue(checker.isValid("https://scm.mmbase.org"));
   }
   
}

