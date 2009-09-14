/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.portlets.flash;

import javax.portlet.ActionRequest;

import com.finalist.cmsc.portlets.ContentPortlet;

/**
 * This portlet adds 'movieWidth' and 'movieHeight' parameters to the regular
 * ContentPortlet. These are used in the view to determine the width and height
 * of the flash SWF file.
 *
 * @author Jasper Stroomer
 */
public class FlashPortlet extends ContentPortlet {
	private static final String MOVIE_WIDTH = "movieWidth";
	private static final String MOVIE_HEIGHT = "movieHeight";

	/**
	 * Persists the edit_default parameters on the request.
	 *
	 * @param request
	 *            the request which contains the parameters to be saved.
	 * @param portletId
	 *            the id of the portlet to save the parameters to.
	 */
	@Override
	protected void saveParameters(ActionRequest request, String portletId) {
		setPortletParameter(portletId, MOVIE_WIDTH, request.getParameter(MOVIE_WIDTH));
		setPortletParameter(portletId, MOVIE_HEIGHT, request.getParameter(MOVIE_HEIGHT));
		super.saveParameters(request, portletId);
	}
}