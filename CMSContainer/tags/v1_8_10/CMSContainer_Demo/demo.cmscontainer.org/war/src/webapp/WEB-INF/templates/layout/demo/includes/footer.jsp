<div id="footer">
   <div class="content">
      <span class="space">|</span>&nbsp;
      <a href="<cmsc:link dest="${site.urlfragment}" />">home</a>&nbsp;<span class="space">|</span>&nbsp;
      <cmsc:link dest="${site.urlfragment}/contact" var="contactPage" />
      <c:if test="$(not empty contactPage)"><a href="${contactPage}">contact</a>&nbsp;<span class="space">|</span>&nbsp;</c:if>
      <cmsc:link dest="${site.urlfragment}/disclaimer" var="disclaimerPage" />
      <c:if test="$(not empty disclaimerPage)"><a href="${disclaimerPage}">disclaimer</a>&nbsp;<span class="space">|</span>&nbsp;</c:if>      
      <cmsc:link dest="${site.urlfragment}/copyright" var="copyrightPage" />
      <c:if test="$(not empty copyrightPage)"><a href="${copyrightPage}">copyright</a>&nbsp;<span class="space">|</span>&nbsp;</c:if>      
      <cmsc:link dest="${site.urlfragment}/sitemap" var="sitemapPage" />
      <c:if test="$(not empty sitemapPage)"><a href="${sitemapPage}">sitemap</a>&nbsp;<span class="space">|</span>&nbsp;</c:if>
   </div>
</div>