package com.finalist.cmsc.community.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.jasig.cas.client.util.AbstractConfigurationFilter;
import org.jasig.cas.client.validation.Saml11TicketValidationFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Usage:
    <filter>
        <filter-name>CAS Validation Filter</filter-name>
        <!-- Instead of org.jasig.cas.client.validation.Saml11TicketValidationFilter-->
        <filter-class>com.finalist.cmsc.community.filter.MultiSiteTicketSamlValidationWrapperFilter</filter-class>
        <init-param>
        	<!-- Oh, maybe this has to be multi-configurable too, I don't think so though since this does not do redirects??? -->
            <param-name>casServerUrlPrefix</param-name>
            <param-value>https://www.nai.nl/single-sign-on</param-value>
          </init-param>
        <init-param>
        	<!-- Search for "," below for how this works -->
            <param-name>serverName</param-name>
            <param-value>https://www.nai.nl,https://en.nai.nl,https://to.nai.nl</param-value>
        </init-param>
        <init-param>
            <param-name>redirectAfterValidation</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    
    It will also pickup serverNames from the context.xml file
 */

public class MultiSiteTicketSamlValidationWrapperFilter extends AbstractConfigurationFilter {
    
    private Map<String, Filter> wrappedFilters = new HashMap<String, Filter>();

    protected static final Log log = LogFactory.getLog(MultiSiteTicketSamlValidationWrapperFilter.class);

    public void destroy() {
        for (Filter wrappedFilter: wrappedFilters.values()) {
            wrappedFilter.destroy();
        }
        
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURL = request.getRequestURL().toString();
        boolean filterWasFound = false;
        for (Map.Entry<String, Filter> wrappedFilterEntry: wrappedFilters.entrySet()) {
        	String serverName = wrappedFilterEntry.getKey();
            if (requestURL.contains(serverName)) {
                log.debug("servername found for requestURL: "+requestURL+": "+serverName);

            	// Woohoo, found filter, let it do all the work
                filterWasFound = true;
                wrappedFilterEntry.getValue().doFilter(servletRequest, servletResponse, chain);
                break;
            }
        }
        if (!filterWasFound) {
            log.warn("no servername found for requestURL: "+requestURL+" ignoring request");
//FP: I think these should be bounced, because we are screwing up security otherwise
            // Hmm, we have to do the chaining then I guess
//            chain.doFilter(servletRequest, servletResponse);
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    	// Possible improvements here:
    	// - Might as well use a special serverNames property (only one line difference)
    	// - Would be nice to make instantiated validation filter configurable classname
        String serverNames = getPropertyFromInitParams(filterConfig, "serverName", null);
        
        log.info("starting up with serverNames: "+serverNames);
        
    	if ((serverNames != null) && serverNames.length() > 0) {
            // Set up a number of filters (could be just one)
            for (String serverName: serverNames.split(",")) {
                Filter ticketValidationFilter = new Saml11TicketValidationFilter();
                ticketValidationFilter.init(new WrappedFilterConfig(filterConfig, serverName));
                wrappedFilters.put(serverName, ticketValidationFilter);
            }
        }
        
    }
    
    private static class WrappedFilterConfig implements FilterConfig {

        private FilterConfig wrappedFilterConfig;
        private String serverNameForThisFilter;
		public WrappedFilterConfig(FilterConfig filterConfig, String serverName) {
		    this.wrappedFilterConfig = filterConfig;
		    this.serverNameForThisFilter = serverName;
		}

		public String getFilterName() {
		    // Hmm, so multiple filters with this name
		    return wrappedFilterConfig.getFilterName();
		}

		// Important: The whole basis of this working is that AbstractConfigurationFilter.getPropertyFromInitParams
		// Checks the init params first
		public String getInitParameter(String parameterName) {
			if ("serverName".equals(parameterName)) {
			    return serverNameForThisFilter;
			} else {
			    return wrappedFilterConfig.getInitParameter(parameterName);
			}
		}

		@SuppressWarnings("unchecked")
		public Enumeration getInitParameterNames() {
		    // Let's hope nobody really uses this.... (serverName could be missing)
		    return wrappedFilterConfig.getInitParameterNames();
		}

		public ServletContext getServletContext() {
		    return wrappedFilterConfig.getServletContext();
		}
        
    }

}
